package server.spring.batch.userpointJob.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import server.spring.batch.userpointJob.domain.UserPoint;

public interface UserPointRepository extends JpaRepository<UserPoint, Long> {

    UserPoint findByUserId(Long userId);
}
