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

public class ServidorTCP {
    private static final int PUERTO = 8080;

    private ServerSocket serverSocket;
    private Map<String, Set<PrintWriter>> suscripcionesTemas;

    public ServidorTCP() {
        suscripcionesTemas = new HashMap<>();
    }

    public void iniciar() {
        try {
            serverSocket = new ServerSocket(PUERTO);
            System.out.println("Servidor iniciado en el puerto " + PUERTO);

            while (true) {
                Socket socketCliente = serverSocket.accept();
                System.out.println("Nuevo cliente conectado: " + socketCliente);

                ManejadorCliente manejadorCliente = new ManejadorCliente(socketCliente);
                Thread hilo = new Thread(manejadorCliente);
                hilo.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            detener();
        }
    }

    public void detener() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
                System.out.println("Servidor detenido");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void suscribirCliente(String tema, PrintWriter escritor) {
        Set<PrintWriter> suscriptores = suscripcionesTemas.getOrDefault(tema, new HashSet<>());
        suscriptores.add(escritor);
        suscripcionesTemas.put(tema, suscriptores);
        System.out.println("Cliente suscrito al tema: " + tema);
    }

    private synchronized void cancelarSuscripcionCliente(String tema, PrintWriter escritor) {
        if (suscripcionesTemas.containsKey(tema)) {
            Set<PrintWriter> suscriptores = suscripcionesTemas.get(tema);
            suscriptores.remove(escritor);
            System.out.println("Cliente canceló la suscripción al tema: " + tema);

            if (suscriptores.isEmpty()) {
                suscripcionesTemas.remove(tema);
            }
        }
    }

    private synchronized void distribuirMensaje(String tema, String mensaje) {
        Set<PrintWriter> suscriptores = suscripcionesTemas.getOrDefault(tema, new HashSet<>());

        for (PrintWriter escritor : suscriptores) {
            escritor.println(mensaje);
            escritor.flush();
        }
    }

    private class ManejadorCliente implements Runnable {
        private Socket socketCliente;
        private PrintWriter escritor;

        public ManejadorCliente(Socket socketCliente) {
            this.socketCliente = socketCliente;
        }

        @Override
        public void run() {
            try {
                BufferedReader lector = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
                escritor = new PrintWriter(socketCliente.getOutputStream(), true);

                String lineaEntrada;
                while ((lineaEntrada = lector.readLine()) != null) {
                    System.out.println("Mensaje recibido: " + lineaEntrada);
                    String[] partes = lineaEntrada.split(":", 2);
                    String tema = partes[0];
                    String mensaje = partes.length > 1 ? partes[1] : "";

                    if (tema.equals("SUB")) {
                        suscribirCliente(mensaje, escritor);
                    } else if (tema.equals("DESUB")) {
                        cancelarSuscripcionCliente(mensaje, escritor);
                    } else if (tema.equals("DEL")) {
                        eliminarCliente();
                        break;
                    } else {
                        distribuirMensaje(tema, mensaje);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                eliminarCliente();
            }
        }

        private synchronized void eliminarCliente() {
            for (Set<PrintWriter> suscriptores : suscripcionesTemas.values()) {
                suscriptores.remove(escritor);
            }

            escritor.close();

            try {
                socketCliente.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Cliente desconectado: " + socketCliente);
        }
    }

    public static void main(String[] args) {
        ServidorTCP servidor = new ServidorTCP();
        servidor.iniciar();
    }
}
