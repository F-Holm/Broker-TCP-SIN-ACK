import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorBroker {
    private static final int PUERTO = 12345;
    private static HashSet<ClienteHilo> clientes = new HashSet<>();
    public static HashMap<String, HashSet<ClienteHilo>> topicos = new HashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket servidorSocket = new ServerSocket(PUERTO);
            System.out.println("Servidor Broker TCP SIN ACK iniciado en el puerto " + PUERTO);

            while (true) {
                Socket clienteSocket = servidorSocket.accept();
                ClienteHilo cliente = new ClienteHilo(clienteSocket);
                clientes.add(cliente);
                cliente.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static synchronized void enviarMensaje(String topico, String mensaje, ClienteHilo remitente) {
        HashSet<ClienteHilo> suscriptores = topicos.get(topico);
        if (suscriptores != null) {
            for (ClienteHilo cliente : suscriptores) {
                if (!cliente.equals(remitente)) {
                    cliente.enviarMensaje(topico, mensaje);
                    /**/System.out.println("Mensaje enviado");
                }
            }
        }
    }

    static synchronized void agregarSuscriptor(String topico, ClienteHilo cliente) {
        topicos.computeIfAbsent(topico, k -> new HashSet<>()).add(cliente);
        /**/for (ClienteHilo clienteHilo: topicos.get(topico)){
            System.out.println("Clientes");
        }/**/
    }

    static synchronized void quitarSuscriptor(String topico, ClienteHilo cliente) {
        /**/System.out.println("Entro en el metodo quitarSuscriptor");
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

    public ClienteHilo(Socket socket) {
        this.socket = socket;
        try {
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String mensaje;
            while ((mensaje = entrada.readLine()) != null) {
                String[] partes = mensaje.split(":");
                String accion = partes[0];
                String topico = partes[1];
                String contenido = partes.length > 2 ? partes[2] : "";

                switch (accion) {
                    case "SUB":
                        ServidorBroker.agregarSuscriptor(topico, this);
                        enviarMensaje("Servidor", "Te has suscrito al tópico " + topico);
                        break;
                    case "DESUB":
                        ServidorBroker.quitarSuscriptor(topico, this);
                        enviarMensaje("Servidor", "Te has desuscrito del tópico " + topico);
                        break;
                    case "DEL":
                        for (HashSet<ClienteHilo> suscriptores : ServidorBroker.topicos.values()) {
                            suscriptores.remove(this);
                        }
                        ServidorBroker.quitarCliente(this);
                        enviarMensaje("Servidor", "Te has desconectado");
                        entrada.close();
                        salida.close();
                        socket.close();
                        return;
                    default:
                        /**/System.out.println(accion + ": " + topico);
                        ServidorBroker.enviarMensaje(accion, topico, this);
                        /**/System.out.println(accion + ": " + topico);
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
