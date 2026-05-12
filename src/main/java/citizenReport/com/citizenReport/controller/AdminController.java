package citizenReport.com.citizenReport.controller;

import citizenReport.com.citizenReport.model.Complaint;
import citizenReport.com.citizenReport.model.User;
import citizenReport.com.citizenReport.repository.UserRepository;
import citizenReport.com.citizenReport.service.ComplaintService;
import citizenReport.com.citizenReport.service.FeedbackService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    private final ComplaintService complaintService;
    private final FeedbackService feedbackService;
    private final UserRepository userRepository;

    public AdminController(ComplaintService complaintService,
                           FeedbackService feedbackService,
                           UserRepository userRepository) {
        this.complaintService = complaintService;
        this.feedbackService = feedbackService;
        this.userRepository = userRepository;
    }

    @GetMapping("/admin")
    public String adminDashboard(Authentication authentication, Model model) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(authentication.getName());
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }

        List<Complaint> complaints = complaintService.getActiveComplaints();
        model.addAttribute("complaints", complaints);
        model.addAttribute("feedbacks", feedbackService.getAllFeedback());

        return "admin";
    }

    @PostMapping("/admin/update-status")
    public String updateStatus(
            @RequestParam Long complaintId,
            @RequestParam String status,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(authentication.getName());
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }

        complaintService.updateComplaintStatus(complaintId, status);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Complaint status updated successfully."
        );

        return "redirect:/admin";
    }

    @GetMapping("/admin/settled")
    public String settledComplaints(Authentication authentication, Model model) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(authentication.getName());
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("complaints", complaintService.getSettledComplaints());

        return "settled_Complaints";
    }

    @PostMapping("/admin/delete")
    public String deleteComplaint(
            @RequestParam Long complaintId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(authentication.getName());
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }

        complaintService.deleteComplaint(complaintId);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Complaint deleted successfully."
        );

        return "redirect:/admin/settled";
    }

    @PostMapping("/admin/restore")
    public String restoreComplaint(
            @RequestParam Long complaintId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(authentication.getName());
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }

        complaintService.restoreComplaint(complaintId);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Complaint restored successfully."
        );

        return "redirect:/admin";
    }

    @PostMapping("/admin/toggle-featured")
    public String toggleFeatured(
            @RequestParam Long feedbackId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(authentication.getName());
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }

        feedbackService.toggleFeatured(feedbackId);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Comment status updated successfully."
        );

        return "redirect:/admin";
    }

    @PostMapping("/admin/delete-feedback")
    public String deleteFeedback(
            @RequestParam Long feedbackId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(authentication.getName());
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }

        feedbackService.deleteFeedback(feedbackId);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Comment deleted successfully."
        );

        return "redirect:/admin";
    }

    @PostMapping("/admin/delete-all-feedback")
    public String deleteAllFeedback(
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(authentication.getName());
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }

        feedbackService.deleteAllFeedback();

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "All comments deleted successfully."
        );

        return "redirect:/admin";
    }

    @GetMapping("/admin/analytics")
    public String analyticsPage(Authentication authentication, Model model) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(authentication.getName());
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }

        List<Complaint> allComplaints = complaintService.getAllComplaints();

        // Total complaints by status
        Map<String, Long> statusCounts = allComplaints.stream()
                .collect(Collectors.groupingBy(Complaint::getStatus, Collectors.counting()));

        // Complaint counts by issue type
        Map<String, Long> issueCounts = allComplaints.stream()
                .collect(Collectors.groupingBy(Complaint::getIssueType, Collectors.counting()));

        // Most reported locations
        Map<String, Long> locationCounts = allComplaints.stream()
                .collect(Collectors.groupingBy(Complaint::getLocation, Collectors.counting()));
        List<Map.Entry<String, Long>> topLocations = locationCounts.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .collect(Collectors.toList());

        // Resolution rate
        long resolvedCount = allComplaints.stream()
                .filter(c -> "RESOLVED".equals(c.getStatus()))
                .count();
        double resolutionRate = allComplaints.isEmpty() ? 0 : (resolvedCount * 100.0) / allComplaints.size();

        model.addAttribute("totalComplaints", allComplaints.size());
        model.addAttribute("statusCounts", statusCounts);
        model.addAttribute("issueCounts", issueCounts);
        model.addAttribute("topLocations", topLocations);
        model.addAttribute("resolutionRate", String.format("%.1f", resolutionRate));

        return "analytics";
    }

    @GetMapping("/admin/report/complaints")
    public void generateComplaintsReport(
            Authentication authentication,
            HttpServletResponse response) throws IOException {

        if (authentication == null || !authentication.isAuthenticated()) {
            response.sendRedirect("/login");
            return;
        }

        User user = userRepository.findByEmail(authentication.getName());
        if (user == null || !"ADMIN".equals(user.getRole())) {
            response.sendRedirect("/login");
            return;
        }

        List<Complaint> complaints = complaintService.getAllComplaints();

        response.setContentType("text/plain; charset=UTF-8");
        response.setHeader("Content-Disposition", 
                "attachment; filename=\"complaints_report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt\"");

        StringBuilder report = new StringBuilder();
        report.append("CITIZEN REPORT - COMPLAINTS REPORT\n");
        report.append("Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss"))).append("\n");
        report.append("=====================================\n\n");

        if (complaints.isEmpty()) {
            report.append("No complaints found.\n");
        } else {
            report.append("Total Complaints: ").append(complaints.size()).append("\n\n");

            for (Complaint complaint : complaints) {
                report.append("-------------------------------------------\n");
                report.append("ID: ").append(complaint.getId()).append("\n");
                report.append("User: ").append(complaint.getUser().getFullName()).append("\n");
                report.append("Email: ").append(complaint.getUser().getEmail()).append("\n");
                report.append("Issue Type: ").append(complaint.getIssueType()).append("\n");
                report.append("Description: ").append(complaint.getDescription()).append("\n");
                report.append("Location: ").append(complaint.getLocation()).append("\n");
                report.append("Status: ").append(complaint.getStatus()).append("\n");
                report.append("Submitted: ").append(complaint.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss"))).append("\n");
                report.append("-------------------------------------------\n\n");
            }
        }

        response.getWriter().write(report.toString());
    }
}
