package citizenReport.com.citizenReport.dto;

import citizenReport.com.citizenReport.model.Complaint;
import citizenReport.com.citizenReport.model.ComplaintStatus;
import java.time.LocalDateTime;

public record ComplaintResponse(Long id, String issueType, String description, String location,
                                ComplaintStatus status, LocalDateTime createdAt) {
    public static ComplaintResponse from(Complaint complaint) {
        return new ComplaintResponse(complaint.getId(), complaint.getIssueType(), complaint.getDescription(),
                complaint.getLocation(), complaint.getStatus(), complaint.getCreatedAt());
    }
}
