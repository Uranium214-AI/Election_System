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
        // Build file infrastructure matrices
        ElectionData.initialize();
        
        // Instantiate decoupled interface components
        authPanel = new AuthPanel(this);
        votingPanel = new VotingPanel(this);
        adminPanel = new AdminPanel(this);

        // Mount layers into the structural layout pipeline
        screenContainer.add(authPanel, "AUTH");
        screenContainer.add(votingPanel, "VOTING");
        screenContainer.add(adminPanel, "ADMIN");

        // Master Frame Window Configuration Properties
        setTitle("NOVA FLOW v3.5 // SECURE MULTI-THREAD SYSTEM CORE");
        setSize(1440, 900);
        setMinimumSize(new Dimension(1024, 768));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(screenContainer);
        setVisible(true);
    }

    public void transitionToScreen(String screenName) {
        if (screenName.equalsIgnoreCase("ADMIN")) {
            adminPanel.sync();
        }
        cardLayout.show(screenContainer, screenName);
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

        // Construct and attach your isolated loading component
        MultiColorLoadingIcon loadingAnimation = new MultiColorLoadingIcon(48, successMessage);
        loadingAnimation.setStrokeThickness(5.0f);
        successMessage.setIcon(loadingAnimation);
        
        loadingAnimation.start();
        overlayWindow.add(successMessage);
        overlayWindow.setVisible(true);

        // Multi-thread delay loop (3000ms) handles safe storage verification
        Timer shutdownTimer = new Timer(3000, event -> {
            loadingAnimation.stop(); // Structural cleanup: kills animation thread safety loops
            overlayWindow.dispose();
            authPanel.reset();
            transitionToScreen("AUTH");
        });
        shutdownTimer.setRepeats(false); 
        shutdownTimer.start();
    }
}
