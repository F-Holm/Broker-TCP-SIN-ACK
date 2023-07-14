import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8080;

    private Socket socket;
    private PrintWriter writer;

    public void connect() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            writer = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected to server");

            Thread inputThread = new Thread(this::handleInput);
            inputThread.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                writer.println(inputLine);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    public void disconnect() {
        try {
            if (writer != null) {
                writer.close();
            }

            if (socket != null) {
                socket.close();
                System.out.println("Disconnected from server");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleInput() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                System.out.println("Received message: " + inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TCPClient client = new TCPClient();
        client.connect();
    }
}
