import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Configure cross-platform system rendering properties
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception e) {
            System.err.println("System Look and Feel initialization skipped. Falling back to default layout.");
        }

        // Initialize and launch the main application interface on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            ElectionFrame appFrame = new ElectionFrame();
            appFrame.launch();
        });
    }
}
