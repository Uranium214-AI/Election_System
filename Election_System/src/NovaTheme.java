import java.awt.*;

public class NovaTheme {
    public static final Color OBSIDIAN = new Color(10, 11, 16);
    public static final Color PANEL_BG = new Color(18, 20, 29);
    public static final Color CARD_BG = new Color(24, 26, 38);
    public static final Color NEON_CYAN = new Color(0, 229, 255);
    public static final Color MATRIX_GREEN = new Color(0, 230, 118);
    public static final Color DISCONNECT_RED = new Color(255, 23, 68);

    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SUBHEADING = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_UI = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_MONO = new Font("Monospaced", Font.BOLD, 14);
    public static final Font FONT_SUB = new Font("Segoe UI", Font.ITALIC, 12);

    public static Color getHouseColor(String houseName) {
        switch (houseName.trim().toUpperCase()) {
            case "SHER": return new Color(255, 112, 67);       // Deep Orange
            case "JAGUAR": return new Color(255, 202, 40);     // Amber Yellow
            case "CHEETAH": return new Color(38, 166, 154);    // Emerald Teal
            case "PUMA": return new Color(149, 117, 205);      // Deep Purple
            default: return Color.LIGHT_GRAY;
        }
    }
}
