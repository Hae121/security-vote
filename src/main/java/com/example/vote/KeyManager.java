package com.example.vote;

import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.servlet.ServletContext;

public class KeyManager {

    private static final String PRIVATE_KEY_DIR = "/WEB-INF/keys";  // 서버 내부 보안 키 저장 위치
    private static final String PUBLIC_KEY_DIR = "/keys";           // 브라우저에서 접근 가능한 공개키 경로

    // 키가 없으면 새로 생성하고 저장 (현재는 사용하지 않지만 호환성을 위해 유지)
    public static void initializeKeys(ServletContext context) throws Exception {
        System.out.println("✅ KeyManager 초기화 완료 (KeyServlet에서 메모리 관리)");
        // 실제로는 KeyServlet에서 메모리상에서 키를 관리하므로 여기서는 아무것도 하지 않음
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