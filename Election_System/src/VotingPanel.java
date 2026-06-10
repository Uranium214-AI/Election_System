import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.*;
import java.util.List;

public class VotingPanel extends JPanel {
    private final ElectionMain masterFrame;
    private final JPanel cardsDeckPanel = new JPanel(new CardLayout());
    private final List<String> processingCategories = new ArrayList<>();
    private final Map<String, ButtonGroup> registeredGroups = new HashMap<>();
    private int deckPointer = 0;
    
    private final JLabel visualIndexMetric;
    private final JButton returnStepButton;
    private final JButton advanceStepButton;

    public VotingPanel(ElectionMain masterFrame) {
        this.masterFrame = masterFrame;
        setBackground(NovaTheme.OBSIDIAN);
        setLayout(new BorderLayout());

        JPanel headerBar = new JPanel(new BorderLayout(20, 0));
        headerBar.setBackground(NovaTheme.PANEL_BG);
        headerBar.setBorder(BorderFactory.createEmptyBorder(20, 35, 20, 35));

        JLabel titleString = new JLabel("LIVE VOTING INTERFACE MATRIX");
        titleString.setFont(NovaTheme.FONT_HEADING);
        titleString.setForeground(Color.WHITE);
        
        visualIndexMetric = new JLabel("BALLOT NODE 0/0");
        visualIndexMetric.setFont(NovaTheme.FONT_MONO);
        visualIndexMetric.setForeground(NovaTheme.NEON_CYAN);

        headerBar.add(titleString, BorderLayout.WEST);
        headerBar.add(visualIndexMetric, BorderLayout.EAST);
        add(headerBar, BorderLayout.NORTH);
        
        cardsDeckPanel.setBackground(NovaTheme.OBSIDIAN);
        add(cardsDeckPanel, BorderLayout.CENTER);

        JPanel footerBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        footerBar.setBackground(NovaTheme.PANEL_BG);

        returnStepButton = new JButton("<< PREVIOUS GATE");
        formatFooterButton(returnStepButton);
        returnStepButton.addActionListener(event -> travelDeck(-1));

        advanceStepButton = new JButton("NEXT CATEGORY >>");
        formatFooterButton(advanceStepButton);
        advanceStepButton.addActionListener(event -> travelDeck(1));

        footerBar.add(returnStepButton);
        footerBar.add(advanceStepButton);
        add(footerBar, BorderLayout.SOUTH);
    }

    private void formatFooterButton(JButton button) {
        button.setFont(NovaTheme.FONT_SUBHEADING);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        button.setPreferredSize(new Dimension(220, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void loadNominees(String voterHouse) {
        cardsDeckPanel.removeAll();
        processingCategories.clear();
        registeredGroups.clear();

        for (String cat : ElectionData.nomineeMap.keySet()) {
            List<String> rawNominees = ElectionData.nomineeMap.get(cat);
            List<String> filteredNominees = new ArrayList<>();
            
            boolean isHouseSpecific = cat.toUpperCase().contains("HOUSE") || cat.toUpperCase().contains("CAPTAIN");

            for (String nomineeToken : rawNominees) {
                String[] structuralData = nomineeToken.split(",");
                String internalHouse = structuralData.length > 1 ? structuralData[1].trim() : "";

                if (isHouseSpecific) {
                    if (internalHouse.equalsIgnoreCase(voterHouse.trim())) {
                        filteredNominees.add(nomineeToken);
                    }
                } else {
                    filteredNominees.add(nomineeToken);
                }
            }

            if (filteredNominees.isEmpty()) continue;
            processingCategories.add(cat);

            JPanel outerWrapper = new JPanel(new GridBagLayout());
            outerWrapper.setBackground(NovaTheme.OBSIDIAN);

            JPanel constraintCard = new JPanel();
            constraintCard.setLayout(new BoxLayout(constraintCard, BoxLayout.Y_AXIS));
            constraintCard.setBackground(NovaTheme.CARD_BG);
            constraintCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(40, 50, 40, 50)
            ));

            JLabel titleHeading = new JLabel("ACTIVE POSITION: " + cat);
            titleHeading.setFont(new Font("Segoe UI", Font.BOLD, 22));
            titleHeading.setForeground(NovaTheme.NEON_CYAN);
            titleHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
            constraintCard.add(titleHeading);
            constraintCard.add(Box.createVerticalStrut(35));

            ButtonGroup selectionGroup = new ButtonGroup();
            JPanel candidateGridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
            candidateGridPanel.setBackground(NovaTheme.CARD_BG);
            candidateGridPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            for (String componentNode : filteredNominees) {
                String[] structuralData = componentNode.split(",");
                String individualName = structuralData[0];
                String internalHouse = structuralData.length > 1 ? structuralData[1] : "Independent Core";

                JPanel profileContainer = new JPanel(new BorderLayout(15, 10));
                profileContainer.setBackground(new Color(25, 28, 42));
                profileContainer.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(NovaTheme.getHouseColor(internalHouse), 1),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
                ));

                JPanel textDetailsCluster = new JPanel();
                textDetailsCluster.setLayout(new BoxLayout(textDetailsCluster, BoxLayout.Y_AXIS));
                textDetailsCluster.setBackground(new Color(25, 28, 42));

                JRadioButton optionRadio = new JRadioButton(individualName);
                optionRadio.setFont(NovaTheme.FONT_SUBHEADING);
                optionRadio.setForeground(Color.WHITE);
                optionRadio.setBackground(new Color(25, 28, 42));
                optionRadio.setFocusPainted(false);
                optionRadio.setActionCommand(componentNode);

                JLabel houseBadge = new JLabel(internalHouse.toUpperCase() + " HOUSE");
                houseBadge.setFont(NovaTheme.FONT_SUB);
                houseBadge.setForeground(NovaTheme.getHouseColor(internalHouse));

                JLabel photoDisplayLabel = new JLabel();
                ImageIcon circularIcon = pullCircularCandidatePhoto(individualName, 130, NovaTheme.getHouseColor(internalHouse));

                if (circularIcon != null) {
                    photoDisplayLabel.setIcon(circularIcon);
                    profileContainer.setPreferredSize(new Dimension(410, 170));
                    textDetailsCluster.add(Box.createVerticalStrut(35)); 
                    textDetailsCluster.add(optionRadio);
                    textDetailsCluster.add(Box.createVerticalStrut(8));
                    textDetailsCluster.add(houseBadge);
                    
                    profileContainer.add(photoDisplayLabel, BorderLayout.WEST);
                    profileContainer.add(textDetailsCluster, BorderLayout.CENTER);
                } else {
                    profileContainer.setPreferredSize(new Dimension(280, 110));
                    textDetailsCluster.add(Box.createVerticalStrut(15));
                    textDetailsCluster.add(optionRadio);
                    textDetailsCluster.add(Box.createVerticalStrut(6));
                    textDetailsCluster.add(houseBadge);
                    
                    profileContainer.add(textDetailsCluster, BorderLayout.CENTER);
                }

                selectionGroup.add(optionRadio);
                candidateGridPanel.add(profileContainer);
            }
            
            registeredGroups.put(cat, selectionGroup);
            constraintCard.add(candidateGridPanel);
            
            GridBagConstraints wrapGbc = new GridBagConstraints();
            wrapGbc.gridx = 0; wrapGbc.gridy = 0;
            wrapGbc.weightx = 1.0; wrapGbc.weighty = 1.0;
            wrapGbc.fill = GridBagConstraints.NONE;
            
            outerWrapper.add(constraintCard, wrapGbc);
            cardsDeckPanel.add(outerWrapper, cat);
        }

        deckPointer = 0;
        updateViewMatrix();
    }

    private ImageIcon pullCircularCandidatePhoto(String studentName, int diameter, Color borderHouseColor) {
        try {
            String filename = studentName.toLowerCase().replace(" ", "_") + ".jpg";
            File imgFile = new File("./ElectionStorage/assets/" + filename);
            if (!imgFile.exists()) return null;

            BufferedImage src = ImageIO.read(imgFile);
            int size = Math.min(src.getWidth(), src.getHeight());
            int x = (src.getWidth() - size) / 2;
            int y = (src.getHeight() - size) / 2;
            BufferedImage square = src.getSubimage(x, y, size, size);

            BufferedImage canvas = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = canvas.createGraphics();
            
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            g2d.setClip(new Ellipse2D.Float(0, 0, diameter, diameter));
            g2d.drawImage(square, 0, 0, diameter, diameter, null);

            g2d.setClip(null);
            g2d.setStroke(new BasicStroke(3.0f));
            g2d.setColor(borderHouseColor);
            g2d.drawOval(1, 1, diameter - 3, diameter - 3);

            g2d.dispose();
            return new ImageIcon(canvas);
        } catch (Exception e) {
            return null;
        }
    }

    private void travelDeck(int offsetDirection) {
        if (offsetDirection == 1 && !assertActiveSelection()) {
            JOptionPane.showMessageDialog(this, 
                "Selection Flag Missing: You must vote for a candidate to move forward.", 
                "VALIDATION FAULT", JOptionPane.WARNING_MESSAGE);
            return;
        }

        deckPointer += offsetDirection;
        if (deckPointer >= processingCategories.size()) {
            triggerTerminalUpload();
        } else {
            updateViewMatrix();
        }
    }

    private boolean assertActiveSelection() {
        String activeCategoryKey = processingCategories.get(deckPointer);
        return registeredGroups.get(activeCategoryKey).getSelection() != null;
    }

    private void updateViewMatrix() {
        CardLayout structuralLayout = (CardLayout) cardsDeckPanel.getLayout();
        structuralLayout.show(cardsDeckPanel, processingCategories.get(deckPointer));
        
        visualIndexMetric.setText(String.format("MODULE %d / %d", deckPointer + 1, processingCategories.size()));
        returnStepButton.setEnabled(deckPointer > 0);
        
        if (deckPointer == processingCategories.size() - 1) {
            advanceStepButton.setText("SUBMIT SECURE BALLOT");
            advanceStepButton.setForeground(NovaTheme.MATRIX_GREEN);
            advanceStepButton.setBorder(BorderFactory.createLineBorder(NovaTheme.MATRIX_GREEN, 1));
        } else {
            advanceStepButton.setText("NEXT CATEGORY >>");
            advanceStepButton.setForeground(Color.WHITE);
            advanceStepButton.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        }
    }

    private void triggerTerminalUpload() {
        int selectionOption = JOptionPane.showConfirmDialog(this,
                "Confirm allocations? Submissions cannot be reversed once finalized.",
                "LEDGER TRANSMISSION PROTOCOL",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (selectionOption == JOptionPane.YES_OPTION) {
            Map<String, String> finalPackagingMap = new LinkedHashMap<>();
            for (String category : processingCategories) {
                finalPackagingMap.put(category, registeredGroups.get(category).getSelection().getActionCommand());
            }
            masterFrame.finalizeBallot(finalPackagingMap);
        } else {
            deckPointer = processingCategories.size() - 1;
            updateViewMatrix();
        }
    }
}
