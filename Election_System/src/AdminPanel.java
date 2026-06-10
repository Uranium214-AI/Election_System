import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {
    private final ElectionMain masterFrame;
    private final JTextArea reportArea;

    public AdminPanel(ElectionMain masterFrame) {
        this.masterFrame = masterFrame;
        setBackground(NovaTheme.OBSIDIAN);
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("CENTRAL AUDIT GATEWAY & METRIC MONITOR");
        title.setFont(NovaTheme.FONT_HEADING);
        title.setForeground(Color.WHITE);
        add(title, BorderLayout.NORTH);

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(NovaTheme.FONT_MONO);
        reportArea.setBackground(NovaTheme.PANEL_BG);
        reportArea.setForeground(NovaTheme.MATRIX_GREEN);
        reportArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(new JScrollPane(reportArea), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        controlPanel.setBackground(NovaTheme.OBSIDIAN);

        JButton generateBtn = new JButton("COMPILE OFFICIAL DISK REPORT");
        formatButton(generateBtn, NovaTheme.NEON_CYAN);
        generateBtn.addActionListener(e -> {
            ElectionData.generateOfficialReport();
            reportArea.setText(">> [SUCCESS] Audit report updated on terminal disk storage.\n>> Target: ./ElectionStorage/official_results_report.txt");
        });

        JButton logoutBtn = new JButton("LOCK ADMIN SHELL");
        formatButton(logoutBtn, NovaTheme.DISCONNECT_RED);
        logoutBtn.addActionListener(e -> masterFrame.transitionToScreen("AUTH"));

        controlPanel.add(generateBtn);
        controlPanel.add(logoutBtn);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void formatButton(JButton btn, Color c) {
        btn.setFont(NovaTheme.FONT_SUBHEADING);
        btn.setBackground(Color.BLACK);
        btn.setForeground(c);
        btn.setBorder(BorderFactory.createLineBorder(c, 1));
        btn.setPreferredSize(new Dimension(280, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void sync() {
        reportArea.setText("Secure Admin Handshake Verified.\nSystem ready to trigger batch data writes...");
    }
}
