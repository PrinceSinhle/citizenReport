package citizenReport.com.citizenReport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import citizenReport.com.citizenReport.model.Complaint;
import citizenReport.com.citizenReport.model.User;
import citizenReport.com.citizenReport.model.ComplaintStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;


public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByUserOrderByCreatedAtDesc(User user);
    List<Complaint> findAllByOrderByCreatedAtDesc();
    java.util.Optional<Complaint> findByIdAndUser(Long id, User user);
    @Query("""
            select c from Complaint c
            where (:status is null or c.status = :status)
              and (:issueType is null or lower(c.issueType) like lower(concat('%', :issueType, '%')))
              and (:location is null or lower(c.location) like lower(concat('%', :location, '%')))
            order by c.createdAt desc
            """)
    List<Complaint> search(@Param("status") ComplaintStatus status,
                           @Param("issueType") String issueType,
                           @Param("location") String location);

    @Query("""
            select c from Complaint c
            where c.status in :settledStatuses
              and (:status is null or c.status = :status)
              and (:issueType is null or lower(c.issueType) like lower(concat('%', :issueType, '%')))
              and (:location is null or lower(c.location) like lower(concat('%', :location, '%')))
            order by c.createdAt desc
            """)
    List<Complaint> searchSettled(@Param("settledStatuses") List<ComplaintStatus> settledStatuses,
                                  @Param("status") ComplaintStatus status,
                                  @Param("issueType") String issueType,
                                  @Param("location") String location);

    @Modifying
    @Query("delete from Complaint c where c.user = :user")
    int deleteAllByUser(@Param("user") User user);
}
