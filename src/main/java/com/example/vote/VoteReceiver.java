package com.example.vote;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/vote")
public class VoteReceiver extends HttpServlet {

	// 투표한 사용자 목록 (메모리에 저장, 실제로는 DB 사용)
	private static final Set<String> votedUsers = new HashSet<>();

	@Override
	public void init() throws ServletException {
		try {
			KeyManager.initializeKeys(getServletContext()); // 키 초기화
			System.out.println("✅ VoteReceiver 초기화 완료");
		} catch (Exception e) {
			throw new ServletException("❌ 키 초기화 실패", e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("📥 VoteReceiver 진입 성공!");

		try {
			// 1. 세션 검증
			HttpSession session = request.getSession(false);
			if (session == null || session.getAttribute("user") == null) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그인 필요");
				return;
			}
			String userId = (String) session.getAttribute("user");

			// 2. 중복 투표 체크
			synchronized (votedUsers) {
				if (votedUsers.contains(userId)) {
					response.setContentType("text/plain; charset=UTF-8");
					response.getWriter().write("already_voted");
					System.out.println("❌ 중복 투표 시도: " + userId);
					return;
				}
			}

			// 3. 암호화된 파라미터 수신
			String encryptedVote = request.getParameter("encryptedVote");
			String encryptedKey = request.getParameter("encryptedKey");
			String ivBase64 = request.getParameter("iv");

			if (encryptedVote == null || encryptedKey == null || ivBase64 == null) {
				throw new ServletException("❌ 필수 파라미터가 누락되었습니다");
			}

			// 4. RSA로 AES 키 복호화 (KeyServlet에서 개인키 가져오기)
			PrivateKey privateKey = KeyServlet.getPrivateKey(userId);
			if (privateKey == null) {
				throw new ServletException("❌ 개인키를 찾을 수 없습니다: " + userId);
			}

			byte[] aesKeyBytes = RSAUtil.decryptAESKey(encryptedKey, privateKey);
			SecretKey aesKey = new SecretKeySpec(aesKeyBytes, "AES");

			// 5. AES로 투표 내용 복호화
			byte[] iv = Base64.getDecoder().decode(ivBase64);
			String decryptedVote = AESUtil.decrypt(encryptedVote, aesKey, iv);
			System.out.println("🗳️ 복호화된 투표 내용: " + decryptedVote);

			// 6. 투표 내용 검증
			if (!"찬성".equals(decryptedVote) && !"반대".equals(decryptedVote)) {
				throw new ServletException("❌ 유효하지 않은 투표 내용: " + decryptedVote);
			}

			// 7. 저장 경로 설정
			String votePath = getServletContext().getRealPath("/WEB-INF/votes");
			if (votePath == null) {
				throw new ServletException("❌ WEB-INF/votes 경로가 null입니다!");
			}

			File folder = new File(votePath);
			if (!folder.exists()) {
				System.out.println("📁 votes 폴더 생성");
				folder.mkdirs();
			}

			// 8. 파일 저장
			String filename = "vote_" + userId + "_" + System.currentTimeMillis() + "_"
					+ UUID.randomUUID().toString().substring(0, 8) + ".txt";
			File voteFile = new File(folder, filename);

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(voteFile, StandardCharsets.UTF_8))) {
				writer.write(decryptedVote);
			}

			// 9. 투표한 사용자 목록에 추가
			synchronized (votedUsers) {
				votedUsers.add(userId);
			}

			System.out.println("✅ 저장 완료: " + voteFile.getAbsolutePath());
			System.out.println("✅ 투표자 등록: " + userId);

			// 10. 성공 응답
			response.setContentType("text/plain; charset=UTF-8");
			response.getWriter().write("success");

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("❌ VoteReceiver 오류: " + e.getMessage());
			response.sendError(500, "투표 처리 실패: " + e.getMessage());
		}
	}

	// 사용자가 이미 투표했는지 확인하는 메서드
	public static boolean hasUserVoted(String userId) {
		synchronized (votedUsers) {
			return votedUsers.contains(userId);
		}
	}

	// 투표한 사용자 목록 초기화 (관리자용)
	public static void resetVotedUsers() {
		synchronized (votedUsers) {
			votedUsers.clear();
			System.out.println("🔄 투표한 사용자 목록 초기화");
		}
	}
}