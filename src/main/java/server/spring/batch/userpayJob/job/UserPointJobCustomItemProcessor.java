package server.spring.batch.userpayJob.job;

import java.time.LocalDateTime;
import org.springframework.batch.item.ItemProcessor;
import server.spring.batch.userpayJob.domain.PointDetail;
import server.spring.batch.userpayJob.domain.PointLog;
import server.spring.batch.userpayJob.domain.enums.PointLogType;

public class UserPointJobCustomItemProcessor implements ItemProcessor<PointDetail, PointDetail> {

    @Override
    public PointDetail process(PointDetail pointDetail) throws Exception {

        System.out.println("UserPointJobCustomItemProcessor");
        pointDetail.setAvailable(false);

        // 로그 기록.
        PointLog pointLog = new PointLog();
        pointLog.setUserId(pointDetail.getUserId());
        pointLog.setPoint(pointDetail.getPoint());
        pointLog.setType(PointLogType.EXPIRED);
        pointLog.setContent("포인트 기간 만료로 인한 소멸");
        pointLog.setCreateDate(LocalDateTime.now());

        pointDetail.setPointLog(pointLog);

        return pointDetail;
    }
}
