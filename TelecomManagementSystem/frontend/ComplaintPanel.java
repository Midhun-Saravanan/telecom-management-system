package frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import backend.ComplaintDAO;

public class ComplaintPanel extends JPanel implements ActionListener {
    private JTextArea complaintText;
    private JButton submitBtn;
    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private final String username;

    public ComplaintPanel() {
        this(null);
    }

    public ComplaintPanel(String username) {
        this.username = username;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        complaintText = new JTextArea(6,40);
        complaintText.setLineWrap(true);
        complaintText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        complaintText.setBorder(BorderFactory.createLineBorder(new Color(25,118,210)));

        submitBtn = new JButton("Submit Complaint");
        submitBtn.setBackground(new Color(25,118,210));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.addActionListener(this);

        add(new JLabel("Describe your issue:"), BorderLayout.NORTH);
        add(new JScrollPane(complaintText), BorderLayout.CENTER);
        add(submitBtn, BorderLayout.SOUTH);
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == submitBtn) {
            String msg = complaintText.getText();
            if(msg.isEmpty()) {
                JOptionPane.showMessageDialog(this,"Enter complaint!");
            } else {
                boolean ok = complaintDAO.insertComplaint(username, msg);
                if (ok) {
                    JOptionPane.showMessageDialog(this,"Complaint submitted!");
                    complaintText.setText("");
                } else {
                    JOptionPane.showMessageDialog(this,"Failed to submit complaint");
                }
            }
        }
    }
}
