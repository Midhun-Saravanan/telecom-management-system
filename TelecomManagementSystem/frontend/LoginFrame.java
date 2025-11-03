package frontend;

import backend.DBConnection; 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*; 

public class LoginFrame extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginBtn;
    private JButton registerBtn; 

    public LoginFrame() {
        setTitle("Telecom Management System - Login/Register");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new GridLayout(5, 1, 10, 10)); 

        JLabel title = new JLabel("Login / Register", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(title);


        JPanel userPanel = new JPanel(new FlowLayout());
        userPanel.add(new JLabel("Username:"));
        usernameField = new JTextField(15);
        userPanel.add(usernameField);
        add(userPanel);

        JPanel passPanel = new JPanel(new FlowLayout());
        passPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(15);
        passPanel.add(passwordField);
        add(passPanel);

  
        JPanel btnPanel = new JPanel(new FlowLayout());
        loginBtn = new JButton("Login");
        loginBtn.addActionListener(this);
        btnPanel.add(loginBtn);
        
        registerBtn = new JButton("Register"); // Initialize the Register button
        registerBtn.addActionListener(this);
        btnPanel.add(registerBtn);
        add(btnPanel);
    }
    

    private String authenticateUser(String username, String password) {
        String query = "SELECT role FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role"); 
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "A database error occurred during login.");
        }
        return null; 
    }
    

    private boolean registerUser(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and Password cannot be empty.");
            return false;
        }

        String insertQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, 'user')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            int rowsAffected = pstmt.executeUpdate(); // Executes the INSERT statement
            return rowsAffected > 0;
            
        } catch (SQLIntegrityConstraintViolationException ex) {
            // Catches error if username is already taken (due to UNIQUE constraint)
            JOptionPane.showMessageDialog(this, "Error: Username '" + username + "' is already taken.");
            return false;
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "A database error occurred during registration.");
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String user = usernameField.getText();
        String pass = new String(passwordField.getPassword());
        
        if (e.getSource() == loginBtn) {
            // LOGIC FOR LOGIN
            String role = authenticateUser(user, pass); 

            if (role != null) {
                JOptionPane.showMessageDialog(this, role.substring(0, 1).toUpperCase() + role.substring(1) + " login successful!");
                new DashboardFrame(role, user).setVisible(true); 
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!");
            }
        } else if (e.getSource() == registerBtn) {
            // LOGIC FOR REGISTRATION
            if (registerUser(user, pass)) {
                JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.");
                usernameField.setText("");
                passwordField.setText("");
            }
        }
    }
}