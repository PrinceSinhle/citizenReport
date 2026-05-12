package citizenReport.com.citizenReport.controller;

import citizenReport.com.citizenReport.model.User;
import citizenReport.com.citizenReport.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String phoneNumber) {

        User user = new User();

        user.setFullName(fullName);
        user.setEmail(email);

        /* ENCRYPT PASSWORD */
        user.setPassword(
                passwordEncoder.encode(password)
        );

        user.setPhoneNumber(phoneNumber);

        /* DEFAULT ROLE */
        user.setRole("USER");

        userRepository.save(user);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/delete-account")
    public String deleteAccount(
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email);

        if (user != null) {
            userRepository.delete(user);
        }

        return "redirect:/login";
    }
}