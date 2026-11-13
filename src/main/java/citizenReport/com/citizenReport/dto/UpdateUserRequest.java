package citizenReport.com.citizenReport.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank @Size(max = 120) String fullName,
        @NotBlank @Email @Size(max = 254) String email,
        @Size(min = 10, max = 72) String password,
        @Size(max = 30) String phoneNumber) { }
