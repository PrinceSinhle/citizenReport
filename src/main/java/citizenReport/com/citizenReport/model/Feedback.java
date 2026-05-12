package citizenReport.com.citizenReport.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;

    private LocalDateTime createdAt;

    private boolean featured = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Feedback() {
        this.createdAt = LocalDateTime.now();
        this.featured = false;
    }

    public Long getId() { return id; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}