package frontend;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    public DashboardFrame(String role, String username) {
        setTitle("Telecom Management System - Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Telecom Management System - " + role.toUpperCase(), JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setOpaque(true);
        header.setBackground(new Color(25, 118, 210));
        header.setForeground(Color.WHITE);
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        if(role.equals("admin")) {
            tabs.addTab("Dashboard", new AdminDashboardPanel(username));
            tabs.addTab("Plans", new AdminPanel());
            tabs.addTab("Billing", new BillingPanel(true));
        } else {
            tabs.addTab("Plans", new UserPanel(username));
            tabs.addTab("Billing", new BillingPanel(false, username));
            tabs.addTab("Complaints", new ComplaintPanel(username));
        }

        add(tabs, BorderLayout.CENTER);
    }
}
