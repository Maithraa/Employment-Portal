import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for Interview Rounds and Assessment Scores.
 * Corresponds to Continuous Evaluation & Marksheet features in the academic reference model.
 */
public class InterviewDAO {

    public void displayInterviewReport() throws SQLException {
        String sql = """
            SELECT e.interview_id, a.application_id, s.seeker_id, s.full_name,
                   c.company_name, j.job_title,
                   e.technical_score, e.coding_score, e.communication_score, e.hr_score,
                   e.interviewer_remarks, e.interview_result, a.application_status
            FROM interview_evaluations e
            JOIN applications a ON a.application_id = e.application_id
            JOIN job_seekers s ON s.seeker_id = a.seeker_id
            JOIN jobs j ON j.job_id = a.job_id
            JOIN employers c ON c.employer_id = j.employer_id
            ORDER BY a.application_id
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n=================================== INTERVIEW EVALUATION REPORT ===================================");
            System.out.printf("%-6s | %-16s | %-16s | %-18s | %-7s | %-12s | %-15s%n",
                    "APP ID", "CANDIDATE", "COMPANY", "ROLE", "SCORE", "RESULT", "STATUS");
            System.out.println("---------------------------------------------------------------------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                double tech = rs.getDouble("technical_score");
                double code = rs.getDouble("coding_score");
                double comm = rs.getDouble("communication_score");
                double hr = rs.getDouble("hr_score");

                // Weighted evaluation formula: Tech 40%, Coding 30%, Comm 15%, HR 15%
                double totalWeighted = (tech * 0.40) + (code * 0.30) + (comm * 0.15) + (hr * 0.15);

                String recommendation;
                if (totalWeighted >= 80.0) {
                    recommendation = "Direct Placement";
                } else if (totalWeighted >= 65.0) {
                    recommendation = "Review Required";
                } else if (totalWeighted > 0.0) {
                    recommendation = "Needs Retraining";
                } else {
                    recommendation = "Pending Assessment";
                }

                System.out.printf("%-6d | %-16s | %-16s | %-18s | %-7.2f | %-12s | %-15s%n",
                        rs.getInt("application_id"),
                        truncate(rs.getString("full_name"), 16),
                        truncate(rs.getString("company_name"), 16),
                        truncate(rs.getString("job_title"), 18),
                        totalWeighted,
                        rs.getString("interview_result"),
                        recommendation
                );
            }
            if (!found) {
                System.out.println("No interview records evaluated yet.");
            }
            System.out.println("===================================================================================================");
        }
    }

    public boolean recordEvaluation(int applicationId, double tech, double code, double comm,
                                    double hr, String remarks, String result) throws SQLException {
        String sql = """
            INSERT INTO interview_evaluations (application_id, technical_score, coding_score,
                                               communication_score, hr_score, interviewer_remarks, interview_result)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                technical_score = VALUES(technical_score),
                coding_score = VALUES(coding_score),
                communication_score = VALUES(communication_score),
                hr_score = VALUES(hr_score),
                interviewer_remarks = VALUES(interviewer_remarks),
                interview_result = VALUES(interview_result)
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, applicationId);
            ps.setDouble(2, tech);
            ps.setDouble(3, code);
            ps.setDouble(4, comm);
            ps.setDouble(5, hr);
            ps.setString(6, remarks);
            ps.setString(7, result);

            return ps.executeUpdate() > 0;
        }
    }

    private static String truncate(String text, int max) {
        if (text == null) return "-";
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }
}
