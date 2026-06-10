import javax.swing.*;
import java.awt.*;

public class MultiColorLoadingIcon implements Icon {
    private final int size;
    private float strokeThickness = 4.0f;
    private int angle = 0;
    private final Timer animationTimer;

    public MultiColorLoadingIcon(int size, JComponent parentComponent) {
        this.size = size;
        this.animationTimer = new Timer(15, event -> {
            angle = (angle + 4) % 360;
            parentComponent.repaint();
        });
    }

    public void setStrokeThickness(float thickness) {
        this.strokeThickness = thickness;
    }

    public void start() { animationTimer.start(); }
    public void stop() { animationTimer.stop(); }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(strokeThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setColor(NovaTheme.NEON_CYAN);
        g2d.drawArc(x + 6, y + 6, size - 12, size - 12, angle, 260);
        g2d.dispose();
    }

    @Override public int getIconWidth() { return size; }
    @Override public int getIconHeight() { return size; }
}
