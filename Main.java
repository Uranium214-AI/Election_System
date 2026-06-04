import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Enforce hardware acceleration for the particle engines
        System.setProperty("sun.java2d.opengl", "true");
        SwingUtilities.invokeLater(() -> new ElectionMain().launch());
    }
}