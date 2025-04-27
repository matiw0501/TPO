/**
 *
 *  @author
 *
 */

package zad1;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private String host;
    private int port;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private volatile boolean running;
    private StringBuilder serverLog = new StringBuilder();
    private Map<String, StringBuilder> clientLogs = new ConcurrentHashMap<>();

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void startServer() {
        running = true;
        executorService = Executors.newCachedThreadPool();
        try {
            serverSocket = new ServerSocket(port, 50, InetAddress.getByName(host));
            executorService.execute(this::acceptClients);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acceptClients() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                executorService.execute(new ClientHandler(clientSocket));
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stopServer() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        executorService.shutdownNow();
    }

    public String getServerLog() {
        return serverLog.toString();
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String clientId = "(unknown client)";

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    serverLog.append(clientId).append(": ").append(line).append("\n");
                    if (!clientLogs.containsKey(clientId)) {
                        clientLogs.put(clientId, new StringBuilder());
                    }
                    clientLogs.get(clientId).append("> ").append(line).append("\n");

                    String response = handleRequest(line);
                    out.println(response);
                    out.flush();
                }
            } catch (IOException e) {
                // Klient się rozłączył - nic nie trzeba robić
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private String handleRequest(String request) {
            if (request.startsWith("login")) {
                String[] parts = request.split("\\s+", 2);
                if (parts.length == 2) {
                    clientId = parts[1];
                    clientLogs.putIfAbsent(clientId, new StringBuilder());
                    return "logged in";
                } else {
                    return "*** Invalid login command";
                }
            } else if (request.startsWith("passed")) {
                String[] parts = request.split("\\s+");
                if (parts.length < 3) {
                    return "*** Invalid passed command. Expected: passed <from> <to>";
                }
                String from = parts[1];
                String to = parts[2];
                return Time.passed(from, to);
            } else if (request.equals("bye and log transfer")) {
                String log = clientLogs.getOrDefault(clientId, new StringBuilder()).toString();
                clientLogs.remove(clientId);
                return log;
            } else {
                return "*** Unknown command";
            }
        }
    }
}
