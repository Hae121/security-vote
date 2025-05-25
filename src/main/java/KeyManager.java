import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.servlet.ServletContext;

public class KeyManager {

    private static final String PRIVATE_KEY_DIR = "/WEB-INF/keys";  // 서버 내부 보안 키 저장 위치
    private static final String PUBLIC_KEY_DIR = "/keys";           // 브라우저에서 접근 가능한 공개키 경로

    // 키가 없으면 새로 생성하고 저장
    public static void initializeKeys(ServletContext context) throws Exception {
        String privateDirPath = context.getRealPath(PRIVATE_KEY_DIR);
        String publicDirPath = context.getRealPath(PUBLIC_KEY_DIR);

        File privateDir = new File(privateDirPath);
        File publicDir = new File(publicDirPath);
        privateDir.mkdirs(); // 폴더 없으면 생성
        publicDir.mkdirs();  // 💡 공개키 폴더도 생성

        File publicKeyPrivate = new File(privateDir, "public.key");
        File privateKey = new File(privateDir, "private.key");
        File publicKeyPublic = new File(publicDir, "public.key"); // 클라이언트용 공개키

        if (!publicKeyPrivate.exists() || !privateKey.exists()) {
            System.out.println("키가 없어 새로 생성합니다.");
            KeyPair keyPair = RSAUtil.generateKeyPair();

            // 서버 내부용 키 저장
            RSAUtil.savePublicKey(keyPair.getPublic(), publicKeyPrivate.getAbsolutePath());
            RSAUtil.savePrivateKey(keyPair.getPrivate(), privateKey.getAbsolutePath());

            // 클라이언트가 가져갈 공개키도 함께 저장
            RSAUtil.savePublicKey(keyPair.getPublic(), publicKeyPublic.getAbsolutePath());

            System.out.println("공개키 복사됨: " + publicKeyPublic.getAbsolutePath());
        } else {
            System.out.println("기존 키 파일이 존재합니다.");
        }
    }

    public static PublicKey getPublicKey(ServletContext context) throws Exception {
        String path = context.getRealPath(PRIVATE_KEY_DIR + "/public.key");
        return RSAUtil.loadPublicKey(path);
    }

    public static PrivateKey getPrivateKey(ServletContext context) throws Exception {
        String path = context.getRealPath(PRIVATE_KEY_DIR + "/private.key");
        return RSAUtil.loadPrivateKey(path);
    }
}
