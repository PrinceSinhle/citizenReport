package citizenReport.com.citizenReport.controller;

import citizenReport.com.citizenReport.model.Complaint;
import citizenReport.com.citizenReport.model.Feedback;
import citizenReport.com.citizenReport.model.User;
import citizenReport.com.citizenReport.repository.UserRepository;
import citizenReport.com.citizenReport.service.ComplaintService;
import citizenReport.com.citizenReport.service.FeedbackService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final ComplaintService complaintService;
    private final FeedbackService feedbackService;
    private final UserRepository userRepository;

    public DashboardController(ComplaintService complaintService, FeedbackService feedbackService,
                               UserRepository userRepository) {
        this.complaintService = complaintService;
        this.feedbackService = feedbackService;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        List<Complaint> complaints = complaintService.getComplaintsByUser(user);
        List<Feedback> feedbacks = feedbackService.getFeedbackByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("complaints", complaints);
        model.addAttribute("feedbacks", feedbacks);

        return "dashboard";
    }
}