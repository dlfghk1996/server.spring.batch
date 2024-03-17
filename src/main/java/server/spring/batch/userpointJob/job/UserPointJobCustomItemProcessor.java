package server.spring.batch.userpointJob.job;

import java.time.LocalDateTime;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import server.spring.batch.userpointJob.domain.PointDetail;
import server.spring.batch.userpointJob.domain.PointLog;
import server.spring.batch.userpointJob.domain.enums.PointLogType;

public class UserPointJobCustomItemProcessor implements ItemProcessor<PointDetail, PointDetail> {

    @Override
    public PointDetail process(PointDetail pointDetail) throws Exception {

        System.out.println("UserPointJobCustomItemProcessor");
        pointDetail.setAvailable(false);
        System.out.println(pointDetail.getUserId());
        // 로그 기록.
        PointLog pointLog = new PointLog();
        pointLog.setUserId(pointDetail.getUserId());
        pointLog.setPoint(pointDetail.getPoint());
        pointLog.setType(PointLogType.EXPIRED);
        pointLog.setContent("포인트 기간 만료로 인한 소멸");
        pointLog.setCreateDate(LocalDateTime.now());

        pointDetail.setPointLog(pointLog);
        pointDetail.setAvailable(false);
        return pointDetail;
    }
}
