import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ElectionData {
    public static final String WORK_DIR = "./ElectionSystem/";
    private static final String NOMINEES_FILE = WORK_DIR + "nominees.csv";
    private static final String VOTERS_FILE = WORK_DIR + "voted_registry.txt";
    private static final String RESULTS_FILE = WORK_DIR + "election_results.csv";

    public static Map<String, List<String>> nomineesByPosition = new LinkedHashMap<>();
    public static Map<String, Map<String, Integer>> voteTally = new ConcurrentHashMap<>();

    public static void initialize() {
        new File(WORK_DIR).mkdirs();
        File csvFile = new File(NOMINEES_FILE);
        if (!csvFile.exists()) generateDefaultNomineesFile();

        try (BufferedReader r = new BufferedReader(new FileReader(NOMINEES_FILE))) {
            String line;
            while((line = r.readLine()) != null) {
                String[] data = line.split(",");
                if(data.length >= 2) {
                    String pos = data[0].trim();
                    String details = data[1].trim() + (data.length > 2 ? "," + data[2].trim() : ",Default");
                    nomineesByPosition.putIfAbsent(pos, new ArrayList<>());
                    nomineesByPosition.get(pos).add(details);
                }
            }
        } catch (Exception e) { System.out.println("Init Error: " + e.getMessage()); }
    }

    private static void generateDefaultNomineesFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(NOMINEES_FILE))) {
            pw.println("Head Boy,Aryan Sharma,Puma");
            pw.println("Head Boy,Rahul Desai,Sher");
            pw.println("Head Girl,Priya Patel,Cheetah");
            pw.println("Sports Captain,Rohan Gupta,Jaguar");
        } catch (IOException e) {}
    }

    public static synchronized boolean hasVoted(String gr, String name) {
        File f = new File(VOTERS_FILE);
        if (!f.exists()) return false;
        try (Scanner s = new Scanner(f)) {
            while (s.hasNextLine()) {
                if (s.nextLine().trim().equalsIgnoreCase(gr + "," + name)) return true;
            }
        } catch (Exception e) {}
        return false;
    }

    public static synchronized void recordVoter(String gr, String name) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(VOTERS_FILE, true)))) {
            out.println(gr + "," + name);
        } catch (IOException e) {}
    }

    public static synchronized void submitVotes(Map<String, String> choices) {
        for (Map.Entry<String, String> entry : choices.entrySet()) {
            voteTally.putIfAbsent(entry.getKey(), new HashMap<>());
            Map<String, Integer> posVotes = voteTally.get(entry.getKey());
            posVotes.put(entry.getValue(), posVotes.getOrDefault(entry.getValue(), 0) + 1);
        }
        exportResults();
    }

    private static void exportResults() {
        try (PrintWriter w = new PrintWriter(new File(RESULTS_FILE))) {
            w.println("Position,Winner,Votes,Runner Up,Votes");
            for (String pos : voteTally.keySet()) {
                List<Map.Entry<String, Integer>> list = new ArrayList<>(voteTally.get(pos).entrySet());
                list.sort((a,b) -> b.getValue().compareTo(a.getValue()));
                String p1 = list.get(0).getKey(); int v1 = list.get(0).getValue();
                String p2 = (list.size() > 1) ? list.get(1).getKey() : "N/A";
                int v2 = (list.size() > 1) ? list.get(1).getValue() : 0;
                w.printf("%s,%s,%d,%s,%d\n", pos, p1, v1, p2, v2);
            }
        } catch (Exception e) {}
    }
}
