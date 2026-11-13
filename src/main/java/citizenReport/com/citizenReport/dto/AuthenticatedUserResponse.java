package citizenReport.com.citizenReport.dto;

import citizenReport.com.citizenReport.model.User;

public record AuthenticatedUserResponse(Long id, String fullName, String email, String role) {
    public static AuthenticatedUserResponse from(User user) {
        return new AuthenticatedUserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole());
    }
}
