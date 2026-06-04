import java.awt.*;

public class NovaTheme {
    public static final Color OBSIDIAN = new Color(8, 9, 13);
    public static final Color NEON_CYAN = new Color(0, 245, 255);
    public static final Color MATRIX_GREEN = new Color(50, 255, 50);
    public static final Color DEEP_RED = new Color(255, 49, 49);

    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 36);
    public static final Font FONT_SUB = new Font("Segoe UI Semibold", Font.PLAIN, 18);
    public static final Font FONT_BODY = new Font("Monospaced", Font.BOLD, 14);

    public static Color getHouseColor(String house) {
        switch (house.toLowerCase().trim()) {
            case "jaguar":  return new Color(255, 49, 49);
            case "sher":    return new Color(255, 223, 0);
            case "puma":    return new Color(255, 110, 0);
            case "cheetah": return new Color(0, 191, 255);
            default:        return new Color(150, 160, 175);
        }
    }

    public static Color getGlowColor(String house) {
        switch (house.toLowerCase().trim()) {
            case "jaguar":  return new Color(130, 0, 0);
            case "sher":    return new Color(180, 140, 0);
            case "puma":    return new Color(160, 60, 0);
            case "cheetah": return new Color(0, 60, 160);
            default:        return Color.DARK_GRAY;
        }
    }
}