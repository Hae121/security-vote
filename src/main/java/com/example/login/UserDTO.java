package com.example.login;

public class UserDTO {
	private final String id; //한 번만 설정되고, 외부 변경 불가
	private final String hashedPassword;

	public UserDTO(String id, String hashedPassword) {
		this.id = id;
		this.hashedPassword = hashedPassword;
	}

	public String getId() {
		return id;
	}

	private String getHashedPassword() {//외부 노출 차단
		return hashedPassword;
	}
	
	public boolean matchesPassword(String hashedInput) { //내부에서 해시 비교만
	    return this.hashedPassword.equals(hashedInput);
	}
	
//toString 오바리이드 방지	
	@Override
	public String toString() {
		return "UserDTO{id='" + id + "'}";
	}

}
