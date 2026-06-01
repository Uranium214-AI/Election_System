import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {
    public AdminPanel(ElectionMain parent) {
        setBackground(Color.WHITE);
        JButton back = new JButton("Back to Login");
        back.addActionListener(e -> {
    Container parentContainer = parent.getContentPane();
    // We cast to Container so Java knows it has a Layout
    Container mainPanel = (Container) parentContainer.getComponent(0);
    ((CardLayout) mainPanel.getLayout()).show(mainPanel, "AUTH");
});

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(new Font("Arial", 1, 18));
        int y = 50;
        for (var pos : ElectionData.voteTally.entrySet()) {
            g.drawString(pos.getKey(), 50, y);
            y += 30;
            for (var cand : pos.getValue().entrySet()) {
                g.fillRect(50, y, cand.getValue() * 20, 20);
                g.drawString(cand.getKey() + " (" + cand.getValue() + ")", 60 + (cand.getValue() * 20), y + 15);
                y += 30;
            }
            y += 20;
        }
    }
}
