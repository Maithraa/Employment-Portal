import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central Database Connection Management for Employment Portal.
 * Supports MariaDB and MySQL with environment variable overrides.
 */
public final class DBConnection {
    private static final String URL = System.getenv().getOrDefault(
        "EMP_DB_URL", "jdbc:mariadb://localhost:3306/employment_portal");
    private static final String USER = System.getenv().getOrDefault("EMP_DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("EMP_DB_PASSWORD", "");

    private DBConnection() {}

    /**
     * Obtains an active connection to the database.
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
