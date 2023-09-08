import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAySHA {
    public final static String delimitador = "EsteEsUnCaracterDelimitador";
    public final static String delimitadorCodificado = Base64.getEncoder().encodeToString(delimitador.getBytes());
    public static String encriptarPrivadaRSA(String mensaje, PrivateKey clavePrivada) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.ENCRYPT_MODE, clavePrivada);
        return Base64.getEncoder().encodeToString(rsaCipher.doFinal(mensaje.getBytes(StandardCharsets.UTF_8)));    }
    public static String encriptarPublicaRSA(String mensaje, PublicKey clavePublica) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.ENCRYPT_MODE, clavePublica);
        return Base64.getEncoder().encodeToString(rsaCipher.doFinal(mensaje.getBytes(StandardCharsets.UTF_8)));
    }
    public static String desEncriptarPublicaRSA(String mensaje, PublicKey clavePublica) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.DECRYPT_MODE, clavePublica);
        return new String(rsaCipher.doFinal(Base64.getDecoder().decode(mensaje.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
    }
    public static String desEncriptarPrivadaRSA(String mensaje, PrivateKey clavePrivada) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.DECRYPT_MODE, clavePrivada);
        return new String(rsaCipher.doFinal(Base64.getDecoder().decode(mensaje.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
    }
    public static String hashearMensaje(String mensaje) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(mensaje.getBytes(StandardCharsets.UTF_8));
        return new String(hash, StandardCharsets.UTF_8);
    }
    public static String clavePublicaBase64 (PublicKey clavePublica){
        return Base64.getEncoder().encodeToString(clavePublica.getEncoded());
    }
    public static String clavePrivadaBase64 (PrivateKey clavePrivada){
        return Base64.getEncoder().encodeToString(clavePrivada.getEncoded());
    }
    public static PublicKey base64ClavePublica (String clavePublica){
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(clavePublica)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static PrivateKey base64ClavePrivada (String clavePrivada) {
        try {
            byte[] clavePrivadaBytes = Base64.getDecoder().decode(clavePrivada);
            PKCS8EncodedKeySpec clavePrivadaSpec = new PKCS8EncodedKeySpec(clavePrivadaBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA"); // Puedes ajustar el algoritmo si no es RSA

            return keyFactory.generatePrivate(clavePrivadaSpec);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
