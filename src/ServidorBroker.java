import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
public class ServidorBroker {
    private static final int PUERTO = 12345;
    private static HashSet<ClienteHilo> clientes = new HashSet<>();
    public static HashMap<String, HashSet<ClienteHilo>> topicos = new HashMap<>();
    private static boolean servidorActivo = true;
    public static PrivateKey clavePrivada = null;
    public static PublicKey clavePublica = null;

    public static void main(String[] args) {
        try {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            KeyPair keypair = keygen.generateKeyPair();
            clavePublica = keypair.getPublic();
            clavePrivada = keypair.getPrivate();

            ServerSocket servidorSocket = new ServerSocket(PUERTO);
            System.out.println("Servidor Broker TCP SIN ACK iniciado en el puerto " + PUERTO);
            Thread consoleThread = new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while (servidorActivo) {
                    String comando = scanner.nextLine();
                    if (comando.equalsIgnoreCase("SALIR")) {
                        servidorActivo = false;
                        try {
                            cerrarServidor();
                        } catch (NoSuchPaddingException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalBlockSizeException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (NoSuchAlgorithmException e) {
                            throw new RuntimeException(e);
                        } catch (BadPaddingException e) {
                            throw new RuntimeException(e);
                        } catch (InvalidKeyException e) {
                            throw new RuntimeException(e);
                        }
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
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    static synchronized void cerrarServidor() throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        for (ClienteHilo cliente : clientes) {
            cliente.enviarMensaje("Servidor", "El servidor se ha cerrado.");
            cliente.enviarMensaje("Servidor", "CERRAR_SERVIDOR");
            cliente.finalizarHilo();
        }
        clientes.clear();
        topicos.clear();
    }

    static synchronized void enviarMensaje(String topico, String mensaje, ClienteHilo remitente) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
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