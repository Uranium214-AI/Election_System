import javax.swing.*;
import java.awt.*;
import java.util.*;

public class VotingPanel extends JPanel {
    private final ElectionMain parent;
    private final JPanel scrollContainer = new JPanel();
    private final Map<String, ButtonGroup> groups = new HashMap<>();

    public VotingPanel(ElectionMain parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(NovaTheme.OBSIDIAN);

        scrollContainer.setLayout(new BoxLayout(scrollContainer, BoxLayout.Y_AXIS));
        scrollContainer.setBackground(NovaTheme.OBSIDIAN);

        JButton submit = new JButton("CONFIRM AND INJECT BALLOT");
        submit.addActionListener(e -> processSelection());
        add(new JScrollPane(scrollContainer), BorderLayout.CENTER);
        add(submit, BorderLayout.SOUTH);
    }

    public void loadNominees() {
        scrollContainer.removeAll();
        groups.clear();

        for (String category : ElectionData.nomineeMap.keySet()) {
            JLabel catLabel = new JLabel(category);
            catLabel.setFont(NovaTheme.FONT_HEADING);
            catLabel.setForeground(NovaTheme.NEON_CYAN);
            scrollContainer.add(catLabel);

            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
            row.setBackground(NovaTheme.OBSIDIAN);
            ButtonGroup bg = new ButtonGroup();

            for (String nominee : ElectionData.nomineeMap.get(category)) {
                CyberCard card = new CyberCard(nominee);
                card.setActionCommand(nominee);
                bg.add(card);
                row.add(card);
            }
            groups.put(category, bg);
            scrollContainer.add(row);
        }
        revalidate();
    }

    private void processSelection() {
        Map<String, String> selections = new HashMap<>();
        for (var entry : groups.entrySet()) {
            if (entry.getValue().getSelection() == null) {
                JOptionPane.showMessageDialog(this, "PLEASE SELECT A CANDIDATE FOR " + entry.getKey());
                return;
            }
            selections.put(entry.getKey(), entry.getValue().getSelection().getActionCommand());
        }
        parent.finalizeBallot(selections);
    }
}