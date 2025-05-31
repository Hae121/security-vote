<%@ page contentType="text/html;charset=UTF-8" %>
<%
    // 세션 체크
    if (session == null || session.getAttribute("user") == null) {
        System.out.println("❌ 세션 없음 - 로그인 페이지로 리다이렉트");
        response.sendRedirect("login.jsp");
        return;
    }
    String username = (String) session.getAttribute("user");
    System.out.println("✅ 홈 페이지 접근 - 사용자: " + username);
%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>온라인 투표 - 홈</title>
  <style>
    body {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
      margin: 0;
    }

    .card {
      background: #ffffff;
      padding: 40px 30px;
      border-radius: 20px;
      box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
      text-align: center;
      max-width: 400px;
      width: 90%;
    }

    .card h1 {
      font-size: 28px;
      margin-bottom: 30px;
      color: #333;
      font-weight: 600;
    }

    .button-group {
      display: flex;
      flex-direction: column;
      gap: 15px;
      margin-top: 20px;
    }

    .btn {
      display: inline-block;
      padding: 15px 25px;
      font-weight: 600;
      border-radius: 10px;
      text-decoration: none;
      border: none;
      cursor: pointer;
      font-size: 16px;
      transition: all 0.3s ease;
      text-align: center;
    }

    .btn-primary {
      background: linear-gradient(135deg, #4f46e5, #7c3aed);
      color: white;
    }

    .btn-primary:hover {
      background: linear-gradient(135deg, #4338ca, #6d28d9);
      transform: translateY(-2px);
      box-shadow: 0 5px 15px rgba(79, 70, 229, 0.4);
    }

    .btn-success {
      background: linear-gradient(135deg, #10b981, #059669);
      color: white;
    }

    .btn-success:hover {
      background: linear-gradient(135deg, #059669, #047857);
      transform: translateY(-2px);
      box-shadow: 0 5px 15px rgba(16, 185, 129, 0.4);
    }

    .btn-secondary {
      background: #f8fafc;
      color: #64748b;
      border: 2px solid #e2e8f0;
    }

    .btn-secondary:hover {
      background: #f1f5f9;
      border-color: #cbd5e1;
      transform: translateY(-1px);
    }

    .user-info {
      background: #f8fafc;
      padding: 15px;
      border-radius: 10px;
      margin-bottom: 20px;
      border-left: 4px solid #4f46e5;
    }

    .nav-buttons {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 15px;
      margin-bottom: 15px;
    }
  </style>
</head>
<body>
  <div class="card">
    <div class="user-info">
      <h1>환영합니다, <%= username %>님!</h1>
    </div>
    
    <div class="button-group">
      <!-- 투표하기 버튼 -->
      <form action="vote.jsp" method="get" style="margin: 0;">
        <button type="submit" class="btn btn-primary" style="width: 100%;">
          🗳️ 투표하러 가기
        </button>
      </form>
      
      <!-- 결과 확인과 로그아웃을 같은 줄에 배치 -->
      <div class="nav-buttons">
        <a href="result.jsp" class="btn btn-success">
          📊 결과 확인
        </a>
        <a href="logout" class="btn btn-secondary">
          🚪 로그아웃
        </a>
      </div>
    </div>
  </div>
</body>
</html>