import java.io.*;
import java.util.*;
/*standalone model exparimental*/
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
            
            setupCategories();
            boolean loadedFromCSV = loadNomineesFromCSV();
            
            if (!loadedFromCSV) {
                injectFallbackDefaults();
            }
            loadHistoricalLogs();
        } catch (Exception e) {
            System.err.println("CRITICAL FAULT: Storage system initialization failure.");
        }
    }

    private static void setupCategories() {
        String[] categories = {"HEAD PREFECT BOY", "HEAD GIRL", "SPORTS PREFECT BOY", "HOUSE CAPTAIN", "HOUSE VICE CAPTAIN"};
        for (String cat : categories) {
            nomineeMap.put(cat, new ArrayList<>());
            voteTally.put(cat, new HashMap<>());
        }
    }

    private static boolean loadNomineesFromCSV() {
        File csvFile = new File(NOMINEES_CSV);
        if (!csvFile.exists()) return false;

        boolean recordsFound = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] segments = line.split(",");
                if (segments.length >= 3) {
                    String category = segments[0].trim();
                    String studentName = segments[1].trim();
                    String schoolHouse = segments[2].trim();

                    if (!nomineeMap.containsKey(category)) {
                        nomineeMap.put(category, new ArrayList<>());
                        voteTally.put(category, new HashMap<>());
                    }
                    addNominee(category, studentName, schoolHouse);
                    recordsFound = true;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return recordsFound;
    }

    private static void injectFallbackDefaults() {
        addNominee("HEAD PREFECT BOY", "Kartik M", "Jaguar");
        addNominee("HEAD PREFECT BOY", "Shlok S", "Sher");
        addNominee("HEAD GIRL", "Navya P", "Cheetah");
        addNominee("HEAD GIRL", "Anusha Kotnis", "Puma");
    }

    private static void addNominee(String category, String studentName, String schoolHouse) {
        String compoundKey = studentName + "," + schoolHouse;
        if (!nomineeMap.get(category).contains(compoundKey)) {
            nomineeMap.get(category).add(compoundKey);
            voteTally.get(category).put(compoundKey, 0);
        }
    }

    public static boolean checkHasVoted(String identificationToken) {
        File registryFile = new File(REGISTRY);
        if (!registryFile.exists()) return false;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(registryFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equalsIgnoreCase(identificationToken.trim())) return true;
            }
        } catch (IOException e) {/**/}
        return false;
    }

    public static void commitBallot(String voterID, Map<String, String> completedSelections) {
        synchronized (ElectionData.class) {
            try (BufferedWriter registryWriter = new BufferedWriter(new FileWriter(REGISTRY, true));
                 BufferedWriter logWriter = new BufferedWriter(new FileWriter(BALLOT_LOG, true))) {
                
                registryWriter.write(voterID.trim());
                registryWriter.newLine();
                registryWriter.flush();

                for (Map.Entry<String, String> selection : completedSelections.entrySet()) {
                    logWriter.write(String.format("%s,%s,%s", voterID.trim(), selection.getKey(), selection.getValue()));
                    logWriter.newLine();
                }
                logWriter.flush();
            } catch (IOException e) {/**/}
        }
    }

    private static void loadHistoricalLogs() {
        File logFile = new File(BALLOT_LOG);
        if (!logFile.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] segments = line.trim().split(",");
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
        } catch (Exception e) {/**/}
    }

    public static void generateOfficialReport() {
        synchronized (ElectionData.class) {
            String outputFilePath = DIR + "official_results_report.txt";
            try (PrintWriter reportWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputFilePath)))) {
                reportWriter.println("=======================================================");
                reportWriter.println("        ELECTION METRIC EXPORT SYSTEM - SYSTEM STATUS ");
                reportWriter.println("=======================================================");
                
                for (Map.Entry<String, Map<String, Integer>> categoryBlock : voteTally.entrySet()) {
                    reportWriter.println("\n>>> CATEGORY TALLY PROFILE: " + categoryBlock.getKey());
                    for (Map.Entry<String, Integer> targetRow : categoryBlock.getValue().entrySet()) {
                        reportWriter.printf("   -> %-22s : %d Valid Ballots\n", targetRow.getKey().split(",")[0], targetRow.getValue());
                    }
                }
            } catch (IOException e) {/**/}
        }
    }
}
