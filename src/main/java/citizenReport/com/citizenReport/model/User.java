package citizenReport.com.citizenReport.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "app_user")
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 120)
    @Setter
    private String fullName;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false)
    @Setter
    private String password;

    @Column(length = 30)
    @Setter
    private String phoneNumber;

    @Column(nullable = false, length = 20)
    @Setter
    private String role = "USER";

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim().toLowerCase(java.util.Locale.ROOT);
    }
}
