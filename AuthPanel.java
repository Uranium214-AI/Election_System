import javax.swing.*;
import java.awt.*;

public class AuthPanel extends JPanel {
    private JTextField grField = new JTextField(15), nameField = new JTextField(15);
    private ElectionMain parent;

    public AuthPanel(ElectionMain parent) {
        this.parent = parent;
        setLayout(new GridBagLayout());
        setBackground(new Color(240, 240, 245));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10,10,10,10);

        JLabel l = new JLabel("Enter Student Details"); l.setFont(new Font("Arial", 1, 24));
        g.gridwidth = 2; add(l, g);
        g.gridwidth = 1; g.gridy = 1; add(new JLabel("Name:"), g); g.gridx = 1; add(nameField, g);
        g.gridy = 2; g.gridx = 0; add(new JLabel("GR No:"), g); g.gridx = 1; add(grField, g);
        
        JButton b = new JButton("Login");
        b.addActionListener(e -> {
            try {
                boolean voted = ElectionMain.isClient ? 
                    NetworkClient.checkVoted(grField.getText(), nameField.getText()) :
                    ElectionData.hasVoted(grField.getText(), nameField.getText());
                if (voted) JOptionPane.showMessageDialog(this, "Already Voted!");
                else parent.startVoting(grField.getText(), nameField.getText());
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Server Offline"); }
        });
        g.gridy = 3; g.gridx = 0; g.gridwidth = 2; add(b, g);
    }
    public void reset() { grField.setText(""); nameField.setText(""); }
}
