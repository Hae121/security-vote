<%@ page contentType="text/html;charset=UTF-8" %>
<%
  if (session.getAttribute("user") == null) {
    response.sendRedirect("login.jsp");
    return;
  }
%>

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>투표 완료</title>
  <style>
    body {
      background: #f0f4f8;
      font-family: 'Segoe UI', sans-serif;
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
    }
    .complete-box {
      background: white;
      padding: 30px;
      border-radius: 15px;
      box-shadow: 0 8px 20px rgba(0,0,0,0.1);
      text-align: center;
      width: 320px;
    }
    h2 {
      margin-bottom: 20px;
      color: #16a34a;
    }
    p {
      margin-bottom: 30px;
      color: #64748b;
      line-height: 1.5;
    }
    .button-group {
      display: flex;
      flex-direction: column;
      gap: 10px;
    }
    button, .btn-link {
      padding: 12px 20px;
      border: none;
      border-radius: 8px;
      font-weight: bold;
      cursor: pointer;
      text-decoration: none;
      text-align: center;
      font-size: 14px;
      transition: all 0.3s ease;
    }
    .btn-primary {
      background: #4f46e5;
      color: white;
    }
    .btn-primary:hover {
      background: #4338ca;
      transform: translateY(-1px);
    }
    .btn-secondary {
      background: #10b981;
      color: white;
    }
    .btn-secondary:hover {
      background: #059669;
      transform: translateY(-1px);
    }
    .btn-tertiary {
      background: #f8fafc;
      color: #64748b;
      border: 1px solid #e2e8f0;
    }
    .btn-tertiary:hover {
      background: #f1f5f9;
    }
  </style>
</head>
<body>
  <div class="complete-box">
    <h2>✅ 투표 완료</h2>
    <p>소중한 한 표가 성공적으로 제출되었습니다.<br>
       투표해 주셔서 감사합니다!</p>
    
    <div class="button-group">
      <button class="btn-primary" onclick="goToResult()">📊 결과 확인하기</button>
      <a href="home.jsp" class="btn-secondary">🏠 홈으로 돌아가기</a>
      <a href="logout" class="btn-tertiary">🚪 로그아웃</a>
    </div>
  </div>

  <script>
    function goToResult() {
      window.location.href = "result.jsp";
    }
  </script>
</body>
</html>