import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TCPServer {
    private static final int PORT = 8080;

    private ServerSocket serverSocket;
    private Map<String, Set<PrintWriter>> topicSubscriptions;

    public TCPServer() {
        topicSubscriptions = new HashMap<>();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    public void stop() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
                System.out.println("Server stopped");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void subscribeClient(String topic, PrintWriter writer) {
        Set<PrintWriter> subscribers = topicSubscriptions.getOrDefault(topic, new HashSet<>());
        subscribers.add(writer);
        topicSubscriptions.put(topic, subscribers);
        System.out.println("Client subscribed to topic: " + topic);
    }

    private synchronized void unsubscribeClient(String topic, PrintWriter writer) {
        if (topicSubscriptions.containsKey(topic)) {
            Set<PrintWriter> subscribers = topicSubscriptions.get(topic);
            subscribers.remove(writer);
            System.out.println("Client unsubscribed from topic: " + topic);

            if (subscribers.isEmpty()) {
                topicSubscriptions.remove(topic);
            }
        }
    }

    private synchronized void distributeMessage(String topic, String message) {
        Set<PrintWriter> subscribers = topicSubscriptions.getOrDefault(topic, new HashSet<>());

        for (PrintWriter writer : subscribers) {
            writer.println(message);
            writer.flush();
        }
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter writer;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new PrintWriter(clientSocket.getOutputStream(), true);

                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    System.out.println("Received message: " + inputLine);
                    String[] parts = inputLine.split(":", 2);
                    String topic = parts[0];
                    String message = parts.length > 1 ? parts[1] : "";

                    if (topic.equals("SUB")) {
                        subscribeClient(message, writer);
                    } else if (topic.equals("DESUB")) {
                        unsubscribeClient(message, writer);
                    } else if (topic.equals("DEL")) {
                        removeClient();
                        break;
                    } else {
                        distributeMessage(topic, message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                removeClient();
            }
        }

        private synchronized void removeClient() {
            for (Set<PrintWriter> subscribers : topicSubscriptions.values()) {
                subscribers.remove(writer);
            }

            writer.close();

            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Client disconnected: " + clientSocket);
        }
    }

    public static void main(String[] args) {
        TCPServer server = new TCPServer();
        server.start();
    }
}