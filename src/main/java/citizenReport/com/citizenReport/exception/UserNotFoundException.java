package citizenReport.com.citizenReport.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("User account not found.");
    }
}
