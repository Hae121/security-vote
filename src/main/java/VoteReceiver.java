import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.security.PrivateKey;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.UUID;

@WebServlet("/vote")
public class VoteReceiver extends HttpServlet {

    @Override
    public void init() throws ServletException {
        try {
            KeyManager.initializeKeys(getServletContext()); // 키 초기화
        } catch (Exception e) {
            throw new ServletException("❌ 키 초기화 실패", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    	System.out.println("📥 VoteReceiver 진입 성공!");

        try {
            // 1. 암호화된 파라미터 수신
            String encryptedVote = request.getParameter("encryptedVote");
            String encryptedKey = request.getParameter("encryptedKey");
            String ivBase64 = request.getParameter("iv");

            // 2. RSA로 AES 키 복호화
            PrivateKey privateKey = KeyManager.getPrivateKey(getServletContext());
            byte[] aesKeyBytes = RSAUtil.decryptAESKey(encryptedKey, privateKey);
            SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

            // 3. AES로 투표 내용 복호화
            byte[] iv = Base64.getDecoder().decode(ivBase64);
            String decryptedVote = AESUtil.decrypt(encryptedVote, aesKey, iv);
            System.out.println("🗳️ 복호화된 투표 내용: " + decryptedVote);

            // 4. 저장 경로 설정
            String votePath = getServletContext().getRealPath("/WEB-INF/votes");
            System.out.println("✅ 저장 경로: " + votePath);

            if (votePath == null) {
                throw new ServletException("❌ WEB-INF/votes 경로가 null입니다!");
            }

            File folder = new File(votePath);
            if (!folder.exists()) {
                System.out.println("📁 폴더가 없어 생성함");
                folder.mkdirs();
            }

            // 5. 파일 저장
            String filename = "vote_" + UUID.randomUUID() + ".txt";
            File voteFile = new File(folder, filename);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(voteFile))) {
                writer.write(decryptedVote);
            }

            System.out.println("✅ 저장 완료: " + voteFile.getAbsolutePath());

            // 6. 응답
            response.setContentType("text/plain");
            response.getWriter().write("success");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "복호화 또는 저장 실패: " + e.getMessage());
        }
    }
}
