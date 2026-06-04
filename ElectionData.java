import java.io.*;
import java.util.*;

public class ElectionData {
    private static final String DIR = "./ElectionStorage/";
    private static final String REGISTRY = DIR + "voter_registry.txt";
    private static final String BALLOT_LOG = DIR + "final_tallies.csv";

    public static Map<String, List<String>> nomineeMap = new LinkedHashMap<>();
    public static Map<String, Map<String, Integer>> voteTally = new LinkedHashMap<>();

    public static void initialize() {
        new File(DIR).mkdirs();
        // Setup default categories if needed
        setupDefaults();
    }

    private static void setupDefaults() {
        String[] categories = {"HEAD BOY", "HEAD GIRL", "SPORTS CAPTAIN"};
        for (String c : categories) {
            nomineeMap.put(c, new ArrayList<>());
            voteTally.put(c, new HashMap<>());
        }
        // Manual entry for testing (Normally loaded from CSV)
        addNominee("HEAD BOY", "Kartik M", "Jaguar");
        addNominee("HEAD BOY", "Kanav Desai", "Sher");
        addNominee("HEAD GIRL", "Sneha Rao", "Cheetah");
        addNominee("SPORTS CAPTAIN", "Amit Singh", "Cheetah");
    }

    private static void addNominee(String cat, String name, String house) {
        String data = name + "," + house;
        nomineeMap.get(cat).add(data);
        voteTally.get(cat).put(data, 0);
    }

    public static boolean checkHasVoted(String id) {
        try (Scanner s = new Scanner(new File(REGISTRY))) {
            while (s.hasNextLine()) if (s.nextLine().equals(id)) return true;
        } catch (Exception e) { /* File might not exist yet */ }
        return false;
    }

    public static void commitBallot(String id, Map<String, String> votes) {
        try {
            BufferedWriter rw = new BufferedWriter(new FileWriter(REGISTRY, true));
            rw.write(id); rw.newLine(); rw.close();

            BufferedWriter bw = new BufferedWriter(new FileWriter(BALLOT_LOG, true));
            for (var entry : votes.entrySet()) {
                bw.write(id + "," + entry.getKey() + "," + entry.getValue());
                bw.newLine();
                // Update live memory tally for Admin Panel
                Map<String, Integer> catTally = voteTally.get(entry.getKey());
                catTally.put(entry.getValue(), catTally.get(entry.getValue()) + 1);
            }
            bw.close();
        } catch (IOException e) { e.printStackTrace(); }
    }
}