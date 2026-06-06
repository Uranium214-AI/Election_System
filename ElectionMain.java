import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ElectionMain extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContainer = new JPanel(cardLayout);
    private AuthPanel authPanel;
    private VotingPanel votingPanel;
    private AdminPanel adminPanel;
    public static boolean isClient = false;
    private String activeGr, activeName;

    public ElectionMain() {
        setTitle("ICSE Election System 2026");
        setSize(1050, 780);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void launch() {
        String[] opts = {"Server (Admin PC)", "Client (Voter PC)"};
        int mode = JOptionPane.showOptionDialog(null, "Select Mode", "Network Setup", 0, 3, null, opts, opts[0]);
        
        try {
            if (mode == 0) {
                ElectionData.initialize();
                NetworkServer.start();
                setTitle("SERVER - IP: " + NetworkServer.getLocalIP());
            } else {
                isClient = true;
                String ip = JOptionPane.showInputDialog("Server IP:", "192.168.1.");
                NetworkClient.setServer(ip);
                NetworkClient.testConnection();
                ElectionData.nomineesByPosition = NetworkClient.fetchNominees();
            }
            initUI();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            System.exit(0);
        }
    }

    private void initUI() {
        authPanel = new AuthPanel(this);
        votingPanel = new VotingPanel(this);
        adminPanel = new AdminPanel(this);
        mainContainer.add(authPanel, "AUTH");
        mainContainer.add(votingPanel, "VOTING");
        mainContainer.add(adminPanel, "ADMIN");
        add(mainContainer);
        setVisible(true);
    }

    public void startVoting(String gr, String name) {
        this.activeGr = gr; this.activeName = name;
        votingPanel.reset();
        cardLayout.show(mainContainer, "VOTING");
    }

    public void commitVote(Map<String, String> choices) {
        try {
            if (isClient) NetworkClient.submitVote(activeGr, activeName, choices);
            else { ElectionData.recordVoter(activeGr, activeName); ElectionData.submitVotes(choices); }
            JOptionPane.showMessageDialog(this, "Vote Cast Successfully!");
            authPanel.reset();
            cardLayout.show(mainContainer, "AUTH");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Network Error!"); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ElectionMain().launch());
    }
}
