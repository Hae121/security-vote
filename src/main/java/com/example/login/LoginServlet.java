package com.example.login;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // 보안을 위한 해시된 비밀번호 저장
    private final Map<String, String> users = new HashMap<>();

    @Override
    public void init() {
        System.out.println("🔧 LoginServlet 초기화 시작...");
        
        // 하드코딩된 사용자 계정 (비밀번호는 SHA-256으로 해시)
        users.put("hae", hashPassword("1234"));
        users.put("admin", hashPassword("admin123"));
        
        System.out.println("🔐 비밀번호 해시 생성 완료");
        System.out.println("📋 등록된 사용자: " + users.keySet());
        System.out.println("✅ LoginServlet 초기화 완료!");
        
        // 디버깅용 해시값 출력
        System.out.println("🔍 hae 해시: " + users.get("hae"));
        System.out.println("🔍 admin 해시: " + users.get("admin"));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 한글 인코딩 설정
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        // 파라미터 가져오기
        String id = request.getParameter("id");
        String pw = request.getParameter("pw");
        
        System.out.println("🔍 로그인 시도 - ID: " + id);
        
        // 입력값 검증
        if (id == null || pw == null || id.trim().isEmpty() || pw.trim().isEmpty()) {
            System.out.println("❌ 로그인 실패: 입력값이 비어있음");
            response.sendRedirect("login.jsp?error=2");
            return;
        }
        
        // 입력된 비밀번호를 해시화
        String hashedInputPassword = hashPassword(pw);
        System.out.println("🔐 입력 비밀번호 해시: " + hashedInputPassword);
        
        // 사용자 존재 확인
        String storedHashedPassword = users.get(id.trim());
        
        if (storedHashedPassword != null && storedHashedPassword.equals(hashedInputPassword)) {
            // 로그인 성공
            HttpSession session = request.getSession();
            session.setAttribute("user", id.trim());
            session.setMaxInactiveInterval(30 * 60); // 30분 세션 유지
            
            System.out.println("✅ 로그인 성공: [" + id.trim() + "]");
            System.out.println("🎉 세션 생성 완료");
            System.out.println("🔗 home.jsp로 리다이렉트");
            
            response.sendRedirect("home.jsp");
        } else {
            // 로그인 실패
            System.out.println("❌ 로그인 실패: ID 또는 비밀번호 불일치");
            System.out.println("🔍 입력된 ID: " + id);
            System.out.println("🔍 사용자 존재: " + (storedHashedPassword != null));
            
            response.sendRedirect("login.jsp?error=1");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        System.out.println("📥 GET 요청 - login.jsp로 리다이렉트");
        response.sendRedirect("login.jsp");
    }
    
    /**
     * SHA-256 해시 함수 (보안 강화)
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes("UTF-8"));
            
            // 바이트 배열을 16진수 문자열로 변환
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
            
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            System.err.println("❌ 해시 생성 오류: " + e.getMessage());
            throw new RuntimeException("비밀번호 해시 생성 실패", e);
        }
    }
}