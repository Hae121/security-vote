package util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.*;
import javax.crypto.Cipher;
import java.util.Base64;

public class RSAUtil {

    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    public static String encryptAESKey(byte[] aesKey, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encrypted = cipher.doFinal(aesKey);
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static byte[] decryptAESKey(String encryptedKeyBase64, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decoded = Base64.getDecoder().decode(encryptedKeyBase64);
        return cipher.doFinal(decoded);
    }

    public static void savePublicKey(PublicKey publicKey, String path) throws Exception {
        File file = new File(path);
        file.getParentFile().mkdirs(); 
        byte[] encoded = publicKey.getEncoded();
        Files.write(file.toPath(), encoded);
    }


    public static void savePrivateKey(PrivateKey privateKey, String path) throws Exception {
        File file = new File(path);
        file.getParentFile().mkdirs(); 
        byte[] encoded = privateKey.getEncoded();
        Files.write(file.toPath(), encoded);
    }


    public static PublicKey loadPublicKey(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    }


    public static PrivateKey loadPrivateKey(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePrivate(spec);
    }
}