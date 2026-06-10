import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class AdminPanel extends JPanel {
    private final ElectionMain masterFrame;
    private final javax.swing.Timer repaintTimer;
    private final List<PixelCluster> backgroundClusters = new ArrayList<>();
    private double dialTurnoutProgress = 0;
    private int auditedUniqueVoterTotal = 0;
    private final Map<String, Double> graphicVelocityBars = new HashMap<>();

    public AdminPanel(ElectionMain masterFrame) {
        this.masterFrame = masterFrame;
        setBackground(NovaTheme.OBSIDIAN);
        setLayout(new BorderLayout());

        repaintTimer = new javax.swing.Timer(16, event -> {
            executeVectorCalculations();
            repaint();
        });

        JPanel systemHeaderBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        systemHeaderBar.setBackground(NovaTheme.OBSIDIAN);

        JButton disconnectButton = new JButton("<< RETURN TO AUTH");
        applyHeaderButtonLayout(disconnectButton, NovaTheme.WARN_GOLD);
        disconnectButton.addActionListener(event -> {
            repaintTimer.stop();
            masterFrame.transitionToScreen("AUTH");
        });

        JButton compileReportButton = new JButton("GENERATE OFFICIAL REPORT");
        applyHeaderButtonLayout(compileReportButton, NovaTheme.MATRIX_GREEN);
        compileReportButton.addActionListener(event -> {
            ElectionData.generateOfficialReport();
            JOptionPane.showMessageDialog(this,
                    "REPORT COMPILED AND SECURELY WRITTEN TO DISK.\nTarget: ElectionStorage/official_results_report.txt",
                    "SYSTEM RECONSTRUCTION METRICS",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        JButton exitAppButton = new JButton("SHUTDOWN SYSTEM");
        applyHeaderButtonLayout(exitAppButton, NovaTheme.DISCONNECT_RED);
        exitAppButton.addActionListener(event -> System.exit(0)); // Crucial safe-exit for fullscreen wrapper

        systemHeaderBar.add(disconnectButton);
        systemHeaderBar.add(compileReportButton);
        systemHeaderBar.add(exitAppButton);
        add(systemHeaderBar, BorderLayout.NORTH);
    }

    private void applyHeaderButtonLayout(JButton btn, Color paintColor) {
        btn.setFont(NovaTheme.FONT_MONO);
        btn.setBackground(new Color(20, 22, 33));
        btn.setForeground(paintColor);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(paintColor, 1));
        btn.setPreferredSize(new Dimension(280, 45));
    }

    public void sync() {
        int latestUniqueLines = ElectionData.getVoterCount();
        if (latestUniqueLines > auditedUniqueVoterTotal) {
            int netDifferential = latestUniqueLines - auditedUniqueVoterTotal;
            for (int i = 0; i < netDifferential * 15; i++) {
                backgroundClusters.add(new PixelCluster(getWidth(), getHeight()));
            }
            auditedUniqueVoterTotal = latestUniqueLines;
        }
        repaintTimer.start();
    }

    private void executeVectorCalculations() {
        double targetedLimitValue = (double) auditedUniqueVoterTotal / 1000.0;
        if (dialTurnoutProgress < targetedLimitValue) dialTurnoutProgress += 0.005;
        if (dialTurnoutProgress > targetedLimitValue) dialTurnoutProgress = targetedLimitValue;

        for (Map.Entry<String, Map<String, Integer>> operationalCategory : ElectionData.voteTally.entrySet()) {
            String trackingCategoryName = operationalCategory.getKey();
            for (Map.Entry<String, Integer> targetNomineeNode : operationalCategory.getValue().entrySet()) {
                String identificationString = trackingCategoryName + "_" + targetNomineeNode.getKey();
                double graphicValuePointer = graphicVelocityBars.getOrDefault(identificationString, 0.0);
                double absoluteDatabaseValue = targetNomineeNode.getValue();

                if (graphicValuePointer < absoluteDatabaseValue) {
                    graphicValuePointer += 0.05;
                    if (graphicValuePointer > absoluteDatabaseValue) graphicValuePointer = absoluteDatabaseValue;
                    graphicVelocityBars.put(identificationString, graphicValuePointer);
                }
            }
        }

        Point2D pullCoordinateCenter = new Point2D.Double(getWidth() / 4.0, getHeight() / 2.0);
        for (int i = backgroundClusters.size() - 1; i >= 0; i--) {
            PixelCluster systemNode = backgroundClusters.get(i);
            systemNode.advanceStep(pullCoordinateCenter);
            if (systemNode.extinguished) backgroundClusters.remove(i);
        }
    }

    @Override
    protected void paintComponent(Graphics graphicContext) {
        super.paintComponent(graphicContext);
        Graphics2D renderingPipeline = (Graphics2D) graphicContext.create();
        renderingPipeline.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        renderingPipeline.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int gridWidth = getWidth();
        int gridHeight = getHeight();

        int middleX = gridWidth / 4;
        int middleY = gridHeight / 2;
        int circleRadius = Math.min(350, gridHeight / 3);

        renderingPipeline.setStroke(new BasicStroke(16f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        renderingPipeline.setColor(new Color(25, 28, 40));
        renderingPipeline.drawOval(middleX - circleRadius, middleY - circleRadius, circleRadius * 2, circleRadius * 2);

        renderingPipeline.setColor(NovaTheme.NEON_CYAN);
        renderingPipeline.draw(new Arc2D.Double(middleX - circleRadius, middleY - circleRadius, circleRadius * 2, circleRadius * 2, 90, -dialTurnoutProgress * 360, Arc2D.OPEN));

        renderingPipeline.setFont(new Font("Monospaced", Font.BOLD, 42));
        renderingPipeline.setColor(Color.WHITE);
        String counterText = auditedUniqueVoterTotal + " VBT";
        renderingPipeline.drawString(counterText, middleX - renderingPipeline.getFontMetrics().stringWidth(counterText) / 2, middleY - 5);

        renderingPipeline.setFont(new Font("Segoe UI", Font.BOLD, 16));
        renderingPipeline.setColor(NovaTheme.MATRIX_GREEN);
        String ratioPercentText = String.format("%.1f%% COMPLETE", dialTurnoutProgress * 100);
        renderingPipeline.drawString(ratioPercentText, middleX - renderingPipeline.getFontMetrics().stringWidth(ratioPercentText) / 2, middleY + 45);

        for (PixelCluster singleNode : backgroundClusters) singleNode.render(renderingPipeline);

        int sectionLeftX = gridWidth / 2;
        int sectionTopY = 120;
        int structuralWidthBounds = (gridWidth / 2) - 80;

        renderingPipeline.setFont(new Font("Segoe UI", Font.BOLD, 28));
        renderingPipeline.setColor(NovaTheme.NEON_CYAN);
        renderingPipeline.drawString("LIVE METRIC CHART DISTRIBUTION", sectionLeftX, sectionTopY - 45);

        int currentDrawHeight = sectionTopY;
        for (Map.Entry<String, Map<String, Integer>> categoryElement : ElectionData.voteTally.entrySet()) {
            String localizedCategoryName = categoryElement.getKey();
            Map<String, Integer> studentMapData = categoryElement.getValue();

            renderingPipeline.setFont(new Font("Segoe UI", Font.BOLD, 18));
            renderingPipeline.setColor(Color.WHITE);
            renderingPipeline.drawString("// " + localizedCategoryName, sectionLeftX, currentDrawHeight);
            currentDrawHeight += 24;

            int dynamicPeakVoteValue = 1;
            for (int individualVoteScore : studentMapData.values()) {
                if (individualVoteScore > dynamicPeakVoteValue) dynamicPeakVoteValue = individualVoteScore;
            }

            List<Map.Entry<String, Integer>> processSortingList = new ArrayList<>(studentMapData.entrySet());
            final List<String> immutableStructuralOrder = ElectionData.nomineeMap.get(localizedCategoryName);

            processSortingList.sort((node1, node2) -> {
                int scoreCheck = node2.getValue().compareTo(node1.getValue());
                if (scoreCheck != 0) return scoreCheck;
                return Integer.compare(immutableStructuralOrder.indexOf(node1.getKey()), immutableStructuralOrder.indexOf(node2.getKey()));
            });

            int universalLeaderHeadCount = 0;
            for (int valuesIterator : studentMapData.values()) {
                if (valuesIterator == dynamicPeakVoteValue) universalLeaderHeadCount++;
            }

            for (Map.Entry<String, Integer> loopRowEntry : processSortingList) {
                String encodedKeyString = loopRowEntry.getKey();
                String[] parsedArrayTokens = encodedKeyString.split(",");
                String targetStudentName = parsedArrayTokens[0];
                String targetHouseGroup = parsedArrayTokens.length > 1 ? parsedArrayTokens[1] : "Independent Core";
                int individualTotalVotes = loopRowEntry.getValue();

                String relationalGraphKey = localizedCategoryName + "_" + encodedKeyString;
                double activeRenderWidthScore = graphicVelocityBars.getOrDefault(relationalGraphKey, 0.0);
                Color houseAestheticColor = NovaTheme.getHouseColor(targetHouseGroup);

                renderingPipeline.setFont(new Font("Monospaced", Font.BOLD, 15));
                renderingPipeline.setColor(Color.LIGHT_GRAY);
                String labelStringTrace = String.format("%-18s [%d V]", targetStudentName, individualTotalVotes);
                renderingPipeline.drawString(labelStringTrace, sectionLeftX, currentDrawHeight + 16);

                int layoutOffsetStartBarX = sectionLeftX + 220;
                int graphicMaxLimitWidth = structuralWidthBounds - 350;
                int currentComputedBarWidth = (int) ((activeRenderWidthScore / dynamicPeakVoteValue) * graphicMaxLimitWidth);
                currentComputedBarWidth = Math.max(8, currentComputedBarWidth);

                renderingPipeline.setColor(new Color(25, 28, 40));
                renderingPipeline.fillRoundRect(layoutOffsetStartBarX, currentDrawHeight + 4, graphicMaxLimitWidth, 16, 8, 8);

                renderingPipeline.setColor(houseAestheticColor);
                renderingPipeline.fillRoundRect(layoutOffsetStartBarX, currentDrawHeight + 4, currentComputedBarWidth, 16, 8, 8);

                if (individualTotalVotes == dynamicPeakVoteValue && individualTotalVotes > 0) {
                    if (universalLeaderHeadCount > 1) {
                        renderingPipeline.setColor(NovaTheme.NEON_CYAN);
                        renderingPipeline.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        renderingPipeline.drawString("TIED LEADING", layoutOffsetStartBarX + graphicMaxLimitWidth + 18, currentDrawHeight + 16);
                    } else {
                        renderingPipeline.setColor(NovaTheme.MATRIX_GREEN);
                        renderingPipeline.setFont(new Font("Segoe UI", Font.BOLD, 13));
                        renderingPipeline.drawString("LEADING", layoutOffsetStartBarX + graphicMaxLimitWidth + 18, currentDrawHeight + 16);
                    }
                }
                currentDrawHeight += 32;
            }
            currentDrawHeight += 25;
        }
        renderingPipeline.dispose();
    }

    private static class PixelCluster {
        double currentPositionX, currentPositionY;
        double speedX, speedY;
        boolean extinguished = false;

        public PixelCluster(int horizontalFrameBound, int verticalFrameBound) {
            Random randomGenerator = new Random();
            currentPositionX = randomGenerator.nextInt(Math.max(1, horizontalFrameBound));
            currentPositionY = randomGenerator.nextInt(Math.max(1, verticalFrameBound));
        }

        void advanceStep(Point2D gravitationalPullCenter) {
            double distanceDeltaX = gravitationalPullCenter.getX() - currentPositionX;
            double distanceDeltaY = gravitationalPullCenter.getY() - currentPositionY;
            double aggregateHypotenuse = Math.hypot(distanceDeltaX, distanceDeltaY);

            if (aggregateHypotenuse < 25) {
                extinguished = true;
                return;
            }

            speedX += (distanceDeltaX / aggregateHypotenuse) * 0.50;
            speedY += (distanceDeltaY / aggregateHypotenuse) * 0.50;
            currentPositionX += speedX;
            currentPositionY += speedY;
            speedX *= 0.90;
            speedY *= 0.90;
        }

        void render(Graphics2D canvasPipeline) {
            canvasPipeline.setColor(NovaTheme.NEON_CYAN);
            canvasPipeline.fill(new Ellipse2D.Double(currentPositionX, currentPositionY, 3.5, 3.5));
        }
    }
}