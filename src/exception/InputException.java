package exception;

@SuppressWarnings("serial")
public class InputException extends Exception {
    private final InputErrorType errorType;
    
    public enum InputErrorType {
        INVALID_MENU_CHOICE("잘못된 메뉴 선택"),
        INVALID_NUMBER_FORMAT("숫자 형식이 올바르지 않습니다"),
        EMPTY_INPUT("입력값이 비어있습니다"),
        INVALID_ID_FORMAT("ID 형식이 올바르지 않습니다"),
        INVALID_CANDIDATE_CHOICE("잘못된 후보자 선택"),
        CONSOLE_NOT_AVAILABLE("콘솔을 사용할 수 없습니다"),
        INPUT_TOO_LONG("입력값이 너무 깁니다"),
        INVALID_CONFIRMATION("확인 입력이 올바르지 않습니다");
        
        private final String message;
        
        InputErrorType(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    public InputException(InputErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }
    
    public InputException(InputErrorType errorType, String customMessage) {
        super(customMessage);
        this.errorType = errorType;
    }
    
    public InputException(InputErrorType errorType, Throwable cause) {
        super(errorType.getMessage(), cause);
        this.errorType = errorType;
    }
    
    public InputErrorType getErrorType() {
        return errorType;
    }
}