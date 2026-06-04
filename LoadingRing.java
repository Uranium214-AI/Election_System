import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;

public class LoadingRing extends JComponent {
    private int sweepAngle = 0;

    public LoadingRing() {
        setPreferredSize(new Dimension(100, 100));
        new Timer(16, e -> {
            sweepAngle = (sweepAngle + 5) % 360;
            repaint();
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(NovaTheme.NEON_CYAN);
        int boundedSize = Math.min(getWidth(), getHeight()) - 20;
        g2.draw(new Arc2D.Float(10, 10, boundedSize, boundedSize, sweepAngle, 120, Arc2D.OPEN));
    }
}