package citizenReport.com.citizenReport.exception;

public class CannotDeleteCurrentUserException extends RuntimeException {
    public CannotDeleteCurrentUserException() {
        super("You cannot delete the account you are currently using.");
    }
}
