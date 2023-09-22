package Tests;

import EncriptacionAsimetricaSimetrica.RSA_SHA_AES;

import javax.crypto.*;
import java.io.*;
import javax.crypto.spec.GCMParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
public class testAES {
    private SecretKey key;
    private final int KEY_SIZE = 128;
    private final int DATA_LENGTH = 128;
    private Cipher encryptionCipher;
    public void init() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(KEY_SIZE);
        key = keyGenerator.generateKey();
    }
    private String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }
    public String encrypt(String data) throws Exception {
        byte[] dataInBytes = data.getBytes();
        encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = encryptionCipher.doFinal(dataInBytes);
        return encode(encryptedBytes);
    }
    public String decrypt(String encryptedData) throws Exception {
        byte[] dataInBytes = decode(encryptedData);
        Cipher decryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(DATA_LENGTH, encryptionCipher.getIV());
        decryptionCipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] decryptedBytes = decryptionCipher.doFinal(dataInBytes);
        return new String(decryptedBytes);
    }

    public static Cipher deserializarCipher(String cipherAsString) throws IOException, ClassNotFoundException {
        byte[] cipherBytes = Base64.getDecoder().decode(cipherAsString);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cipherBytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

        return (Cipher) objectInputStream.readObject();
    }

    public static String serializeCipher(Cipher cipher) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(cipher);
        objectOutputStream.close();

        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException {
        SecretKey clave = RSA_SHA_AES.generarClaveAES();

        String data = "ZLATAN > MESSI > PELÉ > CR7 > MARADONA > RONALDO";

        String encriptado = RSA_SHA_AES.encriptarAES(data, clave);
        String desencriptado = RSA_SHA_AES.desencriptarAES(encriptado, RSA_SHA_AES.base64SecretKey(RSA_SHA_AES.secretKeyBase64(clave)));

        System.out.println(data);
        System.out.println(encriptado);
        System.out.println(desencriptado);
    }
}
