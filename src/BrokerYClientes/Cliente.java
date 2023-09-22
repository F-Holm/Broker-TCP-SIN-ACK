package BrokerYClientes;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
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
    public static PublicKey clavePublica = null;
    public static SecretKey claveAES = null;
    public static void enviarMensaje(String mensaje, PrintWriter salida) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String mensajeHasheadoEncriptado = RSA_SHA_AES.encriptarPrivadaRSA(RSA_SHA_AES.hashearMensaje(mensaje), clavePrivada);
        String mensajeEncriptado = RSA_SHA_AES.encriptarAES(mensaje, claveAES);
        salida.println(mensajeHasheadoEncriptado + RSA_SHA_AES.delimitadorCodificado + mensajeEncriptado);
    }
    public static String recibirMensaje(String mensaje) throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        String[] partes = mensaje.split(RSA_SHA_AES.delimitadorCodificado);
        String mensajeSTR = RSA_SHA_AES.desencriptarAES(partes[1], claveAES);
        if (RSA_SHA_AES.desencriptarPublicaRSA(partes[0], clavePublicaServer).equals(RSA_SHA_AES.hashearMensaje(mensajeSTR)))
            return mensajeSTR;
        return "";
    }
    public static SecretKey recibirClaveAES(String mensaje) throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String[] partes = mensaje.split(RSA_SHA_AES.delimitadorCodificado);
        String mensajeSTR = RSA_SHA_AES.desencriptarPrivadaRSA(partes[1], clavePrivada);
        if (RSA_SHA_AES.desencriptarPublicaRSA(partes[0], clavePublicaServer).equals(RSA_SHA_AES.hashearMensaje(mensajeSTR)))
            return RSA_SHA_AES.base64SecretKey(mensajeSTR);
        return null;
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

            System.out.println("BrokerYClientes.Cliente conectado al servidor en " + SERVIDOR + ":" + PUERTO);

            Thread receptor = new Thread(() -> {
                try {
                    String mensaje;
                    clavePublicaServer = RSA_SHA_AES.base64ClavePublica(entrada.readLine());
                    claveAES = recibirClaveAES(entrada.readLine());
                    while (true) {
                        mensaje = entrada.readLine();
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
                         BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
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

            salida.println(RSA_SHA_AES.clavePublicaBase64(clavePublica));

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
        } catch (InterruptedException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException |
                 InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
