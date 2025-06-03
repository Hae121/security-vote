package manager;

import java.io.*;
import java.security.*;
import java.util.*;

import dto.UserDTO;
import util.RSAUtil;

public class UserManager {
    private static final String USER_DATA_DIR = "user_data";
    private static final String USER_INFO_FILE = "users.dat";
    
    public void createUser(String id, char[] password, boolean isAdmin) throws Exception {
        File userDir = new File(USER_DATA_DIR, id);
        userDir.mkdirs();
        
        // 기존 사용자 확인
        if (userExists(id)) {
            System.out.println("🔄 기존 사용자 " + id + " 정보를 사용합니다.");
            return;
        }
        
        // 솔트 생성
        String salt = generateSalt();
        
        // 비밀번호 해시화
        String hashedPassword = hashPasswordWithSalt(password, salt);
        
        // RSA 키 쌍 생성
        KeyPair keyPair = RSAUtil.generateKeyPair();
        
        // 공개키와 개인키 저장
        String publicKeyPath = new File(userDir, "public.key").getAbsolutePath();
        String privateKeyPath = new File(userDir, "private.key").getAbsolutePath();
        
        RSAUtil.savePublicKey(keyPair.getPublic(), publicKeyPath);
        RSAUtil.savePrivateKey(keyPair.getPrivate(), privateKeyPath);
        
        // 사용자 정보 저장
        saveUserInfo(id, hashedPassword, salt, isAdmin);
        
        System.out.println("✅ 사용자 " + id + " 생성 완료 (관리자: " + isAdmin + ")");
    }
    
    public void createUser(String id, String password, boolean isAdmin) throws Exception {
        char[] passwordChars = password.toCharArray();
        try {
            createUser(id, passwordChars, isAdmin);
        } finally {
            Arrays.fill(passwordChars, ' ');
        }
    }
    
    private boolean userExists(String id) {
        File userInfoFile = new File(USER_DATA_DIR, USER_INFO_FILE);
        if (!userInfoFile.exists()) return false;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(userInfoFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 4 && parts[0].equals(id)) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("❌ 사용자 존재 확인 오류: " + e.getMessage());
        }
        return false;
    }
    
    private void saveUserInfo(String id, String hashedPassword, String salt, boolean isAdmin) throws Exception {
        File userInfoFile = new File(USER_DATA_DIR, USER_INFO_FILE);
        
        // 기존 사용자 정보 읽기
        Set<String> existingUsers = new HashSet<>();
        if (userInfoFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(userInfoFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    existingUsers.add(line);
                }
            }
        }
        
        // 새 사용자 정보 추가
        String userInfo = id + ":" + hashedPassword + ":" + salt + ":" + isAdmin;
        existingUsers.add(userInfo);
        
        // 파일에 저장
        try (PrintWriter writer = new PrintWriter(new FileWriter(userInfoFile))) {
            for (String info : existingUsers) {
                writer.println(info);
            }
        }
    }
    
    public UserDTO authenticate(String id, char[] password) throws Exception {
        File userInfoFile = new File(USER_DATA_DIR, USER_INFO_FILE);
        if (!userInfoFile.exists()) {
            return null;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(userInfoFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 4 && parts[0].equals(id)) {
                    String storedHash = parts[1];
                    String salt = parts[2];
                    boolean isAdmin = Boolean.parseBoolean(parts[3]);
                    
                    // 입력된 비밀번호 해시화하여 비교
                    String inputHash = hashPasswordWithSalt(password, salt);
                    
                    if (storedHash.equals(inputHash)) {
                        // 공개키 로드
                        PublicKey publicKey = loadUserPublicKey(id);
                        return new UserDTO(id, storedHash, salt, isAdmin, publicKey);
                    }
                }
            }
        }
        
        return null;
    }
    
    // 오버로드된 메서드 - 기존 호환성 유지
    public UserDTO authenticate(String id, String password) throws Exception {
        char[] passwordChars = password.toCharArray();
        try {
            return authenticate(id, passwordChars);
        } finally {
            Arrays.fill(passwordChars, ' ');
        }
    }
    
    // Console을 사용한 안전한 비밀번호 입력
    public UserDTO authenticateWithConsole(String id) throws Exception {
        Console console = System.console();
        if (console != null) {
            char[] passwordChars = console.readPassword("🔐 비밀번호: ");
            try {
                return authenticate(id, passwordChars);
            } finally {
                // 메모리에서 즉시 제거
                Arrays.fill(passwordChars, ' ');
            }
        } else {
            System.err.println("❌ Console을 사용할 수 없습니다. IDE에서 실행 시 제한될 수 있습니다.");
            return null;
        }
    }
    
    // Console을 사용한 안전한 사용자 생성
    public void createUserWithConsole(String id, boolean isAdmin) throws Exception {
        Console console = System.console();
        if (console != null) {
            char[] passwordChars = console.readPassword("🔐 새 비밀번호: ");
            char[] confirmChars = console.readPassword("🔐 비밀번호 확인: ");
            
            try {
                // 비밀번호 일치 확인
                if (Arrays.equals(passwordChars, confirmChars)) {
                    createUser(id, passwordChars, isAdmin);
                } else {
                    System.err.println("❌ 비밀번호가 일치하지 않습니다.");
                }
            } finally {
                // 메모리에서 즉시 제거
                Arrays.fill(passwordChars, ' ');
                Arrays.fill(confirmChars, ' ');
            }
        } else {
            System.err.println("❌ Console을 사용할 수 없습니다. IDE에서 실행 시 제한될 수 있습니다.");
        }
    }
    
    public PublicKey loadUserPublicKey(String userId) throws Exception {
        String publicKeyPath = new File(USER_DATA_DIR, userId + "/public.key").getAbsolutePath();
        return RSAUtil.loadPublicKey(publicKeyPath);
    }
    
    public PrivateKey loadUserPrivateKey(String userId) throws Exception {
        String privateKeyPath = new File(USER_DATA_DIR, userId + "/private.key").getAbsolutePath();
        return RSAUtil.loadPrivateKey(privateKeyPath);
    }
    
    private String generateSalt() {
    	SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        
        StringBuilder sb = new StringBuilder();
        for (byte b : salt) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    private String hashPasswordWithSalt(char[] password, String salt) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        
        // char[]를 byte[]로 안전하게 변환
        byte[] passwordBytes = new byte[password.length * 2];
        for (int i = 0; i < password.length; i++) {
            passwordBytes[i * 2] = (byte) (password[i] >> 8);
            passwordBytes[i * 2 + 1] = (byte) password[i];
        }
        
        try {
            // 솔트와 함께 해시화
            digest.update(passwordBytes);
            digest.update(salt.getBytes("UTF-8"));
            byte[] hashedBytes = digest.digest();
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } finally {
            // 임시 바이트 배열 초기화
            Arrays.fill(passwordBytes, (byte) 0);
        }
    }
    
}