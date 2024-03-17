package server.spring.batch.common.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.spring.batch.common.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
