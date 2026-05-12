package citizenReport.com.citizenReport.service;

import citizenReport.com.citizenReport.model.Complaint;
import citizenReport.com.citizenReport.model.User;
import citizenReport.com.citizenReport.repository.ComplaintRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    // Save complaint
    public void saveComplaint(Complaint complaint) {
        complaintRepository.save(complaint);
    }

    // Get complaints for a specific user
    public List<Complaint> getComplaintsByUser(User user) {
        return complaintRepository.findByUser(user);
    }

    // Get all complaints (not used much now but keep it)
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    // Get only ACTIVE complaints (for admin page)
    public List<Complaint> getActiveComplaints() {
        return complaintRepository.findByStatusNotIn(List.of("RESOLVED", "REJECTED"));
    }

    // Get only SETTLED complaints
    public List<Complaint> getSettledComplaints() {
        return complaintRepository.findByStatusIn(List.of("RESOLVED", "REJECTED"));
    }

    // Get complaint by ID
    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id).orElse(null);
    }

    // Update status (SUBMITTED -> IN_PROGRESS -> RESOLVED/REJECTED)
    public void updateComplaintStatus(Long id, String status) {
        Complaint complaint = getComplaintById(id);

        if (complaint != null) {
            complaint.setStatus(status);
            complaintRepository.save(complaint);
        }
    }

    // Delete complaint
    public void deleteComplaint(Long id) {
        complaintRepository.deleteById(id);
    }

    // Restore complaint back to active
    public void restoreComplaint(Long id) {
        Complaint complaint = getComplaintById(id);

        if (complaint != null) {
            complaint.setStatus("SUBMITTED");
            complaintRepository.save(complaint);
        }
    }
}