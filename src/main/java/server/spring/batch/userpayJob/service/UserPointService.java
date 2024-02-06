package server.spring.batch.userpayJob.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.spring.batch.userpayJob.domain.PointDetail;
import server.spring.batch.userpayJob.domain.UserPoint;
import server.spring.batch.userpayJob.repository.UserPointRepository;


@RequiredArgsConstructor
@Service
public class UserPointService {

    private final UserPointRepository repository;

    public void add(PointDetail pointDetail){
        UserPoint userPoint = repository.findByUserId(pointDetail.getUserId());
        userPoint.setPoint(Math.max((userPoint.getPoint() - pointDetail.getPoint()), 0));
        userPoint.setModifyDate(LocalDateTime.now());
        repository.save(userPoint);
    }
}

