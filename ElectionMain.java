import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ElectionMain extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel container = new JPanel(cardLayout);
    private AuthPanel auth;
    private VotingPanel voting;
    private AdminPanel admin;
    private String activeVoterID;

    public void launch() {
        ElectionData.initialize();

        auth = new AuthPanel(this);
        voting = new VotingPanel(this);
        admin = new AdminPanel(this);

        container.add(auth, "AUTH");
        container.add(voting, "VOTING");
        container.add(admin, "ADMIN");

        setTitle("NOVA FLOW v2.5 // SECURE BALLOT");
        setSize(1440, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(container);
        setVisible(true);
    }

    public void transitionToScreen(String name) {
        if (name.equals("ADMIN")) admin.sync();
        cardLayout.show(container, name);
    }

    public void initiateVoting(String id) {
        this.activeVoterID = id;
        voting.loadNominees();
        transitionToScreen("VOTING");
    }

    public void finalizeBallot(Map<String, String> choices) {
        ElectionData.commitBallot(activeVoterID, choices);
        showSuccessOverlay();
    }

    private void showSuccessOverlay() {
        JWindow win = new JWindow(this);
        win.setSize(getSize());
        win.setLocationRelativeTo(this);
        win.getContentPane().setBackground(Color.BLACK);

        JLabel msg = new JLabel("TRANSACTION INJECTED TO DATA STREAM", SwingConstants.CENTER);
        msg.setFont(NovaTheme.FONT_HEADING);
        msg.setForeground(NovaTheme.MATRIX_GREEN);
        win.add(msg);
        win.setVisible(true);

        Timer t = new Timer(3000, e -> {
            win.dispose();
            auth.reset();
            transitionToScreen("AUTH");
        });
        t.setRepeats(false); // STOP LOOP BUG
        t.start();
    }
}