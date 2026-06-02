import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class VotingPanel extends JPanel {
    private ElectionMain parent;
    private Map<String, String> choices = new HashMap<>();
    private List<AbstractButton> btns = new ArrayList<>();

    public VotingPanel(ElectionMain parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();

        for (String pos : ElectionData.nomineesByPosition.keySet()) {
            JPanel p = new JPanel();
            ButtonGroup bg = new ButtonGroup();
            for (String nom : ElectionData.nomineesByPosition.get(pos)) {
                JToggleButton b = new JToggleButton(nom);
                b.setPreferredSize(new Dimension(200, 100));
                b.addActionListener(e -> choices.put(pos, nom));
                bg.add(b); btns.add(b); p.add(b);
            }
            tabs.addTab(pos, p);
        }
        JButton sub = new JButton("SUBMIT VOTE");
        sub.addActionListener(e -> parent.commitVote(choices));
        add(tabs, BorderLayout.CENTER); add(sub, BorderLayout.SOUTH);
    }
    public void reset() { choices.clear(); for(var b : btns) b.setSelected(false); }
}
