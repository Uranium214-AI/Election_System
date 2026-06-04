import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConfettiEngine extends JPanel implements java.awt.event.ActionListener {
    private final List<Particle> particles = new ArrayList<>();
    private final Timer loopTimer = new Timer(16, this);

    public ConfettiEngine() {
        setOpaque(false);
        Random r = new Random();
        String[] housePalettes = {"jaguar", "sher", "puma", "cheetah"};

        for (int i = 0; i < 180; i++) {
            particles.add(new Particle(
                    300 + r.nextInt(550), r.nextInt(120) - 130,
                    r.nextFloat() * 7 - 3.5, r.nextFloat() * 5 + 2.5,
                    NovaTheme.getHouseColor(housePalettes[r.nextInt(housePalettes.length)]),
                    r.nextInt(10) + 8
            ));
        }
    }

    public void start() { loopTimer.start(); }
    public void stop() { loopTimer.stop(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (Particle p : particles) {
            g2.setColor(p.color);
            g2.fillRoundRect((int)p.x, (int)p.y, p.size, p.size / 2, 4, 4);
        }
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        for (Particle p : particles) p.update();
        repaint();
    }

    private static class Particle {
        double x, y, vx, vy, phase, wobbleSpeed; Color color; int size;
        Particle(double x, double y, double vx, double vy, Color c, int size) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy; this.color = c; this.size = size;
            Random r = new Random();
            this.phase = r.nextDouble() * Math.PI * 2;
            this.wobbleSpeed = 0.05 + r.nextDouble() * 0.07;
        }
        void update() {
            x += vx + Math.sin(phase) * 1.8;
            y += vy;
            vy += 0.13; // Consistent gravity vector
            phase += wobbleSpeed;
        }
    }
}