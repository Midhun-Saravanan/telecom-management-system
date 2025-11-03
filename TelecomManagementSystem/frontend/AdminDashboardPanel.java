package frontend;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import backend.PlanDAO;
import backend.BillingDAO;
import backend.ComplaintDAO;

public class AdminDashboardPanel extends JPanel {
    private final PlanDAO planDAO = new PlanDAO();
    private final BillingDAO billingDAO = new BillingDAO();
    private final ComplaintDAO complaintDAO = new ComplaintDAO();

    private JLabel plansCountLabel;
    private JLabel billsCountLabel;
    private JLabel complaintsCountLabel;
    private JTable recentComplaintsTable;

    public AdminDashboardPanel(String username) {
        setLayout(new BorderLayout(12,12));
        setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        JLabel title = new JLabel("Admin Dashboard", JLabel.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        JPanel stats = new JPanel(new GridLayout(1,3,12,12));
        plansCountLabel = statCard("Total Plans", "0", new Color(25,118,210));
        billsCountLabel = statCard("Total Bills", "0", new Color(46,125,50));
        complaintsCountLabel = statCard("Complaints", "0", new Color(198,40,40));
        stats.add(wrap(plansCountLabel));
        stats.add(wrap(billsCountLabel));
        stats.add(wrap(complaintsCountLabel));
        add(stats, BorderLayout.CENTER);

        // Recent complaints table
        String[] cols = {"ID","Username","Message"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        recentComplaintsTable = new JTable(model);
        recentComplaintsTable.setRowHeight(24);
        recentComplaintsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JPanel bottom = new JPanel(new BorderLayout());
        JLabel rc = new JLabel("Recent Complaints", JLabel.LEFT);
        rc.setBorder(BorderFactory.createEmptyBorder(8,0,8,0));
        rc.setFont(new Font("Segoe UI", Font.BOLD, 16));
        bottom.add(rc, BorderLayout.NORTH);
        bottom.add(new JScrollPane(recentComplaintsTable), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        refreshData();
    }

    private JLabel statCard(String title, String value, Color color) {
        JLabel label = new JLabel("<html><div style='text-align:center;'><div style='font-size:13px;opacity:0.85;'>"+title+"</div><div style='font-size:24px;font-weight:700;'>"+value+"</div></div></html>", JLabel.CENTER);
        label.setOpaque(true);
        label.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 32));
        label.setBorder(BorderFactory.createLineBorder(color));
        label.setPreferredSize(new Dimension(220,80));
        return label;
    }

    private JComponent wrap(JLabel label) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(label, BorderLayout.CENTER);
        return p;
    }

    private void refreshData() {
        int plans = planDAO.listPlans().size();
        int bills = billingDAO.listBills().size();
        int complaints = complaintDAO.listComplaints().size();
        setStatValue(plansCountLabel, "Total Plans", String.valueOf(plans));
        setStatValue(billsCountLabel, "Total Bills", String.valueOf(bills));
        setStatValue(complaintsCountLabel, "Complaints", String.valueOf(complaints));

        DefaultTableModel m = (DefaultTableModel) recentComplaintsTable.getModel();
        m.setRowCount(0);
        int shown = 0;
        for (Object[] r : complaintDAO.listComplaints()) {
            if (shown >= 5) break;
            m.addRow(new Object[]{r[0], r[1], r[2]});
            shown++;
        }
    }

    private void setStatValue(JLabel label, String title, String value) {
        label.setText("<html><div style='text-align:center;'><div style='font-size:13px;opacity:0.85;'>"+title+"</div><div style='font-size:24px;font-weight:700;'>"+value+"</div></div></html>");
    }
}
