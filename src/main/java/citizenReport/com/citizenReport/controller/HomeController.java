package citizenReport.com.citizenReport.controller;

import citizenReport.com.citizenReport.service.FeedbackService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final FeedbackService feedbackService;

    public HomeController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/")
    public String homePage(Model model) {
        var featured = feedbackService.getFeaturedFeedback();
        if (featured.isEmpty()) {
            model.addAttribute("feedbacks", feedbackService.getFiveOldestFeedbacks());
        } else {
            model.addAttribute("feedbacks", featured);
        }
        return "home";
    }
}