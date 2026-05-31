import java.io.*;
import java.net.*;
import java.util.*;

public class NetworkClient {
    private static String serverUrl;

    public static void setServer(String ip) {
        serverUrl = "http://" + ip + ":" + NetworkServer.PORT;
    }

    public static void testConnection() throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL(serverUrl + "/nominees").openConnection();
        c.setConnectTimeout(2000);
        if (c.getResponseCode() != 200) throw new Exception();
    }

    public static Map<String, List<String>> fetchNominees() throws Exception {
        Map<String, List<String>> map = new LinkedHashMap<>();
        Scanner s = new Scanner(new URL(serverUrl + "/nominees").openStream());
        while (s.hasNextLine()) {
            String[] p = s.nextLine().split("\\|", 2);
            map.putIfAbsent(p[0], new ArrayList<>());
            map.get(p[0]).add(p[1]);
        }
        return map;
    }

    public static boolean checkVoted(String gr, String name) throws Exception {
        return post("/check", "gr=" + gr + "&name=" + name).equals("voted");
    }

    public static void submitVote(String gr, String name, Map<String, String> choices) throws Exception {
        StringBuilder v = new StringBuilder();
        choices.forEach((k, val) -> v.append(k).append(":").append(val).append(","));
        post("/vote", "gr=" + gr + "&name=" + name + "&votes=" + v.toString());
    }

    private static String post(String path, String body) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL(serverUrl + path).openConnection();
        c.setRequestMethod("POST");
        c.setDoOutput(true);
        c.setRequestProperty("X-Auth", NetworkServer.AUTH_KEY);
        try (OutputStream o = c.getOutputStream()) { o.write(body.getBytes("UTF-8")); }
        return new String(c.getInputStream().readAllBytes(), "UTF-8");
    }
}
