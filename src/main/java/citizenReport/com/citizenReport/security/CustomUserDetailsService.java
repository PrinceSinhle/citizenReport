package citizenReport.com.citizenReport.security;

import citizenReport.com.citizenReport.model.User;
import citizenReport.com.citizenReport.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserRepository userRepository;

    public CustomUserDetailsService(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        logger.info("Authenticating user: {}", email);
        User user = userRepository.findByEmail(email);

        if (user == null) {
            logger.warn("User not found: {}", email);
            throw new UsernameNotFoundException(
                    "User not found"
            );
        }

        logger.info("Loaded user {} with role {}", email, user.getRole());

        return new org.springframework.security.core.userdetails.User(

                user.getEmail(),

                user.getPassword(),

                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + user.getRole()
                        )
                )
        );
    }
}