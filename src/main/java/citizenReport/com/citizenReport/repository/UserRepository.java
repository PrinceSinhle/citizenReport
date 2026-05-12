package citizenReport.com.citizenReport.repository;

import citizenReport.com.citizenReport.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    

User findByEmail(String email);

}