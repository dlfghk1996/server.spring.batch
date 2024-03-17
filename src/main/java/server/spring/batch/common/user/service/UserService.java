package server.spring.batch.common.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.spring.batch.common.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
}
