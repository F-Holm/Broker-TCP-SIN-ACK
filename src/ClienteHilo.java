import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashSet;

public class ClienteHilo extends Thread {
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private boolean hiloActivo = true;
    private PublicKey clavePublica = null;
    public  ClienteHilo(Socket socket) {
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
    public String recibirMensaje(String mensaje) throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String[] partes = mensaje.split(RSAySHA.delimitadorCodificado);
        String mensajeSTR = RSAySHA.desEncriptarPrivadaRSA(partes[1], ServidorBroker.clavePrivada);
        if (RSAySHA.desEncriptarPublicaRSA(partes[0], clavePublica).equals(RSAySHA.hashearMensaje(mensajeSTR))){
            return mensajeSTR;
        }
        return "";
    }
    @Override
    public void run() {
        try {
            String mensaje;
            boolean primero = true;
            enviarClave();
            while (hiloActivo && (mensaje = entrada.readLine()) != null) {
                if (primero){
                    primero = false;
                    clavePublica = RSAySHA.base64ClavePublica(mensaje);
                    //System.out.println(clavePublica);
                    continue;
                }
                if (recibirMensaje(mensaje).equals("")) break;
                String[] partes = recibirMensaje(mensaje).split(":");
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
                        entrada.close();
                        salida.close();
                        socket.close();
                        finalizarHilo();
                        return;
                    default:
                        ServidorBroker.enviarMensaje(partes[0], partes[1], this);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public void enviarClave() throws IOException {
        salida.println(RSAySHA.clavePublicaBase64(ServidorBroker.clavePublica));
    }
    public void enviarMensaje(String topico, String mensaje) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (!(mensaje.equals("CERRAR_SERVIDOR") && topico.equals("Servidor"))) {
            mensaje = topico + ": " + mensaje;
        }
        String mensajeHasheadoEncriptado = RSAySHA.encriptarPrivadaRSA(RSAySHA.hashearMensaje(mensaje), ServidorBroker.clavePrivada);
        String mensajeEncriptado = RSAySHA.encriptarPublicaRSA(mensaje, clavePublica);
        salida.println(mensajeHasheadoEncriptado + RSAySHA.delimitadorCodificado + mensajeEncriptado);
    }
}