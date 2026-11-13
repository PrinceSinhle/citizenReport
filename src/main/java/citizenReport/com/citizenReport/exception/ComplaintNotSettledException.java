package citizenReport.com.citizenReport.exception;

public class ComplaintNotSettledException extends RuntimeException {
    public ComplaintNotSettledException() {
        super("Only resolved or rejected complaints can be deleted from the settled archive.");
    }
}
