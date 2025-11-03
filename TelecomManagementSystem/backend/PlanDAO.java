package backend;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanDAO {
    public PlanDAO() {
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS plans (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "data_gb INT NOT NULL, " +
                "price DECIMAL(10,2) NOT NULL" +
                ")";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn != null ? conn.createStatement() : null) {
            if (stmt != null) stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Object[]> listPlans() {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT id, name, data_gb, price FROM plans ORDER BY id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn != null ? conn.prepareStatement(sql) : null;
             ResultSet rs = ps != null ? ps.executeQuery() : null) {
            if (rs != null) {
                while (rs.next()) {
                    rows.add(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getInt("data_gb"), rs.getBigDecimal("price")});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public boolean addPlan(String name, int dataGb, double price) {
        String sql = "INSERT INTO plans(name, data_gb, price) VALUES(?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn != null ? conn.prepareStatement(sql) : null) {
            if (ps == null) return false;
            ps.setString(1, name);
            ps.setInt(2, dataGb);
            ps.setDouble(3, price);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
