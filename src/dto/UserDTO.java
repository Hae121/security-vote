package dto;

import java.security.PublicKey;

public class UserDTO {
    private final String id;
    private final String hashedPassword;
    private final String salt;
    private final boolean isAdmin;
    private final PublicKey publicKey;
    
    public UserDTO(String id, String hashedPassword, String salt, boolean isAdmin, PublicKey publicKey) {
        this.id = id;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
        this.isAdmin = isAdmin;
        this.publicKey = publicKey;
    }
    
    public String getId() {
        return id;
    }
    
    public boolean isAdmin() {
        return isAdmin;
    }
    
    public PublicKey getPublicKey() {
        return publicKey;
    }
    
    public String getSalt() {
        return salt;
    }
    
    public boolean matchesPassword(String hashedInput) {
        return this.hashedPassword.equals(hashedInput);
    }
    
    @Override
    public String toString() {
        return "UserDTO{id='" + id + "', isAdmin=" + isAdmin + "}";
    }
}