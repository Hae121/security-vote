import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class KeyGen {
    public static void main(String[] args) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();

        try (FileOutputStream fos = new FileOutputStream("private.key")) {
            fos.write(pair.getPrivate().getEncoded());
        }

        try (FileOutputStream fos = new FileOutputStream("public.key")) {
            fos.write(pair.getPublic().getEncoded());
        }

        System.out.println(" 생성 완료");
    }
}