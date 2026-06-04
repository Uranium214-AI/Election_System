import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class AdminPanel extends JPanel {
    private final ElectionMain parent;
    private final List<Particle> particles = new ArrayList<>();
    private final javax.swing.Timer engine;
    private double progress = 0;
    private int currentVotes = 0;

    public AdminPanel(ElectionMain parent) {
        this.parent = parent;
        setBackground(NovaTheme.OBSIDIAN);
        setLayout(new BorderLayout());

        engine = new javax.swing.Timer(16, e -> {
            updatePhysics();
            repaint();

        JButton exit = new JButton("DISCONNECT TERMINAL");
        exit.addActionListener(event -> { engine.stop(); parent.transitionToScreen("AUTH"); });
        add(exit, BorderLayout.NORTH);


        });
    }

    public void sync() {
        int count = 0;
        for (var cat : ElectionData.voteTally.values()) {
            for (int v : cat.values()) count += v;
        }
        if (count > currentVotes) {
            for (int i=0; i < (count-currentVotes)*10; i++) particles.add(new Particle(getWidth(), getHeight()));
            currentVotes = count;
        }
        engine.start();
    }

    private void updatePhysics() {
        double target = (double)currentVotes / 1000.0;
        if (progress < target) progress += 0.005;

        Point2D center = new Point2D.Double(getWidth()/2.0, getHeight()/2.0);
        for (int i = particles.size()-1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.update(center);
            if (p.isDead) particles.remove(i);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth()/2, cy = getHeight()/2;

        // Progress Ring
        g2.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(30, 30, 45));
        g2.drawOval(cx-150, cy-150, 300, 300);

        g2.setColor(NovaTheme.NEON_CYAN);
        g2.draw(new Arc2D.Double(cx-150, cy-150, 300, 300, 90, -progress*360, Arc2D.OPEN));

        g2.setFont(NovaTheme.FONT_HEADING);
        g2.setColor(Color.WHITE);
        String txt = currentVotes + " VBT";
        g2.drawString(txt, cx - g2.getFontMetrics().stringWidth(txt)/2, cy + 15);

        for (Particle p : particles) p.draw(g2);
        g2.dispose();
    }

    private static class Particle {
        double x, y, vx, vy;
        boolean isDead = false;
        public Particle(int w, int h) {
            Random r = new Random();
            x = r.nextInt(w); y = r.nextInt(h);
        }
        void update(Point2D target) {
            double dx = target.getX() - x, dy = target.getY() - y;
            double dist = Math.hypot(dx, dy);
            if (dist < 20) { isDead = true; return; }
            vx += dx/dist * 0.5; vy += dy/dist * 0.5;
            x += vx; y += vy;
            vx *= 0.92; vy *= 0.92;
        }
        void draw(Graphics2D g) {
            g.setColor(NovaTheme.NEON_CYAN);
            g.fill(new Ellipse2D.Double(x, y, 3, 3));
        }
    }
}