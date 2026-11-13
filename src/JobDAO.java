import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for Job Postings and Openings.
 */
public class JobDAO {

    public void displayAllJobs() throws SQLException {
        String query = """
            SELECT j.job_id, e.company_name, j.job_title, j.job_type, j.min_experience,
                   j.offered_salary_lpa, j.required_skills, j.location, j.vacancies, j.status
            FROM jobs j
            JOIN employers e ON e.employer_id = j.employer_id
            ORDER BY j.job_id
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n============================================ JOB VACANCIES ============================================");
            System.out.printf("%-8s | %-18s | %-22s | %-10s | %-6s | %-8s | %-10s | %-8s%n",
                    "JOB ID", "COMPANY", "ROLE TITLE", "TYPE", "MIN EXP", "CTC(LPA)", "LOCATION", "STATUS");
            System.out.println("--------------------------------------------------------------------------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-8s | %-18s | %-22s | %-10s | %-6.1f | %-8.2f | %-10s | %-8s%n",
                        rs.getString("job_id"),
                        truncate(rs.getString("company_name"), 18),
                        truncate(rs.getString("job_title"), 22),
                        rs.getString("job_type"),
                        rs.getDouble("min_experience"),
                        rs.getDouble("offered_salary_lpa"),
                        rs.getString("location"),
                        rs.getString("status")
                );
            }
            if (!found) {
                System.out.println("No job postings available.");
            }
            System.out.println("========================================================================================================");
        }
    }

    public boolean postJob(String jobId, String employerId, String title, String dept,
                           String jobType, double minExp, double salaryLpa, String skills,
                           String location, int vacancies) throws SQLException {
        String query = """
            INSERT INTO jobs (job_id, employer_id, job_title, department, job_type, min_experience,
                              offered_salary_lpa, required_skills, location, vacancies, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'OPEN')
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, jobId);
            ps.setString(2, employerId);
            ps.setString(3, title);
            ps.setString(4, dept);
            ps.setString(5, jobType);
            ps.setDouble(6, minExp);
            ps.setDouble(7, salaryLpa);
            ps.setString(8, skills);
            ps.setString(9, location);
            ps.setInt(10, vacancies);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateJobStatus(String jobId, String status) throws SQLException {
        String query = "UPDATE jobs SET status = ? WHERE job_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, status);
            ps.setString(2, jobId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteJob(String jobId) throws SQLException {
        String query = "DELETE FROM jobs WHERE job_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, jobId);
            return ps.executeUpdate() > 0;
        }
    }

    public void filterJobs(String keyword, String location) throws SQLException {
        String query = """
            SELECT j.job_id, e.company_name, j.job_title, j.job_type, j.min_experience,
                   j.offered_salary_lpa, j.required_skills, j.location, j.status
            FROM jobs j
            JOIN employers e ON e.employer_id = j.employer_id
            WHERE (LOWER(j.job_title) LIKE ? OR LOWER(j.required_skills) LIKE ?)
              AND LOWER(j.location) LIKE ?
            ORDER BY j.offered_salary_lpa DESC
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            String kwPattern = "%" + (keyword != null ? keyword.toLowerCase() : "") + "%";
            String locPattern = "%" + (location != null ? location.toLowerCase() : "") + "%";
            ps.setString(1, kwPattern);
            ps.setString(2, kwPattern);
            ps.setString(3, locPattern);

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n--- Filtered Job Results ---");
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    System.out.printf("[%s] %s at %s (%s) | MinExp: %.1f yrs | CTC: %.2f LPA | Skills: %s | Status: %s%n",
                            rs.getString("job_id"),
                            rs.getString("job_title"),
                            rs.getString("company_name"),
                            rs.getString("location"),
                            rs.getDouble("min_experience"),
                            rs.getDouble("offered_salary_lpa"),
                            rs.getString("required_skills"),
                            rs.getString("status")
                    );
                }
                if (!any) {
                    System.out.println("No matching jobs found.");
                }
            }
        }
    }

    public boolean jobExists(String jobId) throws SQLException {
        String query = "SELECT 1 FROM jobs WHERE job_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, jobId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static String truncate(String text, int max) {
        if (text == null) return "-";
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }
}
