package citizenReport.com.citizenReport.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(max = 25) String fullName,
        @NotBlank @Email @Size(max = 30) String email,
        @NotBlank @Size(min = 10, max = 20) String password,
        @Size(max = 30) String phoneNumber) { }
