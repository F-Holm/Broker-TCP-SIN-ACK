import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class testSHA {
    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String texto = "ZLATAN";
        System.out.println(texto);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
        String hash_ = new String(hash, StandardCharsets.UTF_8);
        System.out.println(hash_);
    }
}
