package BrokerYClientes;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLOutput;
import java.util.Base64;

public class RSA_SHA_AES {
    public final static String delimitador = "EsteEsUnCaracterDelimitador";
    public final static String delimitadorCodificado = Base64.getEncoder().encodeToString(delimitador.getBytes());
    public final static String delimitadorAES = "DelimitadorCaracterUnEsEste";
    public final static String delimitadorCodificadoAES = Base64.getEncoder().encodeToString(delimitadorAES.getBytes());
    public static String encriptarPrivadaRSA(String mensaje, PrivateKey clavePrivada) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.ENCRYPT_MODE, clavePrivada);
        return Base64.getEncoder().encodeToString(rsaCipher.doFinal(mensaje.getBytes(StandardCharsets.UTF_8)));    }
    public static String encriptarPublicaRSA(String mensaje, PublicKey clavePublica) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.ENCRYPT_MODE, clavePublica);
        return Base64.getEncoder().encodeToString(rsaCipher.doFinal(mensaje.getBytes(StandardCharsets.UTF_8)));
    }
    public static String desencriptarPublicaRSA(String mensaje, PublicKey clavePublica) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.DECRYPT_MODE, clavePublica);
        return new String(rsaCipher.doFinal(Base64.getDecoder().decode(mensaje.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
    }
    public static String desencriptarPrivadaRSA(String mensaje, PrivateKey clavePrivada) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
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
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(clavePrivada)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encriptarAES(String mensaje, SecretKey clave) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, clave);
        return Base64.getEncoder().encodeToString(cipher.doFinal(mensaje.getBytes())) + delimitadorCodificadoAES + Base64.getEncoder().encodeToString(cipher.getIV());
    }

    public static String desencriptarAES(String mensajeEncriptado, SecretKey clave) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        String[] partes = mensajeEncriptado.split(delimitadorCodificadoAES);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, clave, new GCMParameterSpec(128, Base64.getDecoder().decode(partes[1])));
        return new String(cipher.doFinal(Base64.getDecoder().decode(partes[0])));
    }

    public static SecretKey generarClaveAES() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    public static String secretKeyBase64(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public static SecretKey base64SecretKey(String base64Key) {
        return new SecretKeySpec(Base64.getDecoder().decode(base64Key), 0, Base64.getDecoder().decode(base64Key).length, "AES");
    }


}
