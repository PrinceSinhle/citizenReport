package citizenReport.com.citizenReport.service;

import citizenReport.com.citizenReport.model.Feedback;
import citizenReport.com.citizenReport.model.User;
import citizenReport.com.citizenReport.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public void saveFeedback(String comment, User user) {
        Feedback feedback = new Feedback();
        feedback.setComment(comment);
        feedback.setUser(user);

        feedbackRepository.save(feedback);
    }

    public List<Feedback> getFeedbackByUser(User user) {
        return feedbackRepository.findByUser(user);
    }

    public List<Feedback> getFiveOldestFeedbacks() {
        return feedbackRepository.findTop5ByOrderByCreatedAtAsc();
    }

    public List<Feedback> getAllFeedbackOldestFirst() {
        return feedbackRepository.findAllByOrderByCreatedAtAsc();
    }

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    public void toggleFeatured(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId).orElse(null);
        if (feedback != null) {
            feedback.setFeatured(!feedback.isFeatured());
            feedbackRepository.save(feedback);
        }
    }
    public void deleteFeedback(Long feedbackId) {
        feedbackRepository.deleteById(feedbackId);
    }

    public void deleteAllFeedback() {
        feedbackRepository.deleteAll();
    }

    public List<Feedback> getFeaturedFeedback() {
        return feedbackRepository.findByFeaturedTrue();
    }

}