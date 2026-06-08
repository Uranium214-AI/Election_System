import java.awt.*;

public class NovaTheme {
    // Deep spatial backgrounds
    public static final Color OBSIDIAN = new Color(0x0A, 0x0B, 0x10);
    public static final Color PANEL_BG = new Color(0x12, 0x14, 0x20);
    public static final Color CARD_BG = new Color(0x1A, 0x1D, 0x2F);

    // System highlight alerts
    public static final Color NEON_CYAN = new Color(0x00, 0xF0, 0xFF);
    public static final Color MATRIX_GREEN = new Color(0x39, 0xFF, 0x14);
    public static final Color DISCONNECT_RED = new Color(0xFF, 0x31, 0x31);
    public static final Color WARN_GOLD = new Color(0xFF, 0xB0, 0x00);

    // Core application typography
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_SUBHEADING = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUB = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_UI = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FONT_MONO = new Font("Monospaced", Font.BOLD, 15);

    public static Color getHouseColor(String houseName) {
        if (houseName == null) return Color.GRAY;
        switch (houseName.toUpperCase().trim()) {
            case "SHER": return new Color(0xFF, 0xC1, 0x07);
            case "JAGUAR": return new Color(0xFF, 0x47, 0x57);
            case "CHEETAH": return new Color(0x2E, 0x86, 0xDE);
            case "PUMA": return new Color(0xFF, 0x6B, 0x6B);
            default: return new Color(0xA4, 0xB0, 0xBE);
        }
    }

    public static Color getGlowColor(String houseName) {
        if (houseName == null) return new Color(0x00, 0xF0, 0xFF, 50);
        switch (houseName.toUpperCase().trim()) {
            case "SHER": return new Color(0xFF, 0xC1, 0x07, 40);
            case "JAGUAR": return new Color(0xFF, 0x47, 0x57, 40);
            case "CHEETAH": return new Color(0x2E, 0x86, 0xDE, 40);
            case "PUMA": return new Color(0xFF, 0x6B, 0x6B, 40);
            default: return new Color(0x00, 0xF0, 0xFF, 30);
        }
    }
}