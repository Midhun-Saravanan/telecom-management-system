package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import backend.BillingDAO;
import backend.PlanDAO;
import backend.SubscriptionDAO;

public class BillingPanel extends JPanel implements ActionListener {
    private JTextField nameField, amountField;
    private JButton generateBillBtn;
    private JTable billsTable;
    private DefaultTableModel tableModel;
    private final BillingDAO billingDAO = new BillingDAO();
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JButton searchBtn;
    private JButton markPaidBtn;
    private final boolean isAdmin;
    private JComboBox<Object[]> planCombo; // [id, label]
    private JButton applyPlanBtn;
    private final PlanDAO planDAO = new PlanDAO();
    private final SubscriptionDAO subscriptionDAO = new SubscriptionDAO();
    private final String username;
    private JTable subsTable; // user subscriptions table
    private DefaultTableModel subsTableModel;

    public BillingPanel() {
        this(true, null);
    }

    public BillingPanel(boolean isAdmin) {
        this(isAdmin, null);
    }

    public BillingPanel(boolean isAdmin, String username) {
        this.isAdmin = isAdmin;
        this.username = username;
        setLayout(new BorderLayout());
        JPanel form = new JPanel(new GridLayout(3,2,10,10));
        form.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        form.add(new JLabel("Customer Name:"));
        nameField = new JTextField();
        form.add(nameField);

        form.add(new JLabel("Amount (₹):"));
        amountField = new JTextField();
        form.add(amountField);

        generateBillBtn = new JButton("Generate Bill");
        generateBillBtn.addActionListener(this);
        form.add(new JLabel());
        form.add(generateBillBtn);

        add(form, BorderLayout.NORTH);

        String[] columns = {"Bill ID","Customer","Amount (₹)","Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        billsTable = new JTable(tableModel);
        billsTable.setRowHeight(25);
        billsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(new JScrollPane(billsTable), BorderLayout.CENTER);

        // Admin-only controls below table: search/filter + mark as paid
        if (isAdmin) {
            JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
            controls.add(new JLabel("Search:"));
            searchField = new JTextField(12);
            controls.add(searchField);
            controls.add(new JLabel("Status:"));
            statusFilter = new JComboBox<>(new String[]{"All","Pending","Paid"});
            controls.add(statusFilter);
            searchBtn = new JButton("Apply");
            searchBtn.addActionListener(this);
            controls.add(searchBtn);
            markPaidBtn = new JButton("Mark as Paid");
            markPaidBtn.addActionListener(this);
            controls.add(markPaidBtn);
            add(controls, BorderLayout.SOUTH);
        }

        loadBills();
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == generateBillBtn) {
            String name = nameField.getText();
            String amount = amountField.getText();
            if(name.isEmpty() || amount.isEmpty()) {
                JOptionPane.showMessageDialog(this,"Enter all fields!");
            } else {
                try {
                    double amt = Double.parseDouble(amount);
                    boolean ok = billingDAO.insertBill(name, amt, "Pending");
                    if (ok) {
                        loadBills();
                        JOptionPane.showMessageDialog(this,"Bill generated for "+name+" (₹"+amount+")");
                        nameField.setText("");
                        amountField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(this,"Failed to generate bill");
                    }
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(this, "Amount must be a number");
                }
            }
        } else if (e.getSource() == searchBtn || e.getSource() == statusFilter) {
            loadBills();
        } else if (e.getSource() == markPaidBtn) {
            int row = billsTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a bill to mark as Paid");
                return;
            }
            Object idObj = tableModel.getValueAt(row, 0);
            if (idObj == null) {
                JOptionPane.showMessageDialog(this, "Invalid selection");
                return;
            }
            int id = Integer.parseInt(String.valueOf(idObj));
            boolean ok = billingDAO.updateBillStatus(id, "Paid");
            if (ok) {
                loadBills();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update status");
            }
        }
    }

    private void loadBills() {
        if (tableModel == null) return;
        tableModel.setRowCount(0);
        String q = (isAdmin && searchField != null) ? searchField.getText() : null;
        String status = (isAdmin && statusFilter != null && statusFilter.getSelectedItem()!=null) ? statusFilter.getSelectedItem().toString() : "All";
        java.util.List<Object[]> rows = billingDAO.listBillsFiltered(q, status);
        for (Object[] row : rows) {
            tableModel.addRow(row);
        }
    }

    private void loadSubscriptions() {
        if (isAdmin || subsTableModel == null || username == null) return;
        subsTableModel.setRowCount(0);
        java.util.List<Object[]> subs = subscriptionDAO.listSubscriptionsByUser(username);
        for (Object[] r : subs) {
            // r: id, plan_id, name, data_gb, price, status, subscribed_at
            subsTableModel.addRow(new Object[]{r[0], r[2], r[3], r[4], r[5], r[6]});
        }
    }

    private void populatePlanCombo() {
        if (planCombo == null) return;
        planCombo.removeAllItems();
        java.util.List<Object[]> plans = planDAO.listPlans();
        for (Object[] r : plans) {
            int id = Integer.parseInt(String.valueOf(r[0]));
            String name = String.valueOf(r[1]);
            String data = String.valueOf(r[2]);
            String price = String.valueOf(r[3]);
            String label = name + " (" + data + " GB, ₹" + price + ")";
            planCombo.addItem(new Object[]{id, label});
        }
        planCombo.setRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Object[]) {
                    setText(String.valueOf(((Object[])value)[1]));
                }
                return c;
            }
        });
    }
}
