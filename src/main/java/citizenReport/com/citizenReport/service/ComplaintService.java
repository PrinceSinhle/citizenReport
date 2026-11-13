package citizenReport.com.citizenReport.service;

import citizenReport.com.citizenReport.model.Complaint;
import citizenReport.com.citizenReport.model.ComplaintStatus;
import citizenReport.com.citizenReport.model.User;
import citizenReport.com.citizenReport.repository.ComplaintRepository;
import citizenReport.com.citizenReport.exception.ComplaintNotFoundException;
import citizenReport.com.citizenReport.exception.InvalidComplaintFilterException;
import citizenReport.com.citizenReport.exception.ComplaintNotSettledException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ComplaintService {
    private final ComplaintRepository complaints;

    public ComplaintService(ComplaintRepository complaints) {
        this.complaints = complaints;
    }

    public List<Complaint> findFor(User user) {
        return complaints.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Complaint> findAll() {
        return complaints.findAllByOrderByCreatedAtDesc();
    }

    public List<Complaint> search(ComplaintStatus status, String issueType, String location) {
        return complaints.search(status, cleanFilter(issueType), cleanFilter(location));
    }

    public List<Complaint> searchSettled(ComplaintStatus status, String issueType, String location) {
        if (status != null && status != ComplaintStatus.RESOLVED && status != ComplaintStatus.REJECTED) {
            throw new InvalidComplaintFilterException("Settled complaint status must be RESOLVED or REJECTED.");
        }
        return complaints.searchSettled(List.of(ComplaintStatus.RESOLVED, ComplaintStatus.REJECTED),
                status, cleanFilter(issueType), cleanFilter(location));
    }

    public Complaint findOwned(Long id, User user) {
        return complaints.findByIdAndUser(id, user)
                .orElseThrow(ComplaintNotFoundException::new);
    }

    public Complaint findById(Long id) {
        return complaints.findById(id).orElseThrow(ComplaintNotFoundException::new);
    }

    public Complaint create(String issueType, String description, String location, User user) {
        Complaint complaint = new Complaint();
        complaint.setIssueType(issueType.trim());
        complaint.setDescription(description.trim());
        complaint.setLocation(location.trim());
        complaint.setUser(user);
        return complaints.save(complaint);
    }

    public Complaint updateStatus(Long id, ComplaintStatus status) {
        Complaint complaint = complaints.findById(id)
                .orElseThrow(ComplaintNotFoundException::new);
        complaint.setStatus(status);
        return complaints.save(complaint);
    }

    public void deleteSettled(Long id) {
        Complaint complaint = findById(id);
        if (complaint.getStatus() != ComplaintStatus.RESOLVED
                && complaint.getStatus() != ComplaintStatus.REJECTED) {
            throw new ComplaintNotSettledException();
        }
        complaints.delete(complaint);
    }

    private String cleanFilter(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
