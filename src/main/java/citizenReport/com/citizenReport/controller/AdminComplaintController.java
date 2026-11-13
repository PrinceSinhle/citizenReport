package citizenReport.com.citizenReport.controller;

import citizenReport.com.citizenReport.dto.ComplaintResponse;
import citizenReport.com.citizenReport.dto.UpdateComplaintStatusRequest;
import citizenReport.com.citizenReport.model.ComplaintStatus;
import citizenReport.com.citizenReport.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/admin/complaints")
public class AdminComplaintController {
    private final ComplaintService complaints;

    public AdminComplaintController(ComplaintService complaints) {
        this.complaints = complaints;
    }

    @GetMapping
    public List<ComplaintResponse> list(
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(required = false) String issueType,
            @RequestParam(required = false) String location) {
        return complaints.search(status, issueType, location).stream().map(ComplaintResponse::from).toList();
    }

    @GetMapping("/settled")
    public List<ComplaintResponse> settled(
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(required = false) String issueType,
            @RequestParam(required = false) String location) {
        return complaints.searchSettled(status, issueType, location).stream().map(ComplaintResponse::from).toList();
    }

    @PatchMapping("/{id}/status")
    public ComplaintResponse updateStatus(@PathVariable Long id,
            @Valid @RequestBody UpdateComplaintStatusRequest request) {
        return ComplaintResponse.from(complaints.updateStatus(id, request.status()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSettled(@PathVariable Long id) {
        complaints.deleteSettled(id);
    }
}
