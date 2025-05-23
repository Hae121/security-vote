<%@ page contentType="text/html;charset=UTF-8" %>
<%
    if (session == null || session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    String username = (String) session.getAttribute("user");
%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <title>홈</title>
  <style>
    body {
      background: #f0f4f8;
      font-family: 'Segoe UI', sans-serif;
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
    }

    .card {
      background: #ffffff;
      padding: 40px 30px;
      border-radius: 16px;
      box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
      text-align: center;
    }

    .card h1 {
      font-size: 24px;
      margin-bottom: 20px;
      color: #333;
    }

    .card a {
      display: inline-block;
      padding: 12px 20px;
      background-color: #4f46e5;
      color: white;
      font-weight: bold;
      border-radius: 8px;
      text-decoration: none;
      transition: background-color 0.2s ease;
    }

    .card a:hover {
      background-color: #4338ca;
    }
  </style>
</head>
<body>
  <div class="card">
    <h1>환영합니다, <%= username %>님!</h1>
    <a href="login.jsp">로그아웃</a>
  </div>
</body>
</html>
