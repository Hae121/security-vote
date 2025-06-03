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
        
        if (userExists(id)) {
            System.out.println("기존 사용자 " + id + " 정보를 사용합니다.");
            return;
        } else { } //이미 이전에 실행한경우
        
        String salt = generateSalt();
        
        String hashedPassword = hashPasswordWithSalt(password, salt);
        
        KeyPair keyPair = RSAUtil.generateKeyPair();
        
        String publicKeyPath = new File(userDir, "public.key").getAbsolutePath();
        String privateKeyPath = new File(userDir, "private.key").getAbsolutePath();
        
        RSAUtil.savePublicKey(keyPair.getPublic(), publicKeyPath);
        RSAUtil.savePrivateKey(keyPair.getPrivate(), privateKeyPath);
        
        saveUserInfo(id, hashedPassword, salt, isAdmin);
        
        System.out.println("사용자 " + id + " 생성 완료 (관리자: " + isAdmin + ")");
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
        File userDataDir = new File(USER_DATA_DIR);
        if (!userDataDir.exists()) {
            userDataDir.mkdirs();
        } else { } //이미 userDataDir 이 있는 경우 넘어감
        
        File userInfoFile = new File(USER_DATA_DIR, USER_INFO_FILE);
        if (!userInfoFile.exists()) return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(userInfoFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 4 && parts[0].equals(id)) {
                    return true;
                } else { } //불일치의 경우 catch 됨
            }
        } catch (Exception e) {
            System.err.println("사용자 존재 확인 오류: " + e.getMessage());
        }
        return false;
    }
    
    private void saveUserInfo(String id, String hashedPassword, String salt, boolean isAdmin) throws Exception {
        File userInfoFile = new File(USER_DATA_DIR, USER_INFO_FILE);
        

        Set<String> existingUsers = new HashSet<>();
        if (userInfoFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(userInfoFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    existingUsers.add(line);
                }
            }
        } else { } //userInfoFile이 없는 경우 불가
        
        String userInfo = id + ":" + hashedPassword + ":" + salt + ":" + isAdmin;
        existingUsers.add(userInfo);
        

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
        } else { } //userInfoFile이 없는 경우 불가
        
        try (BufferedReader reader = new BufferedReader(new FileReader(userInfoFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 4 && parts[0].equals(id)) {
                    String storedHash = parts[1];
                    String salt = parts[2];
                    boolean isAdmin = Boolean.parseBoolean(parts[3]);
                    
                    String inputHash = hashPasswordWithSalt(password, salt);
                    
                    if (storedHash.equals(inputHash)) {
                        PublicKey publicKey = loadUserPublicKey(id);
                        return new UserDTO(id, storedHash, salt, isAdmin, publicKey);
                    }
                }
            }
        }
        
        return null;
    }
    

    public UserDTO authenticate(String id, String password) throws Exception {
        char[] passwordChars = password.toCharArray();
        try {
            return authenticate(id, passwordChars);
        } finally {
            Arrays.fill(passwordChars, ' ');
        }
    }
    
    public UserDTO authenticateWithConsole(String id) throws Exception {
        Console console = System.console();
        if (console != null) {
            char[] passwordChars = console.readPassword("비밀번호: ");
            try {
                return authenticate(id, passwordChars);
            } finally {
                Arrays.fill(passwordChars, ' ');
            }
        } else {
            System.err.println("Console을 사용할 수 없습니다.");
            return null;
        }
    }
    
    public void createUserWithConsole(String id, boolean isAdmin) throws Exception {
        Console console = System.console();
        if (console != null) {
            char[] passwordChars = console.readPassword("새 비밀번호: ");
            char[] confirmChars = console.readPassword("비밀번호 확인: ");
            
            try {
                if (Arrays.equals(passwordChars, confirmChars)) {
                    createUser(id, passwordChars, isAdmin);
                } else {
                    System.err.println("비밀번호가 일치하지 않습니다.");
                }
            } finally {
                Arrays.fill(passwordChars, ' ');
                Arrays.fill(confirmChars, ' ');
            }
        } else {
            System.err.println("Console을 사용할 수 없습니다.");
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
        
        byte[] passwordBytes = new byte[password.length * 2];
        for (int i = 0; i < password.length; i++) {
            passwordBytes[i * 2] = (byte) (password[i] >> 8);
            passwordBytes[i * 2 + 1] = (byte) password[i];
        }
        
        try {
            digest.update(passwordBytes);
            digest.update(salt.getBytes("UTF-8"));
            byte[] hashedBytes = digest.digest();
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } finally {
            Arrays.fill(passwordBytes, (byte) 0);
        }
    }
    
}