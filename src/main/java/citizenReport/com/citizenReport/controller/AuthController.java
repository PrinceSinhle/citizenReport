package citizenReport.com.citizenReport.controller;

import citizenReport.com.citizenReport.dto.LoginRequest;
import citizenReport.com.citizenReport.dto.RegisterRequest;
import citizenReport.com.citizenReport.dto.AuthResponse;
import citizenReport.com.citizenReport.dto.AuthenticatedUserResponse;
import citizenReport.com.citizenReport.service.UserService;
import citizenReport.com.citizenReport.service.JwtTokenService;
import citizenReport.com.citizenReport.service.RegistrationEmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final RegistrationEmailService registrationEmailService;

    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtTokenService jwtTokenService,
                          RegistrationEmailService registrationEmailService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.registrationEmailService = registrationEmailService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthenticatedUserResponse register(@Valid @RequestBody RegisterRequest request) {
        var user = userService.create(request);
        registrationEmailService.sendWelcomeEmail(user);
        return AuthenticatedUserResponse.from(user);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.email().trim().toLowerCase(java.util.Locale.ROOT), request.password()));
        return jwtTokenService.issue(userService.findByEmail(authentication.getName()));
    }

    @GetMapping("/me")
    public AuthenticatedUserResponse me(Authentication authentication) {
        return AuthenticatedUserResponse.from(userService.findById(Long.parseLong(authentication.getName())));
    }
}
