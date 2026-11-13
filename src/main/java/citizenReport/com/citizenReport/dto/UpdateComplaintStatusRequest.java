package citizenReport.com.citizenReport.dto;

import citizenReport.com.citizenReport.model.ComplaintStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateComplaintStatusRequest(@NotNull ComplaintStatus status) { }
