package citizenReport.com.citizenReport.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String issueType;
    private String description;
    private String location;
    private String status;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Complaint() {
        this.createdAt = LocalDateTime.now();
        this.status = "SUBMITTED";
    }

    public Long getId() { return id; }

    public String getIssueType() { return issueType; }
    public void setIssueType(String issueType) { this.issueType = issueType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
public void setStatus(String status) {
    this.status = status;
}


    public LocalDateTime getCreatedAt() { return createdAt; }


    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}