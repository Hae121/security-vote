<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%
    // 이미 로그인된 사용자는 홈으로 리다이렉트
    if (session != null && session.getAttribute("user") != null) {
        response.sendRedirect("home.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>온라인 투표 - 로그인</title>
<style>
body {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100vh;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    margin: 0;
}

.login-container {
    background: rgba(255, 255, 255, 0.95);
    padding: 40px 35px;
    border-radius: 20px;
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
    width: 100%;
    max-width: 400px;
    backdrop-filter: blur(10px);
}

.login-container h2 {
    text-align: center;
    margin-bottom: 30px;
    color: #333;
    font-size: 28px;
    font-weight: 600;
}

.form-group {
    margin-bottom: 20px;
}

.form-group input {
    width: 100%;
    padding: 15px;
    border: 2px solid #e2e8f0;
    border-radius: 10px;
    font-size: 16px;
    transition: border-color 0.3s ease;
    box-sizing: border-box;
}

.form-group input:focus {
    outline: none;
    border-color: #4f46e5;
    box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
}

.login-btn {
    width: 100%;
    padding: 15px;
    background: linear-gradient(135deg, #4f46e5, #7c3aed);
    color: white;
    font-weight: 600;
    font-size: 16px;
    border: none;
    border-radius: 10px;
    cursor: pointer;
    transition: all 0.3s ease;
    margin-top: 10px;
}

.login-btn:hover {
    background: linear-gradient(135deg, #4338ca, #6d28d9);
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(79, 70, 229, 0.4);
}

.error-msg {
    background: #fee2e2;
    color: #dc2626;
    padding: 12px;
    border-radius: 8px;
    text-align: center;
    font-size: 14px;
    margin-bottom: 20px;
    border: 1px solid #fecaca;
}

.test-accounts {
    background: #f0f9ff;
    padding: 15px;
    border-radius: 8px;
    margin-top: 20px;
    border-left: 4px solid #0ea5e9;
    font-size: 14px;
}

.test-accounts h4 {
    margin: 0 0 10px 0;
    color: #0369a1;
}

.test-accounts p {
    margin: 5px 0;
    color: #075985;
}
</style>
</head>
<body>
    <div class="login-container">
        <h2>🗳️ 온라인 투표</h2>

        <%
        String error = request.getParameter("error");
        if ("1".equals(error)) {
        %>
        <div class="error-msg">❌ 아이디 또는 비밀번호가 틀렸습니다.</div>
        <%
        } else if ("2".equals(error)) {
        %>
        <div class="error-msg">❌ 아이디와 비밀번호를 입력해주세요.</div>
        <%
        }
        %>

        <form method="post" action="login">
            <div class="form-group">
                <input type="text" name="id" placeholder="아이디를 입력하세요" required />
            </div>
            <div class="form-group">
                <input type="password" name="pw" placeholder="비밀번호를 입력하세요" required />
            </div>
            <button type="submit" class="login-btn">로그인</button>
        </form>

        <div class="test-accounts">
            <h4>🧪 테스트 계정</h4>
            <p><strong>사용자:</strong> hae / 1234</p>
            <p><strong>관리자:</strong> admin / admin123</p>
        </div>
    </div>
</body>
</html>