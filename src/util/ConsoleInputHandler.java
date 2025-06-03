package util;

import java.io.Console;
import java.util.Scanner;
import java.util.Arrays;

import exception.InputException;
import exception.InputException.InputErrorType;

/**
 * 콘솔 입력을 안전하게 처리하기 위한 핸들러 클래스
 */
public class ConsoleInputHandler {
    private final Scanner scanner;
    private final Console console;
    private static final int MAX_RETRY_COUNT = 3;
    
    public ConsoleInputHandler(Scanner scanner) {
        this.scanner = scanner;
        this.console = System.console();
    }
    
    /**
     * 메뉴 선택 입력을 안전하게 처리
     */
    public int getMenuChoice(String prompt, int minChoice, int maxChoice) throws InputException {
        int retryCount = 0;
        
        while (retryCount < MAX_RETRY_COUNT) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return InputValidator.validateMenuChoice(input, minChoice, maxChoice);
                
            } catch (InputException e) {
                retryCount++;
                System.err.println("입력 오류: " + e.getMessage());
                
                if (retryCount < MAX_RETRY_COUNT) {
                    System.out.println("다시 시도해주세요. (남은 시도 횟수: " + (MAX_RETRY_COUNT - retryCount) + ")");
                }
            } catch (Exception e) {
                // Scanner 관련 다른 예외 처리
                scanner.nextLine(); // 잘못된 입력 버퍼 클리어
                retryCount++;
                System.err.println("입력 처리 중 오류가 발생했습니다: " + e.getMessage());
                
                if (retryCount < MAX_RETRY_COUNT) {
                    System.out.println("다시 시도해주세요. (남은 시도 횟수: " + (MAX_RETRY_COUNT - retryCount) + ")");
                }
            }
        }
        
        throw new InputException(InputErrorType.INVALID_MENU_CHOICE, 
            "최대 시도 횟수를 초과했습니다. 메뉴로 돌아갑니다.");
    }
    
    /**
     * 사용자 ID 입력을 안전하게 처리
     */
    public String getUserId(String prompt) throws InputException {
        int retryCount = 0;
        
        while (retryCount < MAX_RETRY_COUNT) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return InputValidator.validateUserId(input);
                
            } catch (InputException e) {
                retryCount++;
                System.err.println("입력 오류: " + e.getMessage());
                
                if (retryCount < MAX_RETRY_COUNT) {
                    System.out.println("다시 시도해주세요. (남은 시도 횟수: " + (MAX_RETRY_COUNT - retryCount) + ")");
                }
            }
        }
        
        throw new InputException(InputErrorType.INVALID_ID_FORMAT, 
            "최대 시도 횟수를 초과했습니다.");
    }
    
    /**
     * 비밀번호 입력을 안전하게 처리 (콘솔 사용)
     */
    public char[] getPassword(String prompt) throws InputException {
        if (console == null) {
            throw new InputException(InputErrorType.CONSOLE_NOT_AVAILABLE, 
                "보안을 위해 콘솔 환경에서만 비밀번호 입력이 가능합니다.");
        }
        
        int retryCount = 0;
        
        while (retryCount < MAX_RETRY_COUNT) {
            try {
                char[] password = console.readPassword(prompt);
                
                if (password == null || password.length == 0) {
                    retryCount++;
                    System.err.println("입력 오류: 비밀번호를 입력해주세요.");
                    
                    if (retryCount < MAX_RETRY_COUNT) {
                        System.out.println("다시 시도해주세요. (남은 시도 횟수: " + (MAX_RETRY_COUNT - retryCount) + ")");
                    }
                    continue;
                }
                
                return password;
                
            } catch (Exception e) {
                retryCount++;
                System.err.println("비밀번호 입력 중 오류가 발생했습니다: " + e.getMessage());
                
                if (retryCount < MAX_RETRY_COUNT) {
                    System.out.println("다시 시도해주세요. (남은 시도 횟수: " + (MAX_RETRY_COUNT - retryCount) + ")");
                }
            }
        }
        
        throw new InputException(InputErrorType.CONSOLE_NOT_AVAILABLE, 
            "비밀번호 입력에 실패했습니다.");
    }
    
    /**
     * 후보자 선택 입력을 안전하게 처리
     */
    public int getCandidateChoice(String prompt, int maxCandidates) throws InputException {
        int retryCount = 0;
        
        while (retryCount < MAX_RETRY_COUNT) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return InputValidator.validateCandidateChoice(input, maxCandidates);
                
            } catch (InputException e) {
                retryCount++;
                System.err.println("입력 오류: " + e.getMessage());
                
                if (retryCount < MAX_RETRY_COUNT) {
                    System.out.println("다시 시도해주세요. (남은 시도 횟수: " + (MAX_RETRY_COUNT - retryCount) + ")");
                }
            }
        }
        
        throw new InputException(InputErrorType.INVALID_CANDIDATE_CHOICE, 
            "최대 시도 횟수를 초과했습니다.");
    }
    
    /**
     * 확인 입력을 안전하게 처리
     */
    public boolean getConfirmation(String prompt) throws InputException {
        int retryCount = 0;
        
        while (retryCount < MAX_RETRY_COUNT) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return InputValidator.validateConfirmation(input);
                
            } catch (InputException e) {
                retryCount++;
                System.err.println("입력 오류: " + e.getMessage());
                
                if (retryCount < MAX_RETRY_COUNT) {
                    System.out.println("다시 시도해주세요. (남은 시도 횟수: " + (MAX_RETRY_COUNT - retryCount) + ")");
                }
            }
        }
        
        throw new InputException(InputErrorType.INVALID_CONFIRMATION, 
            "최대 시도 횟수를 초과했습니다.");
    }
    
    /**
     * 일반 문자열 입력을 안전하게 처리
     */
    public String getStringInput(String prompt, String fieldName) throws InputException {
        int retryCount = 0;
        
        while (retryCount < MAX_RETRY_COUNT) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return InputValidator.validateStringInput(input, fieldName);
                
            } catch (InputException e) {
                retryCount++;
                System.err.println("입력 오류: " + e.getMessage());
                
                if (retryCount < MAX_RETRY_COUNT) {
                    System.out.println("다시 시도해주세요. (남은 시도 횟수: " + (MAX_RETRY_COUNT - retryCount) + ")");
                }
            }
        }
        
        throw new InputException(InputErrorType.EMPTY_INPUT, 
            "최대 시도 횟수를 초과했습니다.");
    }
    
    /**
     * 비밀번호 확인을 위한 안전한 입력 처리
     */
    public char[] getPasswordWithConfirmation(String prompt, String confirmPrompt) throws InputException {
        if (console == null) {
            throw new InputException(InputErrorType.CONSOLE_NOT_AVAILABLE);
        }
        
        int retryCount = 0;
        
        while (retryCount < MAX_RETRY_COUNT) {
            char[] password = null;
            char[] confirmPassword = null;
            
            try {
                password = getPassword(prompt);
                confirmPassword = getPassword(confirmPrompt);
                
                if (Arrays.equals(password, confirmPassword)) {
                    // confirmPassword는 더 이상 필요하지 않으므로 클리어
                    Arrays.fill(confirmPassword, ' ');
                    return password;
                } else {
                    retryCount++;
                    System.err.println("입력 오류: 비밀번호가 일치하지 않습니다.");
                    
                    if (retryCount < MAX_RETRY_COUNT) {
                        System.out.println("다시 시도해주세요. (남은 시도 횟수: " + (MAX_RETRY_COUNT - retryCount) + ")");
                    }
                }
                
            } catch (InputException e) {
                throw e; // 비밀번호 입력 자체에서 발생한 예외는 그대로 전파
            } finally {
                // 메모리에서 비밀번호 정보 제거
                if (password != null) {
                    Arrays.fill(password, ' ');
                }
                if (confirmPassword != null) {
                    Arrays.fill(confirmPassword, ' ');
                }
            }
        }
        
        throw new InputException(InputErrorType.INVALID_CONFIRMATION, 
            "비밀번호 확인에 실패했습니다.");
    }
}