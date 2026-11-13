import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for Job Seekers (Candidates).
 */
public class JobSeekerDAO {

    public void displayAllJobSeekers() throws SQLException {
        String query = """
            SELECT seeker_id, full_name, qualification, experience_years, expected_salary_lpa, skills, email, phone
            FROM job_seekers
            ORDER BY seeker_id
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n==================================== REGISTERED CANDIDATES ====================================");
            System.out.printf("%-10s | %-20s | %-12s | %-6s | %-10s | %-25s | %-12s%n",
                    "ID", "NAME", "DEGREE", "EXP(Y)", "EXP CTC", "SKILLS", "PHONE");
            System.out.println("-----------------------------------------------------------------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-10s | %-20s | %-12s | %-6.1f | %-10.2f | %-25s | %-12s%n",
                        rs.getString("seeker_id"),
                        rs.getString("full_name"),
                        rs.getString("qualification"),
                        rs.getDouble("experience_years"),
                        rs.getDouble("expected_salary_lpa"),
                        truncate(rs.getString("skills"), 25),
                        rs.getString("phone") != null ? rs.getString("phone") : "N/A"
                );
            }
            if (!found) {
                System.out.println("No job seekers registered yet.");
            }
            System.out.println("===============================================================================================");
        }
    }

    public boolean registerJobSeeker(String id, String name, String email, String phone,
                                    String qualification, String skills, double expYears, double expCtc) throws SQLException {
        String query = """
            INSERT INTO job_seekers (seeker_id, full_name, email, phone, qualification, skills, experience_years, expected_salary_lpa)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setString(4, phone != null && !phone.isBlank() ? phone : null);
            ps.setString(5, qualification);
            ps.setString(6, skills);
            ps.setDouble(7, expYears);
            ps.setDouble(8, expCtc);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateJobSeeker(String id, String name, String email, String phone,
                                  String qualification, String skills, double expYears, double expCtc) throws SQLException {
        String query = """
            UPDATE job_seekers
            SET full_name = ?, email = ?, phone = ?, qualification = ?, skills = ?, experience_years = ?, expected_salary_lpa = ?
            WHERE seeker_id = ?
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone != null && !phone.isBlank() ? phone : null);
            ps.setString(4, qualification);
            ps.setString(5, skills);
            ps.setDouble(6, expYears);
            ps.setDouble(7, expCtc);
            ps.setString(8, id);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteJobSeeker(String id) throws SQLException {
        String query = "DELETE FROM job_seekers WHERE seeker_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public void searchJobSeekersBySkill(String keyword) throws SQLException {
        String query = """
            SELECT seeker_id, full_name, qualification, experience_years, skills, email
            FROM job_seekers
            WHERE LOWER(skills) LIKE ? OR LOWER(qualification) LIKE ?
            ORDER BY experience_years DESC
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            String pattern = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n--- Search Results for Skill/Degree: [" + keyword + "] ---");
                boolean any = false;
                while (rs.next()) {
                    any = true;
                    System.out.printf("[%s] %s | %s | Exp: %.1f yrs | Skills: %s | Email: %s%n",
                            rs.getString("seeker_id"),
                            rs.getString("full_name"),
                            rs.getString("qualification"),
                            rs.getDouble("experience_years"),
                            rs.getString("skills"),
                            rs.getString("email")
                    );
                }
                if (!any) {
                    System.out.println("No candidates matched your search criteria.");
                }
            }
        }
    }

    public boolean seekerExists(String id) throws SQLException {
        String query = "SELECT 1 FROM job_seekers WHERE seeker_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, id);
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
