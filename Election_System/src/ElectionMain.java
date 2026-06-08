import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ElectionMain extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel screenContainer = new JPanel(cardLayout);
    private AuthPanel authPanel;
    private VotingPanel votingPanel;
    private AdminPanel adminPanel;
    private String activeVoterID;

    public void launch() {
        // Initialize backend database and directories
        ElectionData.initialize();

        // Instantiate the UI panels
        authPanel = new AuthPanel(this);
        votingPanel = new VotingPanel(this);
        adminPanel = new AdminPanel(this);

        // Map components to the CardLayout engine
        screenContainer.add(authPanel, "AUTH");
        screenContainer.add(votingPanel, "VOTING");
        screenContainer.add(adminPanel, "ADMIN");

        setTitle("VIDYA VALLEY SECURE KIOSK // ELECTION FLOW v4.0");

        // FULL SCREEN DYNAMIC KIOSK CONFIGURATION
        setUndecorated(true); // Strips away the window taskbar and close buttons
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Forces absolute fullscreen scaling
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(screenContainer);
        setVisible(true);
    }

    public void transitionToScreen(String screenName) {
        if (screenName.equalsIgnoreCase("ADMIN")) {
            adminPanel.sync();
        }
        cardLayout.show(screenContainer, screenName);
        // Force an immediate re-draw of the UI to prevent ghosting
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    public void initiateVoting(String voterID) {
        this.activeVoterID = voterID;
        votingPanel.loadNominees();
        transitionToScreen("VOTING");
    }

    public void finalizeBallot(Map<String, String> selectedChoices) {
        ElectionData.commitBallot(activeVoterID, selectedChoices);
        showSuccessOverlay();
    }

    private void showSuccessOverlay() {
        JWindow overlayWindow = new JWindow(this);
        overlayWindow.setSize(getSize());
        overlayWindow.setLocationRelativeTo(this);
        overlayWindow.getContentPane().setBackground(Color.BLACK);
        overlayWindow.setLayout(new GridBagLayout());

        JLabel successMessage = new JLabel("  TRANSACTION SECURELY LOGGED TO LEDGER FILE...");
        successMessage.setFont(NovaTheme.FONT_HEADING);
        successMessage.setForeground(NovaTheme.MATRIX_GREEN);

        MultiColorLoadingIcon loadingAnimation = new MultiColorLoadingIcon(64, successMessage);
        loadingAnimation.setStrokeThickness(6.0f);
        successMessage.setIcon(loadingAnimation);

        loadingAnimation.start();
        overlayWindow.add(successMessage);
        overlayWindow.setVisible(true);

        // Allow the animation to play for 3 seconds before resetting the kiosk
        Timer shutdownTimer = new Timer(3000, event -> {
            loadingAnimation.stop();
            overlayWindow.dispose();
            authPanel.reset();
            transitionToScreen("AUTH");
        });
        shutdownTimer.setRepeats(false);
        shutdownTimer.start();
    }
}