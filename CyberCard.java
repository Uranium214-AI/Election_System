import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class CyberCard extends JToggleButton {
    private final String name, house;
    private final Color primary, secondary;
    private float pulse = 0f;
    private boolean isHovered = false;

    public CyberCard(String rawData) {
        String[] parts = rawData.split(",");
        this.name = parts[0];
        this.house = parts[1];
        this.primary = NovaTheme.getHouseColor(house);
        this.secondary = NovaTheme.getGlowColor(house);

        setPreferredSize(new Dimension(300, 160));
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { isHovered = true; }
            public void mouseExited(java.awt.event.MouseEvent e) { isHovered = false; }
        });

        new Timer(30, e -> { pulse += 0.05f; repaint(); }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Card Base
        g2.setColor(new Color(18, 19, 28));
        g2.fillRoundRect(10, 10, getWidth()-20, getHeight()-20, 25, 25);

        // Neon Border (Fixed Java 11 clamp)
        int alpha = (int)(150 + Math.sin(pulse) * 50);
        alpha = Math.max(0, Math.min(255, alpha));

        g2.setColor(new Color(primary.getRed(), primary.getGreen(), primary.getBlue(), alpha));
        g2.setStroke(new BasicStroke(isSelected() || isHovered ? 4f : 1.5f));
        g2.drawRoundRect(10, 10, getWidth()-20, getHeight()-20, 25, 25);

        // Text Content
        g2.setColor(Color.WHITE);
        g2.setFont(NovaTheme.FONT_SUB);
        g2.drawString(name, 30, 70);

        g2.setFont(NovaTheme.FONT_BODY);
        g2.setColor(primary);
        g2.drawString("[" + house.toUpperCase() + "]", 30, 100);

        if (isSelected()) {
            g2.setColor(NovaTheme.NEON_CYAN);
            g2.drawString("SELECTED //", getWidth()-120, 130);
        }

        g2.dispose();
    }
}