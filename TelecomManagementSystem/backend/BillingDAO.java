package backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillingDAO {
    public BillingDAO() { createTableIfNotExists(); }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS bills ("+
                "id INT AUTO_INCREMENT PRIMARY KEY, "+
                "customer VARCHAR(100) NOT NULL, "+
                "amount DECIMAL(10,2) NOT NULL, "+
                "status VARCHAR(20) NOT NULL DEFAULT 'Pending', "+
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"+
                ")";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn != null ? conn.createStatement() : null) {
            if (st != null) st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Object[]> listBills() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT id, customer, amount, status FROM bills ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn != null ? conn.prepareStatement(sql) : null;
             ResultSet rs = ps != null ? ps.executeQuery() : null) {
            if (rs != null) {
                while (rs.next()) {
                    rows.add(new Object[]{rs.getInt("id"), rs.getString("customer"), rs.getBigDecimal("amount"), rs.getString("status")});
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }

    public boolean insertBill(String customer, double amount, String status) {
        String sql = "INSERT INTO bills(customer, amount, status) VALUES(?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn != null ? conn.prepareStatement(sql) : null) {
            if (ps == null) return false;
            ps.setString(1, customer);
            ps.setDouble(2, amount);
            ps.setString(3, status);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBillStatus(int id, String status) {
        String sql = "UPDATE bills SET status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn != null ? conn.prepareStatement(sql) : null) {
            if (ps == null) return false;
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public List<Object[]> listBillsFiltered(String query, String status) {
        List<Object[]> rows = new ArrayList<>();
        StringBuilder sb = new StringBuilder("SELECT id, customer, amount, status FROM bills");
        boolean hasWhere = false;
        if (query != null && !query.trim().isEmpty()) {
            sb.append(" WHERE customer LIKE ?");
            hasWhere = true;
        }
        if (status != null && !status.equalsIgnoreCase("All")) {
            sb.append(hasWhere ? " AND" : " WHERE");
            sb.append(" status = ?");
        }
        sb.append(" ORDER BY id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn != null ? conn.prepareStatement(sb.toString()) : null) {
            if (ps == null) return rows;
            int idx = 1;
            if (query != null && !query.trim().isEmpty()) {
                ps.setString(idx++, "%" + query.trim() + "%");
            }
            if (status != null && !status.equalsIgnoreCase("All")) {
                ps.setString(idx++, status);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[]{rs.getInt("id"), rs.getString("customer"), rs.getBigDecimal("amount"), rs.getString("status")});
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }
}
