import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executors;

public class NetworkServer {
    public static final int PORT = 5050;
    public static final String AUTH_KEY = "ICSE_SECURE_2026";
    private static HttpServer server;

    public static void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
        server.createContext("/nominees", NetworkServer::handleNominees);
        server.createContext("/check",    NetworkServer::handleCheck);
        server.createContext("/vote",     NetworkServer::handleVote);
        server.setExecutor(Executors.newFixedThreadPool(10)); // Efficiently handle 10+ desktops
        server.start();
    }

    private static void handleNominees(HttpExchange ex) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (var entry : ElectionData.nomineesByPosition.entrySet()) {
            for (String nominee : entry.getValue()) {
                sb.append(entry.getKey()).append("|").append(nominee).append("\n");
            }
        }
        respond(ex, 200, sb.toString());
    }

    private static void handleCheck(HttpExchange ex) throws IOException {
        Map<String, String> params = parseBody(ex);
        boolean voted = ElectionData.hasVoted(params.get("gr"), params.get("name"));
        respond(ex, 200, voted ? "voted" : "ok");
    }

    private static void handleVote(HttpExchange ex) throws IOException {
        if (!AUTH_KEY.equals(ex.getRequestHeaders().getFirst("X-Auth"))) {
            respond(ex, 403, "Denied"); return;
        }
        Map<String, String> params = parseBody(ex);
        Map<String, String> choices = new HashMap<>();
        String[] pairs = params.get("votes").split(",");
        for (String p : pairs) {
            String[] kv = p.split(":");
            if (kv.length == 2) choices.put(kv[0], kv[1]);
        }
        ElectionData.recordVoter(params.get("gr"), params.get("name"));
        ElectionData.submitVotes(choices);
        respond(ex, 200, "ok");
    }

    private static void respond(HttpExchange ex, int code, String body) throws IOException {
        byte[] b = body.getBytes("UTF-8");
        ex.sendResponseHeaders(code, b.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(b); }
    }

    private static Map<String, String> parseBody(HttpExchange ex) throws IOException {
        String body = new String(ex.getRequestBody().readAllBytes(), "UTF-8");
        Map<String, String> m = new HashMap<>();
        for (String p : body.split("&")) {
            String[] kv = p.split("=");
            if (kv.length == 2) m.put(URLDecoder.decode(kv[0], "UTF-8"), URLDecoder.decode(kv[1], "UTF-8"));
        }
        return m;
    }

    public static String getLocalIP() {
        try { return InetAddress.getLocalHost().getHostAddress(); } catch (Exception e) { return "127.0.0.1"; }
    }
}
