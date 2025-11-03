package frontend;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {
    private PlansPanel plansPanel;

    public AdminPanel() {
        setLayout(new BorderLayout());
        plansPanel = new PlansPanel(true); // admin can add plans
        add(plansPanel, BorderLayout.CENTER);
    }
}
