import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.util.Scanner;

public class Cliente {
    public static boolean servidorActivo = true;
    public static PublicKey clavePublicaServer = null;
    public static final String SERVIDOR = "127.0.0.1";
    public static final int PUERTO = 12345;
    public static PrivateKey clavePrivada = null;
    public static PublicKey clsavePublica = null;
    public static void enviarMensaje(String mensaje, PrintWriter salida) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String mensajeHasheadoEncriptado = RSAySHA.encriptarPrivadaRSA(RSAySHA.hashearMensaje(mensaje), clavePrivada);
        String mensajeEncriptado = RSAySHA.encriptarPublicaRSA(mensaje, clavePublicaServer);
        salida.println(mensajeHasheadoEncriptado + RSAySHA.delimitadorCodificado + mensajeEncriptado);
    }
    public static String recibirMensaje(String mensaje) throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String[] partes = mensaje.split(RSAySHA.delimitadorCodificado);
        String mensajeSTR = RSAySHA.desEncriptarPrivadaRSA(partes[1], clavePrivada);
        if (RSAySHA.desEncriptarPublicaRSA(partes[0], clavePublicaServer).equals(RSAySHA.hashearMensaje(mensajeSTR)))
            return mensajeSTR;
        return "";
    }
    public static void main(String[] args) throws NoSuchAlgorithmException {

        KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
        KeyPair keypair = keygen.generateKeyPair();
        clavePublica = keypair.getPublic();
        clavePrivada = keypair.getPrivate();


        try {
            Socket socket = new Socket(SERVIDOR, PUERTO);
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);

            System.out.println("Cliente conectado al servidor en " + SERVIDOR + ":" + PUERTO);

            Thread receptor = new Thread(() -> {
                try {
                    String mensaje;
                    boolean primera = true;
                    while (true) {
                        mensaje = entrada.readLine();
                        if (primera){
                            primera = false;
                            clavePublicaServer = RSAySHA.base64ClavePublica(mensaje);
                            //System.out.println(clavePublicaServer);
                            continue;
                        }
                        if (mensaje == null || mensaje.equals("")) {
                            System.out.println("Servidor ha cerrado la conexión.");
                            servidorActivo = false;
                            break;
                        }
                        if (recibirMensaje(mensaje).equals("CERRAR_SERVIDOR")) {
                            System.out.println("El servidor se ha cerrado.");
                            servidorActivo = false;
                            break;
                        }
                        String mensaje_ = recibirMensaje(mensaje);
                        if (!mensaje_.equals("")) System.out.println(mensaje_);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                         BadPaddingException | InvalidKeyException e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        entrada.close();
                        salida.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            receptor.start();

            salida.println(RSAySHA.clavePublicaBase64(clavePublica));

            while (true) {
                System.out.println("Elige una acción:");
                System.out.println("1. Suscribirse a un tópico");
                System.out.println("2. Desuscribirse de un tópico");
                System.out.println("3. Enviar mensaje a un tópico");
                System.out.println("4. Desconectarse");
                int opcion = scanner.nextInt();
                scanner.nextLine();

                if (!servidorActivo){
                    receptor.join();
                    return;
                }


                switch (opcion) {
                    case 1:
                        System.out.println("Ingresa el nombre del tópico al que te quieres suscribir:");
                        String topicoSub = scanner.nextLine();
                        enviarMensaje("SUB:" + topicoSub, salida);
                        break;
                    case 2:
                        System.out.println("Ingresa el nombre del tópico del que te quieres desuscribir:");
                        String topicoDesub = scanner.nextLine();
                        enviarMensaje("DESUB:" + topicoDesub, salida);
                        break;
                    case 3:
                        System.out.println("Ingresa el nombre del tópico al que quieres enviar un mensaje:");
                        String topicoMensaje = scanner.nextLine();
                        System.out.println("Escribe el mensaje:");
                        String contenidoMensaje = scanner.nextLine();
                        enviarMensaje(topicoMensaje + ":" + contenidoMensaje, salida);
                        break;
                    case 4:
                        enviarMensaje("DEL:", salida);
                        receptor.join();
                        return;
                    default:
                        System.out.println("Opción inválida.");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
