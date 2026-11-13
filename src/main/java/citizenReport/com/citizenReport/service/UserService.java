package citizenReport.com.citizenReport.service;

import citizenReport.com.citizenReport.dto.RegisterRequest;
import citizenReport.com.citizenReport.dto.UpdateUserRequest;
import citizenReport.com.citizenReport.exception.EmailAlreadyRegisteredException;
import citizenReport.com.citizenReport.exception.UserNotFoundException;
import citizenReport.com.citizenReport.model.User;
import citizenReport.com.citizenReport.repository.ComplaintRepository;
import citizenReport.com.citizenReport.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Locale;

@Service
public class UserService {
    private final UserRepository users;
    private final ComplaintRepository complaints;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository users, ComplaintRepository complaints, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.complaints = complaints;
        this.passwordEncoder = passwordEncoder;
    }

    public User create(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (users.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException();
        }

        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPhoneNumber(normalizeOptional(request.phoneNumber()));
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole("USER");
        return users.save(user);
    }

    public List<User> findAll() {
        return users.findAll();
    }

    public User findById(Long id) {
        return users.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User findByEmail(String email) {
        return users.findByEmail(normalizeEmail(email)).orElseThrow(UserNotFoundException::new);
    }

    public User update(Long id, UpdateUserRequest request) {
        User user = findById(id);
        String email = normalizeEmail(request.email());
        if (!email.equals(user.getEmail()) && users.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException();
        }

        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPhoneNumber(normalizeOptional(request.phoneNumber()));
        if (request.password() != null) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        return users.save(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = findById(id);
        complaints.deleteAllByUser(user);
        users.delete(user);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
