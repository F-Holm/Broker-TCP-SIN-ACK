package Tests;

import EncriptacionAsimetricaSimetrica.RSA_SHA_AES;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class test {
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        KeyPairGenerator keygen1 = KeyPairGenerator.getInstance("RSA");
        KeyPair juan = keygen1.generateKeyPair();

        KeyPairGenerator keygen2 = KeyPairGenerator.getInstance("RSA");
        KeyPair pepe = keygen2.generateKeyPair();

        String mensaje_ = "ZLATAN";

        String mensajeHasheadoEncriptadoPrivJuan = RSA_SHA_AES.encriptarPrivadaRSA(RSA_SHA_AES.hashearMensaje(mensaje_), juan.getPrivate());
        String mensajeEncriptadoPubPepe = RSA_SHA_AES.encriptarPublicaRSA(mensaje_, pepe.getPublic());

        Mensaje mensaje = new Mensaje(mensajeHasheadoEncriptadoPrivJuan, mensajeEncriptadoPubPepe);

        String mensajeDesencriptado = RSA_SHA_AES.desencriptarPrivadaRSA(mensaje.getMensajeEncriptado(), pepe.getPrivate());

        if (RSA_SHA_AES.hashearMensaje(mensajeDesencriptado).equals(RSA_SHA_AES.desencriptarPublicaRSA(mensaje.getMensajeHasheadoEncriptado(), juan.getPublic()))){
            System.out.println(mensaje_);
            System.out.println(mensajeDesencriptado);
        } else{
            System.out.println("ERROR");
            System.out.println(mensaje_);
            System.out.println(mensajeDesencriptado);
        }
    }
}
