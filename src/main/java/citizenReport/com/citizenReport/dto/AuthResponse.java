package citizenReport.com.citizenReport.dto;

import java.time.Instant;

public record AuthResponse(String tokenType, String accessToken, Instant expiresAt,
                           AuthenticatedUserResponse user) { }
