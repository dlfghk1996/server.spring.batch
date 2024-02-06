package server.spring.batch.userpayJob.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import server.spring.batch.userpayJob.domain.UserPoint;

public interface UserPointRepository extends JpaRepository<UserPoint, Long> {

    UserPoint findByUserId(Long userId);
}
