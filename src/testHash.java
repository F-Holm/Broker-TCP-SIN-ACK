import java.security.NoSuchAlgorithmException;

public class testHash {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        String mensaje1 = "hola";
        String mensaje2 = "hola";
        String mensaje3 = "holo";
        System.out.println(RSAySHA.hashearMensaje(mensaje1).equals(RSAySHA.hashearMensaje(mensaje2)));
        System.out.println(RSAySHA.hashearMensaje(mensaje1).equals(RSAySHA.hashearMensaje(mensaje3)));
        System.out.println(RSAySHA.hashearMensaje(mensaje1));
    }
}
