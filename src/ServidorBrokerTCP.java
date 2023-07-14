import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorBrokerTCP {
    private static final int PUERTO = 1234;
    private List<Cliente> clientes;

    public ServidorBrokerTCP() {
        clientes = new ArrayList<>();
    }

    public void iniciar() {
        try {
            ServerSocket serverSocket = new ServerSocket(PUERTO);
            System.out.println("Servidor iniciado. Esperando conexiones...");

            while (true) {
                Socket socket = serverSocket.accept();
                Cliente cliente = new Cliente(socket);
                clientes.add(cliente);
                cliente.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Cliente extends Thread {
        private Socket socket;
        private PrintWriter escritor;
        private BufferedReader lector;
        private List<String> topicos;

        public Cliente(Socket socket) {
            this.socket = socket;
            topicos = new ArrayList<>();

            try {
                escritor = new PrintWriter(socket.getOutputStream(), true);
                lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                String nombreCliente = lector.readLine();
                System.out.println("Cliente conectado: " + nombreCliente);

                String mensaje;
                while ((mensaje = lector.readLine()) != null) {
                    if (mensaje.startsWith("SUB:")) {
                        String topico = mensaje.substring(4);
                        suscribirse(topico);
                    }if (mensaje.startsWith("SUB:")) {
                        String topico = mensaje.substring(4);
                        suscribirse(topico);
                    }if (mensaje.startsWith("SUB:")) {
                        String topico = mensaje.substring(4);
                        suscribirse(topico);
                    } else {
                        String topico = obtenerTopico(mensaje);
                        enviarMensaje(topico, mensaje);
                    }
                }

                socket.close();
                eliminarCliente();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void suscribirse(String topico) {
            topicos.add(topico);
            System.out.println("Cliente suscrito al tópico: " + topico);
        }

        private void deSuscribirse(String topico) {
            topicos.remove(topico);
            System.out.println("Cliente no está suscrito al tópico: " + topico);
        }

        private String obtenerTopico(String mensaje) {
            // Implementa la lógica para extraer el tópico del mensaje
            // Por ejemplo, puedes usar un formato específico como "TOPICO: mensaje"
            // y separar el tópico y el mensaje usando algún carácter especial.
            return "";
        }

        private void enviarMensaje(String topico, String mensaje) {
            System.out.println("Enviando mensaje al tópico " + topico + ": " + mensaje);
            for (Cliente cliente : clientes) {
                if (cliente != this && cliente.estaSuscrito(topico)) {
                    cliente.enviar(mensaje);
                }
            }
        }

        private void enviar(String mensaje) {
            escritor.println(mensaje);
        }

        private boolean estaSuscrito(String topico) {
            return topicos.contains(topico);
        }

        private void eliminarCliente() {
            clientes.remove(this);
            System.out.println("Cliente desconectado");
        }
    }

    public static void main(String[] args) {
        ServidorBrokerTCP servidor = new ServidorBrokerTCP();
        servidor.iniciar();
    }
}