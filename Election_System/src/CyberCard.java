import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class CyberCard extends JToggleButton {
    private final String name, house;
    private final Color primary, secondary;
    private float pulse = 0f;
    private boolean isHovered = false;
    private BufferedImage photo = null;

    public CyberCard(String rawData) {
        String[] parts = rawData.split(",");
        this.name = parts[0];
        this.house = parts[1];
        this.primary = NovaTheme.getHouseColor(house);
        this.secondary = NovaTheme.getGlowColor(house);

        setPreferredSize(new Dimension(340, 180));
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);

        // Optional Image Discovery: Sanitizes names to match image filenames automatically
        String imgFilename = name.toLowerCase().trim().replace(" ", "_") + ".png";
        File imgFile = new File(ElectionData.DIR + "assets/" + imgFilename);
        if (imgFile.exists()) {
            try {
                photo = ImageIO.read(imgFile);
            } catch (Exception e) { System.out.println("Failed loading profile image for " + name); }
        }

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { isHovered = true; }
            public void mouseExited(java.awt.event.MouseEvent e) { isHovered = false; }
        });

        new Timer(25, e -> { pulse += 0.04f; repaint(); }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Card Panel Body Base Fill
        g2.setColor(NovaTheme.CARD_BG);
        g2.fillRoundRect(10, 10, w - 20, h - 20, 24, 24);

        // Dynamic Neon Glow Bounds Calculations
        int alpha = (int)(160 + Math.sin(pulse) * 45);
        alpha = Math.max(0, Math.min(255, alpha));
        g2.setColor(new Color(primary.getRed(), primary.getGreen(), primary.getBlue(), alpha));
        g2.setStroke(new BasicStroke(isSelected() || isHovered ? 4.5f : 1.5f));
        g2.drawRoundRect(10, 10, w - 20, h - 20, 24, 24);

        // Render Optional Candidate Photo Profile Image Framework
        int avatarSize = 100;
        int avatarX = 25;
        int avatarY = (h - avatarSize) / 2;

        if (photo != null) {
            // Render clipped smooth circular image bounds
            Area clipArea = new Area(new Ellipse2D.Double(avatarX, avatarY, avatarSize, avatarSize));
            g2.setClip(clipArea);
            g2.drawImage(photo, avatarX, avatarY, avatarSize, avatarSize, null);
            g2.setClip(null); // Reset clip boundary configurations

            // Outer Ring Border Frame for Avatar
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(primary);
            g2.drawOval(avatarX, avatarY, avatarSize, avatarSize);
        } else {
            // Fallback: Custom Mathematical Vector Holographic Shield Design
            g2.setColor(new Color(primary.getRed(), primary.getGreen(), primary.getBlue(), 35));
            g2.fillOval(avatarX, avatarY, avatarSize, avatarSize);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(primary);
            g2.drawOval(avatarX, avatarY, avatarSize, avatarSize);

            // Minimalist Shield Inner Vectors
            g2.drawLine(avatarX + 50, avatarY + 25, avatarX + 50, avatarY + 75);
            g2.drawLine(avatarX + 25, avatarY + 50, avatarX + 75, avatarY + 50);
        }

        // Text Layout Rendering Adjustments
        int textLeftOffset = avatarX + avatarSize + 20;
        g2.setColor(Color.WHITE);
        g2.setFont(NovaTheme.FONT_SUB);
        g2.drawString(name, textLeftOffset, 75);

        g2.setFont(NovaTheme.FONT_BODY);
        g2.setColor(primary);
        g2.drawString(">> " + house.toUpperCase(), textLeftOffset, 105);

        if (isSelected()) {
            g2.setColor(NovaTheme.NEON_CYAN);
            g2.drawString("CONNECTED //", textLeftOffset, 140);
        }

        g2.dispose();
    }
}