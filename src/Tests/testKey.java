package Tests;

import EncriptacionAsimetricaSimetrica.RSA_SHA_AES;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.*;

public class testKey {
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
        KeyPair keypair = keygen.generateKeyPair();

        String mensaje = "ZLATAN";
        System.out.println(mensaje);
        String mensajeEncriptado = RSA_SHA_AES.encriptarPrivadaRSA(mensaje, keypair.getPrivate());
        String clavePublicaSTR = RSA_SHA_AES.clavePublicaBase64(keypair.getPublic());
        PublicKey clavePublica = RSA_SHA_AES.base64ClavePublica(clavePublicaSTR);
        System.out.println(RSA_SHA_AES.desencriptarPublicaRSA(mensajeEncriptado, clavePublica));
        System.out.println(keypair.getPublic());
    }
}
