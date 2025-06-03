package util;

import exception.InputException;
import exception.InputException.InputErrorType;


public class InputValidator {
    
    private static final int MAX_ID_LENGTH = 20;
    private static final int MIN_ID_LENGTH = 2;
    private static final int MAX_INPUT_LENGTH = 100;
    

    public static int validateMenuChoice(String input, int minChoice, int maxChoice) throws InputException {
        if (input == null || input.trim().isEmpty()) {
            throw new InputException(InputErrorType.EMPTY_INPUT);
        }
        
        if (input.trim().length() > MAX_INPUT_LENGTH) {
            throw new InputException(InputErrorType.INPUT_TOO_LONG);
        }
        
        try {
            int choice = Integer.parseInt(input.trim());
            if (choice < minChoice || choice > maxChoice) {
                throw new InputException(InputErrorType.INVALID_MENU_CHOICE, 
                    String.format("메뉴 선택은 %d부터 %d 사이의 숫자여야 합니다.", minChoice, maxChoice));
            }
            return choice;
        } catch (NumberFormatException e) {
            throw new InputException(InputErrorType.INVALID_NUMBER_FORMAT, e);
        }
    }
    

    public static String validateUserId(String input) throws InputException {
        if (input == null || input.trim().isEmpty()) {
            throw new InputException(InputErrorType.EMPTY_INPUT, "사용자 ID를 입력해주세요.");
        }
        
        String trimmedInput = input.trim();
        
        if (trimmedInput.length() < MIN_ID_LENGTH) {
            throw new InputException(InputErrorType.INVALID_ID_FORMAT, 
                String.format("ID는 최소 %d자 이상이어야 합니다.", MIN_ID_LENGTH));
        }
        
        if (trimmedInput.length() > MAX_ID_LENGTH) {
            throw new InputException(InputErrorType.INVALID_ID_FORMAT, 
                String.format("ID는 최대 %d자까지 입력 가능합니다.", MAX_ID_LENGTH));
        }
        
        // ID에 특수문자나 공백 포함 여부 검사
        if (!trimmedInput.matches("^[a-zA-Z0-9_]+$")) {
            throw new InputException(InputErrorType.INVALID_ID_FORMAT, 
                "ID는 영문자, 숫자, 언더스코어(_)만 사용 가능합니다.");
        }
        
        return trimmedInput;
    }
    

    public static int validateCandidateChoice(String input, int maxCandidates) throws InputException {
        if (input == null || input.trim().isEmpty()) {
            throw new InputException(InputErrorType.EMPTY_INPUT, "후보자 번호를 선택해주세요.");
        }
        
        try {
            int choice = Integer.parseInt(input.trim());
            if (choice < 1 || choice > maxCandidates) {
                throw new InputException(InputErrorType.INVALID_CANDIDATE_CHOICE, 
                    String.format("후보자 번호는 1부터 %d 사이여야 합니다.", maxCandidates));
            }
            return choice;
        } catch (NumberFormatException e) {
            throw new InputException(InputErrorType.INVALID_NUMBER_FORMAT, 
                "후보자 번호는 숫자로 입력해주세요.");
        }
    }
    

    public static boolean validateConfirmation(String input) throws InputException {
        if (input == null || input.trim().isEmpty()) {
            throw new InputException(InputErrorType.EMPTY_INPUT, "y 또는 n을 입력해주세요.");
        }
        
        String trimmedInput = input.trim().toLowerCase();
        
        if (trimmedInput.equals("y") || trimmedInput.equals("yes")) {
            return true;
        } else if (trimmedInput.equals("n") || trimmedInput.equals("no")) {
            return false;
        } else {
            throw new InputException(InputErrorType.INVALID_CONFIRMATION, 
                "y(yes) 또는 n(no)만 입력 가능합니다.");
        }
    }
    

    public static String validateStringInput(String input, String fieldName) throws InputException {
        if (input == null || input.trim().isEmpty()) {
            throw new InputException(InputErrorType.EMPTY_INPUT, 
                fieldName + "을(를) 입력해주세요.");
        }
        
        if (input.trim().length() > MAX_INPUT_LENGTH) {
            throw new InputException(InputErrorType.INPUT_TOO_LONG, 
                fieldName + "은(는) " + MAX_INPUT_LENGTH + "자 이하로 입력해주세요.");
        }
        
        return input.trim();
    }
}