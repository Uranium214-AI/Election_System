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
        ElectionData.initialize();
        
        authPanel = new AuthPanel(this);
        votingPanel = new VotingPanel(this);
        adminPanel = new AdminPanel(this);

        screenContainer.add(authPanel, "AUTH");
        screenContainer.add(votingPanel, "VOTING");
        screenContainer.add(adminPanel, "ADMIN");

        setTitle("SECURE KIOSK OS // CORE FLOW ENGINE");
        setUndecorated(true); 
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        add(screenContainer);
        setVisible(true);
    }

    public void transitionToScreen(String screenName) {
        if (screenName.equalsIgnoreCase("ADMIN")) {
            adminPanel.sync();
        }
        cardLayout.show(screenContainer, screenName);
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    public void initiateVoting(String voterID, String voterHouse) {
        this.activeVoterID = voterID;
        votingPanel.loadNominees(voterHouse);
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

        JLabel successMessage = new JLabel("  BALLOT TRANSACTION SECURED TO DISK LEDGER...");
        successMessage.setFont(NovaTheme.FONT_HEADING);
        successMessage.setForeground(NovaTheme.MATRIX_GREEN);

        MultiColorLoadingIcon loadingAnimation = new MultiColorLoadingIcon(64, successMessage);
        loadingAnimation.setStrokeThickness(6.0f);
        successMessage.setIcon(loadingAnimation);
        
        loadingAnimation.start();
        overlayWindow.add(successMessage);
        overlayWindow.setVisible(true);

        Timer shutdownTimer = new Timer(2500, event -> {
            loadingAnimation.stop(); 
            overlayWindow.dispose();
            authPanel.reset();
            transitionToScreen("AUTH");
        });
        shutdownTimer.setRepeats(false); 
        shutdownTimer.start();
    }
}
