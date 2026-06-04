import javax.swing.*;
import java.awt.*;

public class AuthPanel extends JPanel {
    private final JTextField inputField = new JTextField(12);
    private final ElectionMain parent;

    public AuthPanel(ElectionMain parent) {
        this.parent = parent;
        setLayout(new GridBagLayout());
        setBackground(NovaTheme.OBSIDIAN);

        JLabel title = new JLabel("NOVA FLOW CYBER BALLOT CORE");
        title.setFont(NovaTheme.FONT_HEADING);
        title.setForeground(NovaTheme.NEON_CYAN);

        inputField.setFont(new Font("Monospaced", Font.BOLD, 30));
        inputField.setBackground(new Color(20, 22, 30));
        inputField.setForeground(Color.WHITE);
        inputField.setHorizontalAlignment(JTextField.CENTER);
        inputField.setCaretColor(NovaTheme.NEON_CYAN);
        inputField.setBorder(BorderFactory.createLineBorder(NovaTheme.NEON_CYAN, 1));

        JButton loginBtn = new JButton("AUTHENTICATE DIGITAL KEY");
        loginBtn.addActionListener(e -> attemptAccess());
        inputField.addActionListener(e -> attemptAccess());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.gridx = 0; gbc.gridy = 0; add(title, gbc);
        gbc.gridy = 1; add(inputField, gbc);
        gbc.gridy = 2; add(loginBtn, gbc);
    }

    private void attemptAccess() {
        String code = inputField.getText().trim();

        if (code.equals("9z00")) {
            parent.transitionToScreen("ADMIN");
            return;
        }

        // Regex: 1-2 digits, 1 CAPITAL letter, 1-3 digits (e.g. 9D16)
        if (!code.matches("^\\d{1,2}[A-Z]\\d{1,3}$")) {
            JOptionPane.showMessageDialog(this, "INVALID FORMAT: Use Grade + CAPS Section + Roll (e.g. 9D16)");
            return;
        }

        if (ElectionData.checkHasVoted(code)) {
            JOptionPane.showMessageDialog(this, "ACCESS DENIED: IDENTITY ALREADY LOGGED.");
        } else {
            parent.initiateVoting(code);
        }
    }

    public void reset() { inputField.setText(""); inputField.requestFocus(); }
}