package citizenReport.com.citizenReport.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateComplaintRequest(
        @NotBlank @Size(max = 80) String issueType,
        @NotBlank @Size(max = 4000) String description,
        @NotBlank @Size(max = 160) String location) { }
