package citizenReport.com.citizenReport.controller;

import citizenReport.com.citizenReport.dto.ComplaintResponse;
import citizenReport.com.citizenReport.dto.CreateComplaintRequest;
import citizenReport.com.citizenReport.model.User;
import citizenReport.com.citizenReport.service.ComplaintService;
import citizenReport.com.citizenReport.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {
    private final ComplaintService complaints;
    private final UserService users;

    public ComplaintController(ComplaintService complaints, UserService users) {
        this.complaints = complaints;
        this.users = users;
    }

    @GetMapping
    public List<ComplaintResponse> list(Authentication authentication) {
        User user = currentUser(authentication);
        var records = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                ? complaints.findAll() : complaints.findFor(user);
        return records.stream().map(ComplaintResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ComplaintResponse get(@PathVariable Long id, Authentication authentication) {
        User user = currentUser(authentication);
        var isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        var complaint = isAdmin ? complaints.findById(id) : complaints.findOwned(id, user);
        return ComplaintResponse.from(complaint);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComplaintResponse create(@Valid @RequestBody CreateComplaintRequest request, Authentication authentication) {
        return ComplaintResponse.from(complaints.create(request.issueType(), request.description(), request.location(), currentUser(authentication)));
    }

    private User currentUser(Authentication authentication) {
        return users.findById(Long.parseLong(authentication.getName()));
    }
}
