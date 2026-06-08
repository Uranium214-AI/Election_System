import javax.swing.*;
import java.awt.*;

public class AuthPanel extends JPanel {
    private final ElectionMain masterFrame;
    private final JTextField tokenField;
    private final JLabel systemAlertLabel;

    public AuthPanel(ElectionMain masterFrame) {
        this.masterFrame = masterFrame;
        setBackground(NovaTheme.OBSIDIAN);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NONE;

        JPanel blockContainer = new JPanel();
        blockContainer.setLayout(new BoxLayout(blockContainer, BoxLayout.Y_AXIS));
        blockContainer.setBackground(NovaTheme.PANEL_BG);
        blockContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NovaTheme.NEON_CYAN, 2),
                BorderFactory.createEmptyBorder(60, 80, 60, 80)
        ));

        JLabel titleText = new JLabel("NOVA ELECTIONS AUTH GATE");
        titleText.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleText.setForeground(Color.WHITE);
        titleText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descriptorText = new JLabel("Enter Registered Unique Voter Card ID:");
        descriptorText.setFont(NovaTheme.FONT_UI);
        descriptorText.setForeground(Color.LIGHT_GRAY);
        descriptorText.setAlignmentX(Component.CENTER_ALIGNMENT);

        tokenField = new JTextField(18);
        tokenField.setMaximumSize(new Dimension(400, 50));
        tokenField.setFont(new Font("Monospaced", Font.BOLD, 22));
        tokenField.setBackground(Color.BLACK);
        tokenField.setForeground(NovaTheme.NEON_CYAN);
        tokenField.setCaretColor(Color.WHITE);
        tokenField.setHorizontalAlignment(JTextField.CENTER);
        tokenField.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        tokenField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton processButton = new JButton("AUTHENTICATE TOKEN");
        applyActionFormatting(processButton, NovaTheme.MATRIX_GREEN);
        processButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        systemAlertLabel = new JLabel(" ");
        systemAlertLabel.setFont(NovaTheme.FONT_UI);
        systemAlertLabel.setForeground(NovaTheme.DISCONNECT_RED);
        systemAlertLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        processButton.addActionListener(event -> executeSecurityCheck());
        tokenField.addActionListener(event -> executeSecurityCheck());

        blockContainer.add(titleText);
        blockContainer.add(Box.createVerticalStrut(15));
        blockContainer.add(descriptorText);
        blockContainer.add(Box.createVerticalStrut(25));
        blockContainer.add(tokenField);
        blockContainer.add(Box.createVerticalStrut(20));
        blockContainer.add(processButton);
        blockContainer.add(Box.createVerticalStrut(20));
        blockContainer.add(systemAlertLabel);

        add(blockContainer, gbc);
    }

    private void applyActionFormatting(JButton btn, Color traceColor) {
        btn.setFont(NovaTheme.FONT_SUBHEADING);
        btn.setBackground(Color.BLACK);
        btn.setForeground(traceColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(traceColor, 1));
        btn.setMaximumSize(new Dimension(400, 50));
        btn.setPreferredSize(new Dimension(400, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void executeSecurityCheck() {
        String tokenInput = tokenField.getText().trim();
        systemAlertLabel.setText(" ");

        if (tokenInput.isEmpty()) {
            systemAlertLabel.setText("[ERROR: EMPTY VALUE DETECTED]");
            return;
        }

        if (tokenInput.equalsIgnoreCase("ADMIN")) {
            masterFrame.transitionToScreen("ADMIN");
            return;
        }

        if (ElectionData.checkHasVoted(tokenInput)) {
            systemAlertLabel.setText("[ALERT: TOKEN BALLOT PRE-EXISTENT]");
            return;
        }

        masterFrame.initiateVoting(tokenInput);
    }

    public void reset() {
        tokenField.setText("");
        systemAlertLabel.setText(" ");
    }
}