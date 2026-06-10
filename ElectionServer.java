import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
/*for the admin comp only experimental*/
public class ElectionServer {
    private static final int PORT = 8500; 
    private static final ExecutorService pool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        ElectionData.initialize(); 
        System.out.println("[SYS LIVE] Central Network Server running on TCP Port: " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                pool.execute(new ClientConnectionTask(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("[SERVER CRITICAL] System socket failure.");
        }
    }

    private static class ClientConnectionTask implements Runnable {
        private final Socket socket;
        public ClientConnectionTask(Socket socket) { this.socket = socket; }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                 PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true)) {
                
                String action = in.readLine();
                if (action == null) return;

                switch (action) {
                    case "GET_NOMINEES":
                        synchronized (ElectionData.class) {
                            for (String cat : ElectionData.nomineeMap.keySet()) {
                                for (String nominee : ElectionData.nomineeMap.get(cat)) {
                                    out.println(cat + ";" + nominee);
                                }
                            }
                        }
                        out.println("END_OF_STREAM");
                        break;

                    case "CHECK_VOTER":
                        String token = in.readLine();
                        boolean status;
                        synchronized (ElectionData.class) { status = ElectionData.checkHasVoted(token); }
                        out.println(status ? "ALREADY_VOTED" : "CLEAR_TO_PROCEED");
                        break;

                    case "COMMIT_VOTE":
                        String vID = in.readLine();
                        int limit = Integer.parseInt(in.readLine());
                        Map<String, String> basket = new LinkedHashMap<>();
                        for (int i = 0; i < limit; i++) {
                            String[] pair = in.readLine().split(";");
                            if (pair.length >= 2) basket.put(pair[0], pair[1]);
                        }
                        synchronized (ElectionData.class) { ElectionData.commitBallot(vID, basket); }
                        out.println("TRANSACTION_SUCCESSFUL");
                        break;

                    case "GENERATE_REPORT":
                        synchronized (ElectionData.class) { ElectionData.generateOfficialReport(); }
                        out.println("REPORT_DONE");
                        break;
                }
            } catch (IOException e) {/**/}
        }
    }
}
