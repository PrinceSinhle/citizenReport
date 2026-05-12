package citizenReport.com.citizenReport.repository;

import citizenReport.com.citizenReport.model.Feedback;
import citizenReport.com.citizenReport.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByUser(User user);
    List<Feedback> findTop5ByOrderByCreatedAtAsc();
    List<Feedback> findAllByOrderByCreatedAtAsc();
    List<Feedback> findByFeaturedTrue();
}