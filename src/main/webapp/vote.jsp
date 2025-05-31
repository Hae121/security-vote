<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.example.vote.VoteReceiver" %>
<%
  if (session.getAttribute("user") == null) {
    response.sendRedirect("login.jsp");
    return;
  }
  
  String userId = (String) session.getAttribute("user");
  
  // 이미 투표한 사용자인지 확인
  if (VoteReceiver.hasUserVoted(userId)) {
    response.sendRedirect("complete.jsp?status=already_voted");
    return;
  }
%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>전자봉투 투표</title>
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
    .vote-box {
      background: white;
      padding: 30px;
      border-radius: 15px;
      box-shadow: 0 8px 20px rgba(0,0,0,0.1);
      width: 350px;
    }
    h2 {
      text-align: center;
      margin-bottom: 20px;
      color: #1f2937;
    }
    .question {
      text-align: center;
      margin-bottom: 25px;
      color: #374151;
      font-size: 16px;
    }
    .option {
      margin: 15px 0;
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 15px;
      border: 2px solid #e5e7eb;
      border-radius: 10px;
      cursor: pointer;
      transition: all 0.3s ease;
    }
    .option:hover {
      border-color: #4f46e5;
      background: #f8fafc;
    }
    .option.selected {
      border-color: #4f46e5;
      background: #eef2ff;
    }
    .option input[type="radio"] {
      margin: 0;
      transform: scale(1.2);
    }
    .option label {
      font-weight: 500;
      cursor: pointer;
      flex: 1;
    }
    .submit-btn {
      width: 100%;
      padding: 15px;
      background: #4f46e5;
      color: white;
      border: none;
      border-radius: 10px;
      font-size: 16px;
      font-weight: 600;
      cursor: pointer;
      margin-top: 20px;
      transition: all 0.3s ease;
    }
    .submit-btn:hover {
      background: #4338ca;
      transform: translateY(-2px);
    }
    .submit-btn:disabled {
      background: #9ca3af;
      cursor: not-allowed;
      transform: none;
    }
    .user-info {
      text-align: center;
      margin-bottom: 20px;
      color: #6b7280;
      font-size: 14px;
    }
    .loading {
      display: none;
      text-align: center;
      color: #4f46e5;
      margin-top: 15px;
    }
    .error {
      color: #dc2626;
      text-align: center;
      margin-top: 15px;
      padding: 10px;
      background: #fef2f2;
      border-radius: 8px;
      border: 1px solid #fecaca;
    }
    .back-btn {
      position: absolute;
      top: 20px;
      left: 20px;
      padding: 10px 20px;
      background: #6b7280;
      color: white;
      text-decoration: none;
      border-radius: 8px;
      font-size: 14px;
      transition: background 0.3s ease;
    }
    .back-btn:hover {
      background: #4b5563;
    }
  </style>
</head>
<body>
  <a href="home.jsp" class="back-btn">← 홈으로</a>
  
  <div class="vote-box">
    <h2>🗳️ 전자봉투 투표</h2>
    <div class="user-info">투표자: <%= userId %></div>
    <div class="question">다음 안건에 대해 투표해주세요:</div>
    
    <form id="voteForm">
      <div class="option" onclick="selectOption('찬성')">
        <input type="radio" id="agree" name="vote" value="찬성">
        <label for="agree">✅ 찬성</label>
      </div>
      
      <div class="option" onclick="selectOption('반대')">
        <input type="radio" id="disagree" name="vote" value="반대">
        <label for="disagree">❌ 반대</label>
      </div>
      
      <button type="submit" class="submit-btn" id="submitBtn" disabled>
        투표하기
      </button>
    </form>
    
    <div class="loading" id="loading">
      🔐 투표를 암호화하여 전송 중...
    </div>
    
    <div class="error" id="error" style="display: none;"></div>
  </div>

  <script>
    let selectedVote = null;
    let publicKey = null;
    
    // KeyServlet에서 공개키 가져오기
    window.onload = async function() {
      try {
        const response = await fetch('getKey');
        if (response.ok) {
          const keyData = await response.text();
          publicKey = await importRSAKey(keyData);
        } else {
          throw new Error('공개키 로드 실패');
        }
      } catch (error) {
        showError('초기화 실패: ' + error.message);
      }
    };
    
    function selectOption(vote) {
      document.querySelectorAll('.option').forEach(opt => opt.classList.remove('selected'));
      event.currentTarget.classList.add('selected');
      document.querySelector(`input[value="${vote}"]`).checked = true;
      selectedVote = vote;
      document.getElementById('submitBtn').disabled = false;
    }
    
    // VoteReceiver가 기대하는 형태로 암호화해서 전송
    document.getElementById('voteForm').addEventListener('submit', async function(e) {
      e.preventDefault();
      
      if (!selectedVote || !publicKey) {
        showError('투표 선택 또는 암호화 키 오류');
        return;
      }
      
      try {
        document.getElementById('submitBtn').disabled = true;
        document.getElementById('loading').style.display = 'block';
        document.getElementById('error').style.display = 'none';
        
        // AESUtil.generateAESKey()와 동일한 방식
        const aesKey = await crypto.subtle.generateKey({name: 'AES-CBC', length: 128}, true, ['encrypt']);
        const iv = crypto.getRandomValues(new Uint8Array(16)); // AESUtil.generateIV()와 동일
        
        // AESUtil.encrypt()와 동일한 방식
        const encryptedVote = await encryptAES(selectedVote, aesKey, iv);
        
        // RSAUtil.encryptAESKey()와 동일한 방식
        const aesKeyRaw = await crypto.subtle.exportKey('raw', aesKey);
        const encryptedKey = await crypto.subtle.encrypt({name: 'RSA-OAEP'}, publicKey, aesKeyRaw);
        
        // VoteReceiver가 기대하는 파라미터로 전송
        const formData = new FormData();
        formData.append('encryptedVote', encryptedVote);
        formData.append('encryptedKey', arrayBufferToBase64(encryptedKey));
        formData.append('iv', arrayBufferToBase64(iv));
        
        const response = await fetch('vote', {
          method: 'POST',
          body: formData
        });
        
        const result = await response.text();
        
        if (response.ok) {
          if (result === 'success') {
            window.location.href = 'complete.jsp?status=success';
          } else if (result === 'already_voted') {
            window.location.href = 'complete.jsp?status=already_voted';
          } else {
            throw new Error('알 수 없는 응답');
          }
        } else {
          throw new Error('투표 처리 실패');
        }
        
      } catch (error) {
        showError('투표 전송 실패: ' + error.message);
        document.getElementById('submitBtn').disabled = false;
        document.getElementById('loading').style.display = 'none';
      }
    });
    
    function showError(message) {
      document.getElementById('error').textContent = message;
      document.getElementById('error').style.display = 'block';
    }
    
    // Java 유틸과 호환되는 암호화 함수들
    async function importRSAKey(base64Key) {
      const binaryKey = base64ToArrayBuffer(base64Key);
      return await crypto.subtle.importKey('spki', binaryKey, {name: 'RSA-OAEP', hash: 'SHA-256'}, false, ['encrypt']);
    }
    
    async function encryptAES(text, key, iv) {
      const data = new TextEncoder().encode(text);
      const encrypted = await crypto.subtle.encrypt({name: 'AES-CBC', iv: iv}, key, data);
      return arrayBufferToBase64(encrypted);
    }
    
    function base64ToArrayBuffer(base64) {
      const binary = atob(base64);
      const bytes = new Uint8Array(binary.length);
      for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
      return bytes.buffer;
    }
    
    function arrayBufferToBase64(buffer) {
      const bytes = new Uint8Array(buffer);
      let binary = '';
      for (let i = 0; i < bytes.byteLength; i++) binary += String.fromCharCode(bytes[i]);
      return btoa(binary);
    }
  </script>
</body>
</html>