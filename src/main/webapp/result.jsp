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
  <title>투표 결과 확인</title>
  <style>
    body {
      background: #f9fafb;
      font-family: 'Segoe UI', sans-serif;
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
      margin: 0;
    }
    .result-box {
      background: white;
      padding: 30px;
      border-radius: 15px;
      box-shadow: 0 8px 20px rgba(0,0,0,0.1);
      width: 350px;
      text-align: center;
    }
    h2 {
      margin-bottom: 20px;
      color: #1f2937;
    }
    .result-item {
      margin: 15px 0;
    }
    .result-label {
      font-weight: bold;
      margin-bottom: 5px;
      color: #374151;
    }
    .bar-container {
      background-color: #e5e7eb;
      border-radius: 10px;
      overflow: hidden;
      height: 35px;
      position: relative;
      margin-bottom: 10px;
    }
    .bar {
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: flex-end;
      padding-right: 10px;
      color: white;
      font-weight: bold;
      transition: width 0.8s ease-in-out;
    }
    .agree-bar {
      background: linear-gradient(135deg, #16a34a, #15803d);
    }
    .disagree-bar {
      background: linear-gradient(135deg, #dc2626, #b91c1c);
    }
    .vote-count {
      font-size: 12px;
      color: #6b7280;
      margin-top: 5px;
    }
    .button-group {
      margin-top: 25px;
      display: flex;
      flex-direction: column;
      gap: 10px;
    }
    .btn {
      padding: 12px 20px;
      border: none;
      border-radius: 8px;
      font-weight: bold;
      cursor: pointer;
      text-decoration: none;
      text-align: center;
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
      background: #f8fafc;
      color: #64748b;
      border: 1px solid #e2e8f0;
    }
    .btn-secondary:hover {
      background: #f1f5f9;
    }
    .loading {
      color: #6b7280;
      font-style: italic;
    }
    .error {
      color: #dc2626;
      background: #fee2e2;
      padding: 10px;
      border-radius: 5px;
      margin: 10px 0;
    }
  </style>
</head>
<body>
  <div class="result-box">
    <h2>📊 투표 결과</h2>
    
    <div id="loadingMsg" class="loading">결과를 불러오는 중...</div>
    
    <div id="resultContent" style="display: none;">
      <div class="result-item">
        <div class="result-label">찬성</div>
        <div class="bar-container">
          <div id="agreeBar" class="bar agree-bar" style="width: 0%;">0%</div>
        </div>
        <div id="agreeCount" class="vote-count">0표</div>
      </div>
      
      <div class="result-item">
        <div class="result-label">반대</div>
        <div class="bar-container">
          <div id="disagreeBar" class="bar disagree-bar" style="width: 0%;">0%</div>
        </div>
        <div id="disagreeCount" class="vote-count">0표</div>
      </div>
    </div>
    
    <div id="errorMsg" class="error" style="display: none;"></div>
    
    <div class="button-group">
      <button class="btn btn-primary" onclick="refreshResults()">🔄 새로고침</button>
      <a href="home.jsp" class="btn btn-secondary">🏠 홈으로 돌아가기</a>
    </div>
  </div>

  <script>
    let resultData = null;
    
    function loadResults() {
      document.getElementById("loadingMsg").style.display = "block";
      document.getElementById("resultContent").style.display = "none";
      document.getElementById("errorMsg").style.display = "none";
      
      fetch("result")
        .then(res => {
          if (!res.ok) {
            throw new Error('서버 응답 오류: ' + res.status);
          }
          return res.json();
        })
        .then(data => {
          resultData = data;
          displayResults(data);
        })
        .catch(err => {
          console.error("결과 불러오기 실패:", err);
          document.getElementById("loadingMsg").style.display = "none";
          document.getElementById("errorMsg").textContent = "서버로부터 투표 결과를 불러오지 못했습니다: " + err.message;
          document.getElementById("errorMsg").style.display = "block";
        });
    }
    
    function displayResults(data) {
      const agree = data["찬성"] || 0;
      const disagree = data["반대"] || 0;
      const total = agree + disagree;

      const agreePct = total > 0 ? (agree / total * 100).toFixed(1) : 0;
      const disagreePct = total > 0 ? (disagree / total * 100).toFixed(1) : 0;

      document.getElementById("agreeBar").style.width = `${agreePct}%`;
      document.getElementById("agreeBar").textContent = `${agreePct}%`;
      document.getElementById("agreeCount").textContent = `${agree}표`;

      document.getElementById("disagreeBar").style.width = `${disagreePct}%`;
      document.getElementById("disagreeBar").textContent = `${disagreePct}%`;
      document.getElementById("disagreeCount").textContent = `${disagree}표`;
      
      document.getElementById("loadingMsg").style.display = "none";
      document.getElementById("resultContent").style.display = "block";
    }
    
    function refreshResults() {
      loadResults();
    }
    
    // 페이지 로드 시 결과 불러오기
    window.addEventListener('load', loadResults);
  </script>
</body>
</html>