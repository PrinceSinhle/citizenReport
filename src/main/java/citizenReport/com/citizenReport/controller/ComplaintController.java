package citizenReport.com.citizenReport.controller;

import citizenReport.com.citizenReport.model.Complaint;
import citizenReport.com.citizenReport.model.User;
import citizenReport.com.citizenReport.repository.UserRepository;
import citizenReport.com.citizenReport.service.ComplaintService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ComplaintController {

    private final ComplaintService complaintService;
    private final UserRepository userRepository;

    public ComplaintController(
            ComplaintService complaintService,
            UserRepository userRepository) {
        this.complaintService = complaintService;
        this.userRepository = userRepository;
    }

    @GetMapping("/report")
    public String reportPage() {
        return "report";
    }

    @PostMapping("/submit")
    public String submitComplaint(
            @RequestParam String issueType,
            @RequestParam String description,
            @RequestParam String location,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        Complaint complaint = new Complaint();
        complaint.setIssueType(issueType);
        complaint.setDescription(description);
        complaint.setLocation(location);
        complaint.setUser(user);

        complaintService.saveComplaint(complaint);

        redirectAttributes.addFlashAttribute("successMessage",
                "Your complaint has been submitted successfully.");

        return "redirect:/dashboard";
    }
}