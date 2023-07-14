import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteTCP {
    private static final String SERVIDOR_IP = "172.16.255.223";
    private static final int SERVIDOR_PUERTO = 8080;

    private Socket socket;
    private PrintWriter escritor;

    public void conectar() {
        try {
            socket = new Socket(SERVIDOR_IP, SERVIDOR_PUERTO);
            escritor = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Conectado al servidor");

            Thread hiloEntrada = new Thread(this::manejarEntrada);
            hiloEntrada.start();

            BufferedReader lector = new BufferedReader(new InputStreamReader(System.in));
            String lineaEntrada;
            while ((lineaEntrada = lector.readLine()) != null) {
                escritor.println(lineaEntrada);
                escritor.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            desconectar();
        }
    }

    public void desconectar() {
        try {
            if (escritor != null) {
                escritor.close();
            }

            if (socket != null) {
                socket.close();
                System.out.println("Desconectado del servidor");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void manejarEntrada() {
        try {
            BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String lineaEntrada;
            while ((lineaEntrada = lector.readLine()) != null) {
                System.out.println("Mensaje recibido: " + lineaEntrada);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ClienteTCP cliente = new ClienteTCP();
        cliente.conectar();
    }
}
