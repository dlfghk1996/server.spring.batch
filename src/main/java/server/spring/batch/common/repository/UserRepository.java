package server.spring.batch.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.spring.batch.common.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
