import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

public class MultiColorLoadingIcon implements Icon {
    private final int size;
    private final Timer timer;
    private long startTime;
    private float strokeThickness = 3.5f;

    private final Color[] palette = {
            new Color(0x2E, 0xCC, 0x71),
            new Color(0xDC, 0x14, 0x3C),
            new Color(0xF1, 0xC4, 0x0F),
            new Color(0xE6, 0x7E, 0x22)
    };

    public MultiColorLoadingIcon(int size, JComponent parentComponent) {
        this.size = size;
        this.startTime = System.currentTimeMillis();

        this.timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (parentComponent != null && parentComponent.isShowing()) {
                    parentComponent.repaint();
                }
            }
        });
    }

    public void start() {
        if (!timer.isRunning()) {
            this.startTime = System.currentTimeMillis();
            timer.start();
        }
    }

    public void stop() {
        if (timer.isRunning()) {
            timer.stop();
        }
    }

    public void setStrokeThickness(float thickness) {
        this.strokeThickness = thickness;
    }

    private Color blendColors(Color c1, Color c2, float ratio) {
        ratio = Math.max(0f, Math.min(1f, ratio));
        int r = (int) (c1.getRed() * (1 - ratio) + c2.getRed() * ratio);
        int g = (int) (c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio);
        int b = (int) (c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio);
        return new Color(r, g, b);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        long elapsed = System.currentTimeMillis() - startTime;
        int colorDuration = 1500;
        int totalCycleTime = colorDuration * palette.length;
        long cycleTime = elapsed % totalCycleTime;

        int currentIndex = (int) (cycleTime / colorDuration);
        int nextIndex = (currentIndex + 1) % palette.length;
        float blendRatio = (float) (cycleTime % colorDuration) / colorDuration;
        float easeRatio = (float) (0.5 - Math.cos(blendRatio * Math.PI) / 2);

        Color currentColor = blendColors(palette[currentIndex], palette[nextIndex], easeRatio);
        Color trackColor = new Color(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), 35);

        float baseAngle = (elapsed * 0.24f) % 360;
        double pulse = Math.sin(elapsed * 0.003);
        float arcLength = (float) (160 + (120 * pulse));
        float finalAngle = (float) (baseAngle - (40 * pulse));

        float padding = strokeThickness / 2.0f;
        float arcSize = size - (padding * 2);
        float ax = x + padding;
        float ay = y + padding;

        g2d.setColor(trackColor);
        g2d.setStroke(new BasicStroke(strokeThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(new Ellipse2D.Float(ax, ay, arcSize, arcSize));

        g2d.setColor(currentColor);
        g2d.draw(new Arc2D.Float(ax, ay, arcSize, arcSize, finalAngle, arcLength, Arc2D.OPEN));

        g2d.dispose();
    }

    @Override
    public int getIconWidth() { return size; }
    @Override
    public int getIconHeight() { return size; }
}