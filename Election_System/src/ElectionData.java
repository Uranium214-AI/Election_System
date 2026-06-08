import java.io.*;
import java.util.*;

public class ElectionData {
    public static final String DIR = "./ElectionStorage/";
    private static final String REGISTRY = DIR + "voter_registry.txt";
    private static final String BALLOT_LOG = DIR + "final_tallies.csv";
    private static final String NOMINEES_CSV = DIR + "nominees.csv";

    public static Map<String, List<String>> nomineeMap = new LinkedHashMap<>();
    public static Map<String, Map<String, Integer>> voteTally = new LinkedHashMap<>();

    public static void initialize() {
        try {
            File storageDir = new File(DIR);
            if (!storageDir.exists()) storageDir.mkdirs();
            File assetsDir = new File(DIR + "assets/");
            if (!assetsDir.exists()) assetsDir.mkdirs();

            File debugFile = new File(NOMINEES_CSV);
            System.out.println("DEBUG>> SYSTEM IS LOOKING FOR CSV AT :"+ debugFile.getAbsolutePath());
            System.out.println("DEBUG>> DOES FILE EXIST"+ debugFile.exists());
            System.out.println("DEBUG >> IS FILE READABLE"+debugFile.canRead());
            // Core initialization sequence
            setupCategories(); // Now fully declared below!
            boolean loadedFromCSV = loadNomineesFromCSV();

            if (!loadedFromCSV) {
                System.out.println("[SYSTEM INFO] nominees.csv not found or empty. Injecting hardcoded fallback team defaults...");
                injectFallbackDefaults();
            }

            loadHistoricalLogs();
        } catch (Exception e) {
            System.err.println("CRITICAL FAULT: Storage write access initialization failed.");
        }
    }

    // THE MISSING METHOD: Pre-configures the core Map structures for each standard category
    private static void setupCategories() {
        String[] categories = {
                "HEAD PREFECT BOY",
                "HEAD GIRL",
                "SPORTS PREFECT BOY",
                "HOUSE CAPTAIN",
                "HOUSE VICE CAPTAIN"
        };

        for (String cat : categories) {
            nomineeMap.put(cat, new ArrayList<>());
            voteTally.put(cat, new HashMap<>());
        }
    }

    private static boolean loadNomineesFromCSV() {
        File csvFile = new File(NOMINEES_CSV);
        if (!csvFile.exists()) {
            return false;
        }

        boolean recordsFound = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue; // Skip empty rows or comments

                String[] segments = line.split(",");
                if (segments.length >= 3) {
                    String category = segments[0].trim();
                    String studentName = segments[1].trim();
                    String schoolHouse = segments[2].trim();

                    // Dynamically generate category maps if a brand new one is introduced in the CSV
                    if (!nomineeMap.containsKey(category)) {
                        nomineeMap.put(category, new ArrayList<>());
                        voteTally.put(category, new HashMap<>());
                    }

                    addNominee(category, studentName, schoolHouse);
                    recordsFound = true;
                }
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to read nominees.csv from disk. Falling back to default system arrays.");
            return false;
        }
        return recordsFound;
    }

    private static void injectFallbackDefaults() {
        addNominee("HEAD PREFECT BOY", "Kartik M", "Jaguar");
        addNominee("HEAD PREFECT BOY", "Shlok S", "Sher");
        addNominee("HEAD GIRL", "Navya P", "Cheetah");
        addNominee("HEAD GIRL", "Anusha Kotnis", "Puma");
        addNominee("SPORTS PREFECT BOY", "Vihaan", "Jaguar");
        addNominee("SPORTS PREFECT BOY", "Jasveer P", "Cheetah");
        addNominee("HOUSE CAPTAIN", "arjun s", "Sher");
        addNominee("HOUSE CAPTAIN", "Eshaan S", "Puma");
        addNominee("HOUSE VICE CAPTAIN", "Parth K", "Jaguar");
        addNominee("HOUSE VICE CAPTAIN", "Adhrit D", "Cheetah");
    }

    private static void addNominee(String category, String studentName, String schoolHouse) {
        String compoundKey = studentName + "," + schoolHouse;
        // Prevent duplicate entries of the same candidate in the same category loop
        if (!nomineeMap.get(category).contains(compoundKey)) {
            nomineeMap.get(category).add(compoundKey);
            voteTally.get(category).put(compoundKey, 0);
        }
    }

    public static boolean checkHasVoted(String identificationToken) {
        File registryFile = new File(REGISTRY);
        if (!registryFile.exists()) return false;

        try (Scanner fileScanner = new Scanner(new BufferedReader(new FileReader(registryFile)))) {
            while (fileScanner.hasNextLine()) {
                if (fileScanner.nextLine().trim().equalsIgnoreCase(identificationToken.trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Database verification read timeout.");
        }
        return false;
    }

    public static int getVoterCount() {
        File registryFile = new File(REGISTRY);
        if (!registryFile.exists()) return 0;

        int lineCounter = 0;
        try (Scanner fileScanner = new Scanner(new BufferedReader(new FileReader(registryFile)))) {
            while (fileScanner.hasNextLine()) {
                if (!fileScanner.nextLine().trim().isEmpty()) {
                    lineCounter++;
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read voter registries safely.");
        }
        return lineCounter;
    }

    public static void commitBallot(String voterID, Map<String, String> completedSelections) {
        synchronized (ElectionData.class) {
            try (BufferedWriter registryWriter = new BufferedWriter(new FileWriter(REGISTRY, true));
                 BufferedWriter logWriter = new BufferedWriter(new FileWriter(BALLOT_LOG, true))) {

                registryWriter.write(voterID.trim());
                registryWriter.newLine();

                for (Map.Entry<String, String> selection : completedSelections.entrySet()) {
                    logWriter.write(String.format("%s,%s,%s", voterID.trim(), selection.getKey(), selection.getValue()));
                    logWriter.newLine();

                    Map<String, Integer> subTally = voteTally.get(selection.getKey());
                    if (subTally != null && subTally.containsKey(selection.getValue())) {
                        subTally.put(selection.getValue(), subTally.get(selection.getValue()) + 1);
                    }
                }
            } catch (IOException e) {
                System.err.println("Critical write execution drop encountered.");
            }
        }
    }

    private static void loadHistoricalLogs() {
        File logFile = new File(BALLOT_LOG);
        if (!logFile.exists()) return;

        try (Scanner fileScanner = new Scanner(new BufferedReader(new FileReader(logFile)))) {
            while (fileScanner.hasNextLine()) {
                String lineEntry = fileScanner.nextLine().trim();
                if (lineEntry.isEmpty()) continue;

                String[] segments = lineEntry.split(",");
                if (segments.length >= 3) {
                    String coreCategory = segments[1].trim();
                    String candidateNameKey = segments[2].trim();
                    if (segments.length == 4) {
                        candidateNameKey += "," + segments[3].trim();
                    }

                    Map<String, Integer> targetedMap = voteTally.get(coreCategory);
                    if (targetedMap != null && targetedMap.containsKey(candidateNameKey)) {
                        targetedMap.put(candidateNameKey, targetedMap.get(candidateNameKey) + 1);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Historical log trace reset executed.");
        }
    }

    public static void generateOfficialReport() {
        String outputFilePath = DIR + "official_results_report.txt";
        try (PrintWriter reportWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFilePath)))) {
            reportWriter.println("=======================================================");
            reportWriter.println("    VIDYA VALLEY SCHOOL ELECTION - OFFICIAL RESULTS    ");
            reportWriter.println("=======================================================");
            reportWriter.println("System Audited Unique Ballots: " + getVoterCount());
            reportWriter.println("=======================================================\n");

            for (Map.Entry<String, Map<String, Integer>> categoryBlock : voteTally.entrySet()) {
                String moduleTitle = categoryBlock.getKey();
                Map<String, Integer> numericDataset = categoryBlock.getValue();

                List<Map.Entry<String, Integer>> sortingList = new ArrayList<>(numericDataset.entrySet());
                final List<String> referenceBallotStructure = nomineeMap.get(moduleTitle);

                sortingList.sort((entry1, entry2) -> {
                    int quantitativeComparison = entry2.getValue().compareTo(entry1.getValue());
                    if (quantitativeComparison != 0) return quantitativeComparison;
                    return Integer.compare(referenceBallotStructure.indexOf(entry1.getKey()), referenceBallotStructure.indexOf(entry2.getKey()));
                });

                reportWriter.println(">>> BALLOT CATEGORY NODE: " + moduleTitle);
                if (sortingList.isEmpty()) {
                    reportWriter.println("    [!] STATUS: No Candidate Entries Registered inside Node.");
                } else {
                    int highestRecordedVote = sortingList.get(0).getValue();
                    int structuralTieCount = 0;
                    for (Map.Entry<String, Integer> node : sortingList) {
                        if (node.getValue() == highestRecordedVote) structuralTieCount++;
                    }

                    if (structuralTieCount > 1 && highestRecordedVote > 0) {
                        reportWriter.print("    INTEGRITY LOG: TIED FIRST PLACE DETECTED\n    WINNERS: ");
                        for (int i = 0; i < structuralTieCount; i++) {
                            reportWriter.print(sortingList.get(i).getKey().split(",")[0] + " [" + highestRecordedVote + " V]");
                            if (i < structuralTieCount - 1) reportWriter.print(" && ");
                        }
                        reportWriter.println();
                    } else {
                        reportWriter.println("    ELECTED CONSTITUENT WINNER: " + sortingList.get(0).getKey().split(",")[0] + " [" + highestRecordedVote + " V]");
                    }

                    reportWriter.println("\n    Linear Metric Distribution Matrix:");
                    for (Map.Entry<String, Integer> targetRow : sortingList) {
                        reportWriter.printf("     -> %-18s : %d Total Votes\n", targetRow.getKey().split(",")[0], targetRow.getValue());
                    }
                }
                reportWriter.println("-------------------------------------------------------");
            }
            reportWriter.println("SYSTEM STORAGE DATA INTERFACES TERMINATED SECURELY.");
        } catch (IOException e) {
            System.err.println("Failed to write output stream to text block file.");
        }
    }
}