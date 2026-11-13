package citizenReport.com.citizenReport.exception;

public class ComplaintNotFoundException extends RuntimeException {
    public ComplaintNotFoundException() {
        super("Complaint not found.");
    }
}
