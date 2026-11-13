package citizenReport.com.citizenReport.controller;

import citizenReport.com.citizenReport.dto.RegisterRequest;
import citizenReport.com.citizenReport.dto.UpdateUserRequest;
import citizenReport.com.citizenReport.dto.UserResponse;
import citizenReport.com.citizenReport.exception.CannotDeleteCurrentUserException;
import citizenReport.com.citizenReport.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody RegisterRequest request) {
        return UserResponse.from(users.create(request));
    }

    @GetMapping
    public List<UserResponse> list() {
        return users.findAll().stream().map(UserResponse::from).toList();
    }

    @GetMapping("/by-email")
    public UserResponse findByEmail(@RequestParam String email) {
        return UserResponse.from(users.findByEmail(email));
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Long id) {
        return UserResponse.from(users.findById(id));
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return UserResponse.from(users.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication authentication) {
        if (id.toString().equals(authentication.getName())) {
            throw new CannotDeleteCurrentUserException();
        }
        users.delete(id);
    }
}
