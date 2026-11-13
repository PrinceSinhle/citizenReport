package citizenReport.com.citizenReport.dto;

import citizenReport.com.citizenReport.model.User;

public record UserResponse(Long id, String fullName, String email, String phoneNumber, String role) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getPhoneNumber(), user.getRole());
    }
}
