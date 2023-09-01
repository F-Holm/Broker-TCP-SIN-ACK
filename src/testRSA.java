import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class testRSA {
    public static void main(String[] args) {
        try {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            KeyPair keypair = keygen.generateKeyPair();

            Cipher rsaCipher = Cipher.getInstance("RSA");


            String mensaje = "hola";
            System.out.println(mensaje);

            rsaCipher.init(Cipher.ENCRYPT_MODE, keypair.getPrivate());
            String mensajeCifrado = Base64.getEncoder().encodeToString(rsaCipher.doFinal(mensaje.getBytes(StandardCharsets.UTF_8)));
            System.out.println(mensajeCifrado);

            //String mensajeBase64 = Base64.getEncoder().encodeToString(mensajeCifrado);
            //System.out.println(mensajeBase64);
            //byte[] mensajeNormal = Base64.getDecoder().decode(mensajeCifrado_.getBytes(StandardCharsets.UTF_8));
            //System.out.println(mensajeNormal.length);

            rsaCipher.init(Cipher.DECRYPT_MODE, keypair.getPublic());
            String mensajeDescifrado = new String(rsaCipher.doFinal(Base64.getDecoder().decode(mensajeCifrado.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
            System.out.println(mensajeDescifrado);


            /*String originalInput = "test input";
            String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
            byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
            String decodedString = new String(decodedBytes);
            System.out.println(originalInput);
            System.out.println(encodedString);
            System.out.println(decodedBytes);
            System.out.println(decodedString);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
