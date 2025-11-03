package backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionDAO {
    public SubscriptionDAO() { createTableIfNotExists(); }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS subscriptions ("+
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(100) NOT NULL, " +
                "plan_id INT NOT NULL, " +
                "status VARCHAR(20) NOT NULL DEFAULT 'Pending', " +
                "subscribed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (plan_id) REFERENCES plans(id) ON DELETE CASCADE" +
                ")";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn != null ? conn.createStatement() : null) {
            if (st != null) st.execute(sql);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean applyPlan(String username, int planId) {
        String sql = "INSERT INTO subscriptions(username, plan_id, status) VALUES(?,?,'Pending')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn != null ? conn.prepareStatement(sql) : null) {
            if (ps == null) return false;
            ps.setString(1, username);
            ps.setInt(2, planId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Object[]> listSubscriptionsByUser(String username) {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT s.id, s.plan_id, p.name, p.data_gb, p.price, s.status, s.subscribed_at " +
                     "FROM subscriptions s JOIN plans p ON s.plan_id=p.id WHERE s.username=? ORDER BY s.id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn != null ? conn.prepareStatement(sql) : null) {
            if (ps == null) return rows;
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{
                            rs.getInt("id"),
                            rs.getInt("plan_id"),
                            rs.getString("name"),
                            rs.getInt("data_gb"),
                            rs.getBigDecimal("price"),
                            rs.getString("status"),
                            rs.getTimestamp("subscribed_at")
                    });
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }

    public boolean updateLatestPendingStatusByUser(String username, String newStatus) {
        String sql = "UPDATE subscriptions SET status=? WHERE id = (" +
                     "SELECT id FROM (SELECT id FROM subscriptions WHERE username=? AND status='Pending' ORDER BY id DESC LIMIT 1) t)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn != null ? conn.prepareStatement(sql) : null) {
            if (ps == null) return false;
            ps.setString(1, newStatus);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateStatusById(int subscriptionId, String newStatus) {
        String sql = "UPDATE subscriptions SET status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn != null ? conn.prepareStatement(sql) : null) {
            if (ps == null) return false;
            ps.setString(1, newStatus);
            ps.setInt(2, subscriptionId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
