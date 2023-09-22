package Codigo;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.HashSet;

public class ClienteHilo extends Thread {
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private boolean hiloActivo = true;
    private PublicKey clavePublica = null;
    private SecretKey claveAES;
    public ClienteHilo(Socket socket) {
        this.socket = socket;
        try {
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);
            claveAES = RSA_SHA_AES.generarClaveAES();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
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
            enviarClavePublica();
            clavePublica = RSA_SHA_AES.base64ClavePublica(entrada.readLine());
            enviarClaveAES();
            while (hiloActivo && (mensaje = entrada.readLine()) != null) {
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
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                 InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    public void enviarClavePublica() throws IOException {
        salida.println(RSA_SHA_AES.clavePublicaBase64(ServidorBroker.clavePublica));
    }

    public void enviarClaveAES() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        String mensajeHasheadoEncriptado = RSA_SHA_AES.encriptarPrivadaRSA(RSA_SHA_AES.hashearMensaje(RSA_SHA_AES.secretKeyBase64(claveAES)), ServidorBroker.clavePrivada);
        String mensajeEncriptado = RSA_SHA_AES.encriptarPublicaRSA(RSA_SHA_AES.secretKeyBase64(claveAES), clavePublica);
        salida.println(mensajeHasheadoEncriptado + RSA_SHA_AES.delimitadorCodificado + mensajeEncriptado);
    }
    public void enviarMensaje(String topico, String mensaje) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (!(mensaje.equals("CERRAR_SERVIDOR") && topico.equals("Servidor"))) {
            mensaje = topico + ": " + mensaje;
        }
        String mensajeHasheadoEncriptado = RSA_SHA_AES.encriptarPrivadaRSA(RSA_SHA_AES.hashearMensaje(mensaje), ServidorBroker.clavePrivada);
        String mensajeEncriptado = RSA_SHA_AES.encriptarAES(mensaje, claveAES);
        salida.println(mensajeHasheadoEncriptado + RSA_SHA_AES.delimitadorCodificado + mensajeEncriptado);
    }
    public String recibirMensaje(String mensaje) throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        String[] partes = mensaje.split(RSA_SHA_AES.delimitadorCodificado);
        String mensajeSTR = RSA_SHA_AES.desencriptarAES(partes[1], claveAES);
        if (RSA_SHA_AES.desencriptarPublicaRSA(partes[0], clavePublica).equals(RSA_SHA_AES.hashearMensaje(mensajeSTR))){
            return mensajeSTR;
        }
        return "";
    }
}