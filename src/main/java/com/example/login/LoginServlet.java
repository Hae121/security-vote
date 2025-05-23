package com.example.login;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L; //직렬화 에러메시지 제거용

	private final Map<String, UserDTO> users = new HashMap<>();

	@Override
	public void init() {
		users.put("hae", new UserDTO("hae", hash("1234")));
		users.put("admin", new UserDTO("admin", hash("admin123")));
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String id = req.getParameter("id");
		String pw = req.getParameter("pw");

		UserDTO user = users.get(id);

		if (user != null && user.matchesPassword(hash(pw))) {
			HttpSession session = req.getSession();
			session.setAttribute("user", user.getId());
			resp.sendRedirect("home.jsp");
		} else {
			resp.sendRedirect("login.jsp?error=1");
		}
	}

	private String hash(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] result = md.digest(input.getBytes());
			StringBuilder sb = new StringBuilder();
			for (byte b : result)
				sb.append(String.format("%02x", b));
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
