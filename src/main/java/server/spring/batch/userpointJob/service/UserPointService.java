package server.spring.batch.userpointJob.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.spring.batch.userpointJob.domain.PointDetail;
import server.spring.batch.userpointJob.domain.UserPoint;
import server.spring.batch.userpointJob.repository.UserPointRepository;


@RequiredArgsConstructor
@Service
@Transactional
public class UserPointService {

    private final UserPointRepository repository;

    public void update(PointDetail pointDetail){
        UserPoint userPoint = repository.findByUserId(pointDetail.getUserId());
        userPoint.setPoint(Math.max((userPoint.getPoint() - pointDetail.getPoint()), 0));
        userPoint.setModifyDate(LocalDateTime.now());
       // repository.save(userPoint);
    }
}

