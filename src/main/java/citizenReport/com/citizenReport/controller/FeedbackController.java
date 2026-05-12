package citizenReport.com.citizenReport.controller;

import citizenReport.com.citizenReport.model.User;
import citizenReport.com.citizenReport.repository.UserRepository;
import citizenReport.com.citizenReport.service.FeedbackService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final UserRepository userRepository;

    public FeedbackController(FeedbackService feedbackService,
                              UserRepository userRepository) {
        this.feedbackService = feedbackService;
        this.userRepository = userRepository;
    }

    @PostMapping("/feedback")
    public String submitFeedback(
            @RequestParam String comment,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(authentication.getName());
        if (user == null) {
            return "redirect:/login";
        }

        feedbackService.saveFeedback(comment, user);

        return "redirect:/dashboard";
    }
}
