import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class ServidorBroker {
    private static final int PUERTO = 12345;
    private static HashSet<ClienteHilo> clientes = new HashSet<>();
    public static HashMap<String, HashSet<ClienteHilo>> topicos = new HashMap<>();
    private static boolean servidorActivo = true;

    public static void main(String[] args) {
        try {
            ServerSocket servidorSocket = new ServerSocket(PUERTO);
            System.out.println("Servidor Broker TCP SIN ACK iniciado en el puerto " + PUERTO);

            Thread consoleThread = new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while (servidorActivo) {
                    String comando = scanner.nextLine();
                    if (comando.equalsIgnoreCase("SALIR")) {
                        servidorActivo = false;
                        cerrarServidor();
                    }
                }
                scanner.close();
            });
            consoleThread.start();

            while (servidorActivo) {
                Socket clienteSocket = servidorSocket.accept();
                if (servidorActivo) {
                    ClienteHilo cliente = new ClienteHilo(clienteSocket);
                    clientes.add(cliente);
                    cliente.start();
                } else {
                    clienteSocket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static synchronized void cerrarServidor() {
        for (ClienteHilo cliente : clientes) {
            cliente.enviarMensaje("Servidor", "El servidor se ha cerrado.");
            cliente.finalizarHilo();
        }
        clientes.clear();
        topicos.clear();
    }


    static synchronized void enviarMensaje(String topico, String mensaje, ClienteHilo remitente) {
        HashSet<ClienteHilo> suscriptores = topicos.get(topico);
        if (suscriptores != null) {
            for (ClienteHilo cliente : suscriptores) {
                if (!cliente.equals(remitente)) {
                    cliente.enviarMensaje(topico, mensaje);
                }
            }
        }
    }

    static synchronized void agregarSuscriptor(String topico, ClienteHilo cliente) {
        topicos.computeIfAbsent(topico, k -> new HashSet<>()).add(cliente);
    }

    static synchronized void quitarSuscriptor(String topico, ClienteHilo cliente) {
        HashSet<ClienteHilo> suscriptores = topicos.get(topico);
        if (suscriptores != null) {
            suscriptores.remove(cliente);
            if (suscriptores.isEmpty()) {
                topicos.remove(topico);
            }
        }
    }

    static synchronized void quitarCliente(ClienteHilo cliente) {
        clientes.remove(cliente);
    }
}

class ClienteHilo extends Thread {
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;

    private boolean hiloActivo = true;

    public ClienteHilo(Socket socket) {
        this.socket = socket;
        try {
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void finalizarHilo() {
        hiloActivo = false;
        try {
            entrada.close();
            salida.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            String mensaje;
            while (hiloActivo && (mensaje = entrada.readLine()) != null) {
                String[] partes = mensaje.split(":");
                String contenido = partes.length > 2 ? partes[2] : "";

                switch (partes[0]) {
                    case "SUB":
                        ServidorBroker.agregarSuscriptor(partes[1], this);
                        enviarMensaje("Servidor", "Te has suscrito al tópico " + partes[1]);
                        break;
                    case "DESUB":
                        ServidorBroker.quitarSuscriptor(partes[1], this);
                        enviarMensaje("Servidor", "Te has desuscrito del tópico " + partes[1]);
                        break;
                    case "DEL":
                        for (HashSet<ClienteHilo> suscriptores : ServidorBroker.topicos.values()) {
                            suscriptores.remove(this);
                        }
                        ServidorBroker.quitarCliente(this);
                        enviarMensaje("Servidor", "Te has desconectado");
                        finalizarHilo();
                        return;
                    default:
                        ServidorBroker.enviarMensaje(partes[0], partes[1], this);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void enviarMensaje(String topico, String mensaje) {
        salida.println(topico + ": " + mensaje);
    }
}
