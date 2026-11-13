package citizenReport.com.citizenReport.service;

import citizenReport.com.citizenReport.dto.AuthResponse;
import citizenReport.com.citizenReport.dto.AuthenticatedUserResponse;
import citizenReport.com.citizenReport.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class JwtTokenService {
    private final JwtEncoder encoder;
    private final String issuer;
    private final Duration tokenLifetime;

    public JwtTokenService(JwtEncoder encoder,
                           @Value("${app.jwt.issuer}") String issuer,
                           @Value("${app.jwt.access-token-ttl-seconds:600}") long tokenLifetimeSeconds) {
        if (tokenLifetimeSeconds < 60 || tokenLifetimeSeconds > 900) {
            throw new IllegalStateException("JWT access token lifetime must be between 60 and 900 seconds.");
        }
        this.encoder = encoder;
        this.issuer = issuer;
        this.tokenLifetime = Duration.ofSeconds(tokenLifetimeSeconds);
    }

    public AuthResponse issue(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(tokenLifetime);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getId().toString())
                .audience(List.of(issuer))
                .claim("roles", List.of("ROLE_" + user.getRole()))
                .build();
        String token = encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).type("JWT").build(), claims)).getTokenValue();
        return new AuthResponse("Bearer", token, expiresAt, AuthenticatedUserResponse.from(user));
    }
}
