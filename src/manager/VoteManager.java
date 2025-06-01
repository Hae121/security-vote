package manager;

import java.io.*;
import java.security.*;
import java.util.*;
import javax.crypto.SecretKey;

import util.AESUtil;
import util.RSAUtil;

import java.util.Base64;

public class VoteManager {
    private static final String VOTE_DATA_DIR = "vote_data";
    private static final String VOTER_RECORD_FILE = "voters.dat";
    
    private UserManager userManager;
    
    public VoteManager() {
        this.userManager = new UserManager();
    }
    
    public boolean hasVoted(String userId) {
        File voterRecordFile = new File(VOTE_DATA_DIR, VOTER_RECORD_FILE);
        if (!voterRecordFile.exists()) {
            return false;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(voterRecordFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals(userId)) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("❌ 투표 기록 확인 오류: " + e.getMessage());
        }
        
        return false;
    }
    
    public void castVote(String userId, String candidate) throws Exception {
        if (hasVoted(userId)) {
            throw new IllegalStateException("이미 투표한 사용자입니다.");
        }
        
        System.out.println("🔐 전자봉투 기술을 사용하여 투표를 암호화하는 중...");
        
        // 1. AES 키 생성
        SecretKey aesKey = AESUtil.generateAESKey();
        byte[] iv = AESUtil.generateIV();
        
        // 2. 투표 내용을 AES로 암호화
        String encryptedVote = AESUtil.encrypt(candidate, aesKey, iv);
        
        // 3. 사용자의 공개키로 AES 키 암호화 (전자봉투)
        PublicKey userPublicKey = userManager.loadUserPublicKey(userId);
        String encryptedAESKey = RSAUtil.encryptAESKey(aesKey.getEncoded(), userPublicKey);
        
        // 4. 디지털 서명 생성
        PrivateKey userPrivateKey = userManager.loadUserPrivateKey(userId);
        String signature = createDigitalSignature(candidate, userPrivateKey);
        
        // 5. 투표 데이터 저장
        String voteId = UUID.randomUUID().toString();
        saveEncryptedVote(voteId, userId, encryptedVote, encryptedAESKey, iv, signature);
        
        // 6. 투표자 기록
        recordVoter(userId);
        
        System.out.println("✅ 전자봉투로 보호된 투표가 저장되었습니다.");
    }
    
    private void saveEncryptedVote(String voteId, String userId, String encryptedVote, 
                                   String encryptedKey, byte[] iv, String signature) throws Exception {
        File voteFile = new File(VOTE_DATA_DIR, "vote_" + voteId + ".dat");
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(voteFile))) {
            writer.println("VOTE_ID:" + voteId);
            writer.println("USER_ID:" + userId);
            writer.println("ENCRYPTED_VOTE:" + encryptedVote);
            writer.println("ENCRYPTED_KEY:" + encryptedKey);
            writer.println("IV:" + Base64.getEncoder().encodeToString(iv));
            writer.println("SIGNATURE:" + signature);
            writer.println("TIMESTAMP:" + System.currentTimeMillis());
        }
    }
    
    private void recordVoter(String userId) throws Exception {
        File voterRecordFile = new File(VOTE_DATA_DIR, VOTER_RECORD_FILE);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(voterRecordFile, true))) {
            writer.println(userId);
        }
    }
    
    private String createDigitalSignature(String data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes("UTF-8"));
        byte[] signedBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signedBytes);
    }
    
    public Map<String, Integer> countVotes() throws Exception {
        Map<String, Integer> results = new HashMap<>();
        File voteDir = new File(VOTE_DATA_DIR);
        
        if (!voteDir.exists() || !voteDir.isDirectory()) {
            return results;
        }
        
        File[] voteFiles = voteDir.listFiles((dir, name) -> name.startsWith("vote_") && name.endsWith(".dat"));
        if (voteFiles == null) return results;
        
        for (File voteFile : voteFiles) {
            try {
                String decryptedVote = decryptVote(voteFile);
                if (decryptedVote != null) {
                    results.put(decryptedVote, results.getOrDefault(decryptedVote, 0) + 1);
                }
            } catch (Exception e) {
                System.err.println("⚠️  투표 파일 복호화 실패: " + voteFile.getName() + " - " + e.getMessage());
            }
        }
        
        return results;
    }
    
    private String decryptVote(File voteFile) throws Exception {
        Map<String, String> voteData = parseVoteFile(voteFile);
        
        String userId = voteData.get("USER_ID");
        String encryptedVote = voteData.get("ENCRYPTED_VOTE");
        String encryptedKey = voteData.get("ENCRYPTED_KEY");
        String ivBase64 = voteData.get("IV");
        
        // 사용자의 개인키로 AES 키 복호화
        PrivateKey userPrivateKey = userManager.loadUserPrivateKey(userId);
        byte[] aesKeyBytes = RSAUtil.decryptAESKey(encryptedKey, userPrivateKey);
        SecretKey aesKey = AESUtil.decodeKey(Base64.getEncoder().encodeToString(aesKeyBytes));
        
        // AES로 투표 내용 복호화
        byte[] iv = Base64.getDecoder().decode(ivBase64);
        String decryptedVote = AESUtil.decrypt(encryptedVote, aesKey, iv);
        
        return decryptedVote;
    }
    
    public boolean verifyAllVotes() throws Exception {
        File voteDir = new File(VOTE_DATA_DIR);
        if (!voteDir.exists()) return true;
        
        File[] voteFiles = voteDir.listFiles((dir, name) -> name.startsWith("vote_") && name.endsWith(".dat"));
        if (voteFiles == null) return true;
        
        int totalVotes = voteFiles.length;
        int validVotes = 0;
        
        System.out.println("🔍 투표 데이터 검증 시작...");
        
        for (File voteFile : voteFiles) {
            try {
                if (verifyVoteIntegrity(voteFile)) {
                    validVotes++;
                    System.out.println("✅ " + voteFile.getName() + " - 유효");
                } else {
                    System.out.println("❌ " + voteFile.getName() + " - 무효");
                }
            } catch (Exception e) {
                System.out.println("⚠️  " + voteFile.getName() + " - 검증 오류: " + e.getMessage());
            }
        }
        
        System.out.println("📊 검증 결과: " + validVotes + "/" + totalVotes + " 투표가 유효합니다.");
        return validVotes == totalVotes;
    }
    
    private boolean verifyVoteIntegrity(File voteFile) throws Exception {
        Map<String, String> voteData = parseVoteFile(voteFile);
        
        String userId = voteData.get("USER_ID");
        String signature = voteData.get("SIGNATURE");
        
        // 실제 투표 내용 복호화
        String decryptedVote = decryptVote(voteFile);
        
        // 디지털 서명 검증
        PublicKey userPublicKey = userManager.loadUserPublicKey(userId);
        return verifyDigitalSignature(decryptedVote, signature, userPublicKey);
    }
    
    private boolean verifyDigitalSignature(String data, String signatureBase64, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(data.getBytes("UTF-8"));
        byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);
        return signature.verify(signatureBytes);
    }
    
    private Map<String, String> parseVoteFile(File voteFile) throws Exception {
        Map<String, String> data = new HashMap<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(voteFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    data.put(parts[0], parts[1]);
                }
            }
        }
        
        return data;
    }
    
    /**
     * 모든 투표 데이터를 초기화합니다.
     * - 투표 파일들 삭제
     * - 투표자 기록 파일 삭제
     */
    public void resetAllVoteData() throws Exception {
        File voteDir = new File(VOTE_DATA_DIR);
        
        if (!voteDir.exists()) {
            System.out.println("🔄 투표 데이터 디렉토리가 존재하지 않습니다.");
            return;
        }
        
        int deletedFiles = 0;
        
        // 투표 파일들 삭제
        File[] voteFiles = voteDir.listFiles((dir, name) -> name.startsWith("vote_") && name.endsWith(".dat"));
        if (voteFiles != null) {
            for (File voteFile : voteFiles) {
                if (voteFile.delete()) {
                    deletedFiles++;
                } else {
                    System.err.println("⚠️  파일 삭제 실패: " + voteFile.getName());
                }
            }
        }
        
        // 투표자 기록 파일 삭제
        File voterRecordFile = new File(VOTE_DATA_DIR, VOTER_RECORD_FILE);
        if (voterRecordFile.exists()) {
            if (voterRecordFile.delete()) {
                System.out.println("🗑️  투표자 기록 파일이 삭제되었습니다.");
            } else {
                System.err.println("⚠️  투표자 기록 파일 삭제 실패");
            }
        }
        
        System.out.println("🧹 총 " + deletedFiles + "개의 투표 파일이 삭제되었습니다.");
        System.out.println("✨ 투표 데이터 초기화가 완료되었습니다.");
    }
}