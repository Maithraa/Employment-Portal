import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for Placement Offers, Candidate Performance Cards, and Analytics.
 */
public class AnalyticsDAO {

    public void displayCandidateScoreCard(String seekerId) throws SQLException {
        String sql = """
            SELECT s.seeker_id, s.full_name, s.qualification, s.skills,
                   j.job_id, j.job_title, e.company_name, j.offered_salary_lpa AS base_ctc,
                   a.application_id, a.applied_date, a.application_status,
                   iv.technical_score, iv.coding_score, iv.communication_score, iv.hr_score,
                   iv.interview_result, iv.interviewer_remarks,
                   po.offered_ctc_lpa, po.joining_date, po.offer_status
            FROM job_seekers s
            JOIN applications a ON a.seeker_id = s.seeker_id
            JOIN jobs j ON j.job_id = a.job_id
            JOIN employers e ON e.employer_id = j.employer_id
            LEFT JOIN interview_evaluations iv ON iv.application_id = a.application_id
            LEFT JOIN placement_offers po ON po.application_id = a.application_id
            WHERE s.seeker_id = ?
            ORDER BY a.application_id
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, seekerId);
            try (ResultSet rs = ps.executeQuery()) {
                boolean headerPrinted = false;
                double totalScoreSum = 0;
                int scoredCount = 0;

                while (rs.next()) {
                    if (!headerPrinted) {
                        System.out.println("\n==========================================================================================");
                        System.out.println("                   CANDIDATE PERFORMANCE & EVALUATION MARKSHEET                          ");
                        System.out.println("==========================================================================================");
                        System.out.println("Candidate ID    : " + rs.getString("seeker_id"));
                        System.out.println("Name            : " + rs.getString("full_name"));
                        System.out.println("Degree          : " + rs.getString("qualification"));
                        System.out.println("Skills          : " + rs.getString("skills"));
                        System.out.println("------------------------------------------------------------------------------------------");
                        headerPrinted = true;
                    }

                    double tech = rs.getDouble("technical_score");
                    double code = rs.getDouble("coding_score");
                    double comm = rs.getDouble("communication_score");
                    double hr = rs.getDouble("hr_score");
                    double weighted = (tech * 0.40) + (code * 0.30) + (comm * 0.15) + (hr * 0.15);
                    String grade = calculateGrade(weighted);

                    System.out.printf("App #%d | Role: %s (%s)%n",
                            rs.getInt("application_id"),
                            rs.getString("job_title"),
                            rs.getString("company_name"));
                    System.out.printf("  Scores  : Tech: %.1f | Coding: %.1f | Comm: %.1f | HR: %.1f%n",
                            tech, code, comm, hr);
                    System.out.printf("  Overall : Weighted Score: %.2f/100 | Grade: %s | Result: %s%n",
                            weighted, grade, rs.getString("interview_result"));

                    if (rs.getObject("offered_ctc_lpa") != null) {
                        System.out.printf("  Offer   : CTC: %.2f LPA | Joining: %s | Status: %s%n",
                                rs.getDouble("offered_ctc_lpa"),
                                rs.getDate("joining_date"),
                                rs.getString("offer_status"));
                    } else {
                        System.out.println("  Offer   : No placement offer issued yet.");
                    }
                    System.out.println("------------------------------------------------------------------------------------------");

                    if (weighted > 0) {
                        totalScoreSum += weighted;
                        scoredCount++;
                    }
                }

                if (!headerPrinted) {
                    System.out.println("No application records found for candidate ID: " + seekerId);
                } else {
                    double avgScore = scoredCount > 0 ? (totalScoreSum / scoredCount) : 0.0;
                    System.out.printf("Cumulative Candidate Performance Index (CPI): %.2f/100 | Overall Standing: %s%n",
                            avgScore, calculateGrade(avgScore));
                    System.out.println("==========================================================================================");
                }
            }
        }
    }

    public boolean issuePlacementOffer(int applicationId, double offeredCtc, String joiningDate) throws SQLException {
        String sqlOffer = """
            INSERT INTO placement_offers (application_id, offered_ctc_lpa, joining_date, offer_status)
            VALUES (?, ?, ?, 'OFFERED')
            ON DUPLICATE KEY UPDATE
                offered_ctc_lpa = VALUES(offered_ctc_lpa),
                joining_date = VALUES(joining_date),
                offer_status = VALUES(offer_status)
            """;
        String sqlApp = "UPDATE applications SET application_status = 'SELECTED' WHERE application_id = ?";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement psOffer = con.prepareStatement(sqlOffer);
                 PreparedStatement psApp = con.prepareStatement(sqlApp)) {

                psOffer.setInt(1, applicationId);
                psOffer.setDouble(2, offeredCtc);
                psOffer.setString(3, joiningDate);
                psOffer.executeUpdate();

                psApp.setInt(1, applicationId);
                psApp.executeUpdate();

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

    public void displayIndustrySummary() throws SQLException {
        String sql = """
            SELECT e.industry,
                   COUNT(DISTINCT e.employer_id) AS total_companies,
                   COUNT(DISTINCT j.job_id) AS total_openings,
                   COUNT(a.application_id) AS total_applicants,
                   SUM(CASE WHEN a.application_status = 'SELECTED' THEN 1 ELSE 0 END) AS total_selections,
                   AVG(po.offered_ctc_lpa) AS avg_placed_ctc,
                   AVG( (iv.technical_score*0.4 + iv.coding_score*0.3 + iv.communication_score*0.15 + iv.hr_score*0.15) ) AS avg_interview_score
            FROM employers e
            LEFT JOIN jobs j ON j.employer_id = e.employer_id
            LEFT JOIN applications a ON a.job_id = j.job_id
            LEFT JOIN interview_evaluations iv ON iv.application_id = a.application_id
            LEFT JOIN placement_offers po ON po.application_id = a.application_id
            GROUP BY e.industry
            ORDER BY total_openings DESC
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n================================ INDUSTRY-WISE PLACEMENT & RECRUITMENT SUMMARY ================================");
            System.out.printf("%-24s | %-10s | %-10s | %-12s | %-10s | %-14s | %-10s%n",
                    "INDUSTRY SECTOR", "COMPANIES", "VACANCIES", "APPLICANTS", "SELECTED", "AVG CTC (LPA)", "AVG SCORE");
            System.out.println("--------------------------------------------------------------------------------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                double avgCtc = rs.getDouble("avg_placed_ctc");
                double avgScore = rs.getDouble("avg_interview_score");

                System.out.printf("%-24s | %-10d | %-10d | %-12d | %-10d | %-14s | %-10s%n",
                        rs.getString("industry"),
                        rs.getInt("total_companies"),
                        rs.getInt("total_openings"),
                        rs.getInt("total_applicants"),
                        rs.getInt("total_selections"),
                        rs.getObject("avg_placed_ctc") != null ? String.format("%.2f LPA", avgCtc) : "N/A",
                        rs.getObject("avg_interview_score") != null ? String.format("%.2f", avgScore) : "N/A"
                );
            }
            if (!found) {
                System.out.println("No analytics data available.");
            }
            System.out.println("==============================================================================================================");
        }
    }

    private static String calculateGrade(double score) {
        if (score >= 90.0) return "O (Outstanding)";
        if (score >= 80.0) return "A+ (Excellent)";
        if (score >= 70.0) return "A (Very Good)";
        if (score >= 60.0) return "B+ (Good)";
        if (score >= 50.0) return "B (Above Average)";
        if (score >= 40.0) return "C (Pass)";
        return "RA (Reappear/Not Cleared)";
    }
}
