import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Enforce native UI look-and-feel properties safely
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Boot the safe system pipeline on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            ElectionData.initialize();

            ElectionMain appFrame = new ElectionMain();
            appFrame.launch();
        });
    }
}