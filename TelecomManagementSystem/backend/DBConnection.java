package backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // IMPORTANT: Make sure this password matches the root password you set during MySQL installation!
    private static final String URL = "jdbc:mysql://localhost:3306/telecom_db";
    private static final String USER = "root"; 
    private static final String PASSWORD = "1234"; // <-- REPLACE WITH YOUR ACTUAL PASSWORD!

    /**
     * Establishes and returns a connection to the MySQL database.
     * @return Connection object if successful, null otherwise.
     */
    public static Connection getConnection() {
        try {

            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connection established successfully.");
            return conn;
        } catch (SQLException e) {
            System.err.println("Database connection failed! Check your password, URL, and server status (port 3306).");
            e.printStackTrace();
            return null;
        }
    }
    

    public static void main(String[] args) {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}