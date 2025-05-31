package com.example.vote;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.*;
import javax.crypto.Cipher;
import java.util.Base64;

public class RSAUtil {

    // RSA 키 쌍 생성
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    // 공개키로 AES 키 암호화
    public static String encryptAESKey(byte[] aesKey, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encrypted = cipher.doFinal(aesKey);
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // 개인키로 AES 키 복호화
    public static byte[] decryptAESKey(String encryptedKeyBase64, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decoded = Base64.getDecoder().decode(encryptedKeyBase64);
        return cipher.doFinal(decoded);
    }

    // 공개키 저장 (디렉토리 없으면 생성)
    public static void savePublicKey(PublicKey publicKey, String path) throws Exception {
        File file = new File(path);
        file.getParentFile().mkdirs(); //디렉토리 자동 생성!
        byte[] encoded = publicKey.getEncoded();
        Files.write(file.toPath(), encoded);
    }

    // 개인키 저장 (디렉토리 없으면 생성)
    public static void savePrivateKey(PrivateKey privateKey, String path) throws Exception {
        File file = new File(path);
        file.getParentFile().mkdirs(); //디렉토리 자동 생성!
        byte[] encoded = privateKey.getEncoded();
        Files.write(file.toPath(), encoded);
    }

    // 공개키 불러오기
    public static PublicKey loadPublicKey(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    }

    // 개인키 불러오기
    public static PrivateKey loadPrivateKey(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePrivate(spec);
    }
}
