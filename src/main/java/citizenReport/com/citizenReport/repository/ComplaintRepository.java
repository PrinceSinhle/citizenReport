package citizenReport.com.citizenReport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import citizenReport.com.citizenReport.model.User;
import citizenReport.com.citizenReport.model.Complaint;
import java.util.List;


public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
List<Complaint> findByUser(User user);
List<Complaint> findByStatusIn(List<String> statuses);
List<Complaint> findByStatusNotIn(List<String> statuses);
}
