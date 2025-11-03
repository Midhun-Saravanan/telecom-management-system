package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import backend.PlanDAO;
import backend.SubscriptionDAO;

public class PlansPanel extends JPanel implements ActionListener {
    private JTable plansTable;
    private DefaultTableModel tableModel;
    private JButton addPlanBtn;
    private JButton applyPlanBtn;
    private final PlanDAO planDAO = new PlanDAO();
    private final SubscriptionDAO subscriptionDAO = new SubscriptionDAO();
    private final boolean isAdmin;
    private final String username; // for user applications

    public PlansPanel(boolean isAdmin) {
        this(isAdmin, null);
    }

    public PlansPanel(boolean isAdmin, String username) {
        this.isAdmin = isAdmin;
        this.username = username;
        setLayout(new BorderLayout());

        String[] columns = {"Plan ID", "Plan Name", "Data (GB)", "Price (₹)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        plansTable = new JTable(tableModel);
        plansTable.setRowHeight(25);
        plansTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(new JScrollPane(plansTable), BorderLayout.CENTER);

        loadPlans();

        if(isAdmin) {
            addPlanBtn = new JButton("Add New Plan");
            addPlanBtn.addActionListener(this);
            add(addPlanBtn, BorderLayout.SOUTH);
        } else {
            applyPlanBtn = new JButton("Apply for Selected Plan");
            applyPlanBtn.addActionListener(this);
            add(applyPlanBtn, BorderLayout.SOUTH);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if(addPlanBtn != null && e.getSource() == addPlanBtn) {
            JPanel panel = new JPanel(new GridLayout(3,2,10,10));
            JTextField nameField = new JTextField();
            JTextField dataField = new JTextField();
            JTextField priceField = new JTextField();
            panel.add(new JLabel("Plan Name:")); panel.add(nameField);
            panel.add(new JLabel("Data (GB):")); panel.add(dataField);
            panel.add(new JLabel("Price (₹):")); panel.add(priceField);
            int res = JOptionPane.showConfirmDialog(this, panel, "Add Plan", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    String name = nameField.getText().trim();
                    int dataGb = Integer.parseInt(dataField.getText().trim());
                    double price = Double.parseDouble(priceField.getText().trim());
                    if (name.isEmpty()) throw new IllegalArgumentException("Name required");
                    boolean ok = planDAO.addPlan(name, dataGb, price);
                    if (ok) {
                        loadPlans();
                        JOptionPane.showMessageDialog(this, "Plan added successfully");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add plan");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
                }
            }
        } else if (applyPlanBtn != null && e.getSource() == applyPlanBtn) {
            if (username == null || username.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "You must be logged in to apply for a plan.");
                return;
            }
            int row = plansTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a plan to apply.");
                return;
            }
            Object idObj = tableModel.getValueAt(row, 0);
            if (idObj == null) {
                JOptionPane.showMessageDialog(this, "Invalid selection");
                return;
            }
            int planId = Integer.parseInt(String.valueOf(idObj));
            boolean ok = subscriptionDAO.applyPlan(username, planId);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Plan applied successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to apply for plan");
            }
        }
    }

    private void loadPlans() {
        // Clear model
        tableModel.setRowCount(0);
        for (Object[] row : planDAO.listPlans()) {
            tableModel.addRow(row);
        }
    }
}
