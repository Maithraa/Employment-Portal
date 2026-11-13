import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for Employers & Recruiting Companies.
 */
public class EmployerDAO {

    public void displayAllEmployers() throws SQLException {
        String query = """
            SELECT e.employer_id, e.company_name, e.industry, e.location, e.contact_email, e.contact_phone,
                   COUNT(j.job_id) AS total_jobs
            FROM employers e
            LEFT JOIN jobs j ON j.employer_id = e.employer_id
            GROUP BY e.employer_id, e.company_name, e.industry, e.location, e.contact_email, e.contact_phone
            ORDER BY e.employer_id
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n================================== RECRUITING COMPANIES ==================================");
            System.out.printf("%-10s | %-22s | %-20s | %-12s | %-10s | %-20s%n",
                    "EMP ID", "COMPANY NAME", "INDUSTRY", "LOCATION", "OPENINGS", "CONTACT EMAIL");
            System.out.println("------------------------------------------------------------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-10s | %-22s | %-20s | %-12s | %-10d | %-20s%n",
                        rs.getString("employer_id"),
                        rs.getString("company_name"),
                        rs.getString("industry"),
                        rs.getString("location"),
                        rs.getInt("total_jobs"),
                        rs.getString("contact_email")
                );
            }
            if (!found) {
                System.out.println("No employers registered yet.");
            }
            System.out.println("==========================================================================================");
        }
    }

    public boolean registerEmployer(String id, String companyName, String industry, String location,
                                    String email, String phone, String website) throws SQLException {
        String query = """
            INSERT INTO employers (employer_id, company_name, industry, location, contact_email, contact_phone, website)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, id);
            ps.setString(2, companyName);
            ps.setString(3, industry);
            ps.setString(4, location);
            ps.setString(5, email);
            ps.setString(6, phone != null && !phone.isBlank() ? phone : null);
            ps.setString(7, website != null && !website.isBlank() ? website : null);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateEmployer(String id, String companyName, String industry, String location,
                                  String email, String phone, String website) throws SQLException {
        String query = """
            UPDATE employers
            SET company_name = ?, industry = ?, location = ?, contact_email = ?, contact_phone = ?, website = ?
            WHERE employer_id = ?
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, companyName);
            ps.setString(2, industry);
            ps.setString(3, location);
            ps.setString(4, email);
            ps.setString(5, phone != null && !phone.isBlank() ? phone : null);
            ps.setString(6, website != null && !website.isBlank() ? website : null);
            ps.setString(7, id);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteEmployer(String id) throws SQLException {
        String query = "DELETE FROM employers WHERE employer_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean employerExists(String id) throws SQLException {
        String query = "SELECT 1 FROM employers WHERE employer_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
