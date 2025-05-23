<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>온라인 투표 - 로그인</title>
<style>
body {
	background: #f0f4f8;
	display: flex;
	align-items: center;
	justify-content: center;
	height: 100vh;
	font-family: 'Segoe UI', sans-serif;
}

.login-box {
	background: white;
	padding: 30px 25px;
	border-radius: 15px;
	box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
	width: 300px;
}

.login-box h2 {
	text-align: center;
	margin-bottom: 20px;
}

.login-box input {
	width: 100%;
	padding: 10px;
	margin: 10px 0;
	border: 1px solid #ccc;
	border-radius: 8px;
}

.login-box button {
	width: 100%;
	padding: 12px;
	background: #4f46e5;
	color: white;
	font-weight: bold;
	border: none;
	border-radius: 8px;
	cursor: pointer;
}

.login-box button:hover {
	background: #4338ca;
}

.error-msg {
	color: red;
	text-align: center;
	font-size: 0.9em;
}
</style>
</head>
<body>
	<div class="login-box">
		<h2>로그인</h2>

		<%
		if (request.getParameter("error") != null) {
		%>
		<div class="error-msg">아이디 또는 비밀번호가 틀렸습니다.</div>
		<%
		}
		%>

		<form action="login" method="post">
			<input type="text" name="id" placeholder="아이디" required /> <input
				type="password" name="pw" placeholder="비밀번호" required />
			<button type="submit">로그인</button>
		</form>
	</div>
</body>
</html>
