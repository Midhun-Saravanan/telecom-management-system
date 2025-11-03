package frontend;

import javax.swing.*;
import java.awt.*;

public class UserPanel extends JPanel {
    private PlansPanel plansPanel;
    private final String username;

    public UserPanel() {
        this(null);
    }

    public UserPanel(String username) {
        this.username = username;
        setLayout(new BorderLayout());
        plansPanel = new PlansPanel(false, this.username); // user can apply plans with username
        add(plansPanel, BorderLayout.CENTER);
    }
}
