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

class ClienteHilo extends Thread {
    private Socket socket;
    private ObjectInputStream entrada;
    private ObjectOutputStream salida;
    private boolean hiloActivo = true;
    private PublicKey clavePublica = null;
    public  ClienteHilo(Socket socket) {
        this.socket = socket;
        try {
            salida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());
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
    public String recibirMensaje(Mensaje mensaje) throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String mensajeSTR = RSAySHA.desEncriptarPrivadaRSA(mensaje.getMensajeEncriptado(), ServidorBroker.clavePrivada);
        if (RSAySHA.desEncriptarPublicaRSA(mensaje.getMensajeHasheadoEncriptado(), clavePublica).equals(RSAySHA.hashearMensaje(mensajeSTR)))
            return mensajeSTR;
        return "";
    }
    @Override
    public void run() {
        try {
            Mensaje mensaje;
            boolean primero = true;
            while (hiloActivo) {
                mensaje = (Mensaje) entrada.readObject();
                if (recibirMensaje(mensaje).equals("")) break;
                if (primero){
                    primero = false;
                    clavePublica = RSAySHA.base64ClavePublica(mensaje.getMensajeEncriptado());
                    continue;
                }
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
        } catch (ClassNotFoundException | NoSuchPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
    public void enviarMensaje(String topico, String mensaje) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (!(mensaje.equals("CERRAR_SERVIDOR") && topico.equals("Servidor"))) {
            mensaje = topico + ": " + mensaje;
        }
        String mensajeHasheadoEncriptado = RSAySHA.desEncriptarPrivadaRSA(RSAySHA.hashearMensaje(mensaje), ServidorBroker.clavePrivada);
        String mensajeEncriptado = RSAySHA.encriptarPublicaRSA(mensaje, clavePublica);
        salida.writeObject(new Mensaje(mensajeHasheadoEncriptado, mensajeEncriptado));
    }
}