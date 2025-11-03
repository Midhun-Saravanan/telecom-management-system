package backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDAO {
    public ComplaintDAO() { createTableIfNotExists(); }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS complaints ("+
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(100) NULL, " +
                "message TEXT NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn != null ? conn.createStatement() : null) {
            if (st != null) st.execute(sql);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean insertComplaint(String username, String message) {
        String sql = "INSERT INTO complaints(username, message) VALUES(?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn != null ? conn.prepareStatement(sql) : null) {
            if (ps == null) return false;
            if (username == null || username.isEmpty()) ps.setNull(1, Types.VARCHAR); else ps.setString(1, username);
            ps.setString(2, message);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Object[]> listComplaints() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT id, username, message, created_at FROM complaints ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn != null ? conn.prepareStatement(sql) : null;
             ResultSet rs = ps != null ? ps.executeQuery() : null) {
            if (rs != null) {
                while (rs.next()) {
                    rows.add(new Object[]{rs.getInt("id"), rs.getString("username"), rs.getString("message"), rs.getTimestamp("created_at")});
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }
}
