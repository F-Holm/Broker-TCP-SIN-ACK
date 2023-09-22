package Tests;

import Codigo.RSA_SHA_AES;

import java.security.NoSuchAlgorithmException;

public class testHash {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        String mensaje1 = "hola";
        String mensaje2 = "hola";
        String mensaje3 = "holo";
        System.out.println(RSA_SHA_AES.hashearMensaje(mensaje1).equals(RSA_SHA_AES.hashearMensaje(mensaje2)));
        System.out.println(RSA_SHA_AES.hashearMensaje(mensaje1).equals(RSA_SHA_AES.hashearMensaje(mensaje3)));
        System.out.println(RSA_SHA_AES.hashearMensaje(mensaje1));
    }
}
