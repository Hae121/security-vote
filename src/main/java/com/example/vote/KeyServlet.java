package com.example.vote;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/getKey")
public class KeyServlet extends HttpServlet {
    private static final Map<String, KeyPair> keyPairs = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 필요");
            return;
        }

        String userId = (String) session.getAttribute("user");

        KeyPair keyPair = keyPairs.get(userId);
        if (keyPair == null) {
            try {
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                generator.initialize(2048);
                keyPair = generator.generateKeyPair();
                keyPairs.put(userId, keyPair);
                System.out.println("새 키 쌍 생성: " + userId);
            } catch (NoSuchAlgorithmException e) {
                throw new ServletException("키 생성 실패", e);
            }
        }

        PublicKey publicKey = keyPair.getPublic();
        String encodedKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());

        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(encodedKey);
    }

    public static PrivateKey getPrivateKey(String userId) {
        KeyPair pair = keyPairs.get(userId);
        return pair != null ? pair.getPrivate() : null;
    }
}