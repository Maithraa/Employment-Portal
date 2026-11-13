import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Data Access Object for Job Applications and Application Lifecycle.
 */
public class ApplicationDAO {

    public boolean applyForJob(String seekerId, String jobId, String coverNote) throws SQLException {
        String insertApp = """
            INSERT INTO applications (seeker_id, job_id, cover_note, application_status)
            VALUES (?, ?, ?, 'APPLIED')
            """;
        String initEval = """
            INSERT INTO interview_evaluations (application_id, interview_result)
            VALUES (?, 'PENDING')
            """;

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement psApp = con.prepareStatement(insertApp, Statement.RETURN_GENERATED_KEYS)) {
                psApp.setString(1, seekerId);
                psApp.setString(2, jobId);
                psApp.setString(3, coverNote != null && !coverNote.isBlank() ? coverNote : "Direct portal application");
                psApp.executeUpdate();

                int generatedAppId = -1;
                try (ResultSet rs = psApp.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedAppId = rs.getInt(1);
                    }
                }

                if (generatedAppId != -1) {
                    try (PreparedStatement psEval = con.prepareStatement(initEval)) {
                        psEval.setInt(1, generatedAppId);
                        psEval.executeUpdate();
                    }
                }

                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public void displayAllApplications() throws SQLException {
        String query = """
            SELECT a.application_id, a.applied_date, s.seeker_id, s.full_name,
                   j.job_id, j.job_title, e.company_name, a.application_status
            FROM applications a
            JOIN job_seekers s ON s.seeker_id = a.seeker_id
            JOIN jobs j ON j.job_id = a.job_id
            JOIN employers e ON e.employer_id = j.employer_id
            ORDER BY a.application_id DESC
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n======================================= JOB APPLICATIONS =======================================");
            System.out.printf("%-6s | %-12s | %-18s | %-20s | %-16s | %-20s%n",
                    "APP ID", "DATE", "CANDIDATE", "ROLE APPLIED", "COMPANY", "STATUS");
            System.out.println("------------------------------------------------------------------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-6d | %-12s | %-18s | %-20s | %-16s | %-20s%n",
                        rs.getInt("application_id"),
                        rs.getDate("applied_date"),
                        truncate(rs.getString("full_name"), 18),
                        truncate(rs.getString("job_title"), 20),
                        truncate(rs.getString("company_name"), 16),
                        rs.getString("application_status")
                );
            }
            if (!found) {
                System.out.println("No applications submitted yet.");
            }
            System.out.println("================================================================================================");
        }
    }

    public boolean updateApplicationStatus(int applicationId, String newStatus) throws SQLException {
        String query = "UPDATE applications SET application_status = ? WHERE application_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, newStatus);
            ps.setInt(2, applicationId);
            return ps.executeUpdate() > 0;
        }
    }

    public void displayApplicationsBySeeker(String seekerId) throws SQLException {
        String query = """
            SELECT a.application_id, j.job_title, e.company_name, a.applied_date,
                   a.application_status, o.offered_ctc_lpa, o.offer_status
            FROM applications a
            JOIN jobs j ON j.job_id = a.job_id
            JOIN employers e ON e.employer_id = j.employer_id
            LEFT JOIN placement_offers o ON o.application_id = a.application_id
            WHERE a.seeker_id = ?
            ORDER BY a.application_id
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, seekerId);
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n--- Applications for Candidate: " + seekerId + " ---");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    String offerInfo = rs.getObject("offered_ctc_lpa") != null
                            ? String.format(" [OFFER: %.2f LPA - %s]", rs.getDouble("offered_ctc_lpa"), rs.getString("offer_status"))
                            : "";
                    System.out.printf("App #%d | %s at %s | Applied: %s | Status: %s%s%n",
                            rs.getInt("application_id"),
                            rs.getString("job_title"),
                            rs.getString("company_name"),
                            rs.getDate("applied_date"),
                            rs.getString("application_status"),
                            offerInfo
                    );
                }
                if (!found) {
                    System.out.println("No applications found for this candidate.");
                }
            }
        }
    }

    public int findApplicationId(String seekerId, String jobId) throws SQLException {
        String query = "SELECT application_id FROM applications WHERE seeker_id = ? AND job_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, seekerId);
            ps.setString(2, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("application_id");
                }
            }
        }
        return -1;
    }

    private static String truncate(String text, int max) {
        if (text == null) return "-";
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }
}
