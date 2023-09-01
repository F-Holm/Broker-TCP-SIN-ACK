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
        String mensajeEncriptado = RSAySHA.encriptarPrivadaRSA(mensaje, keypair.getPrivate());
        String clavePublicaSTR = RSAySHA.clavePublicaBase64(keypair.getPublic());
        PublicKey clavePublica = RSAySHA.base64ClavePublica(clavePublicaSTR);
        System.out.println(RSAySHA.desEncriptarPublicaRSA(mensajeEncriptado, clavePublica));
    }
}
