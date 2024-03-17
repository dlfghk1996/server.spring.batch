package server.spring.batch.userpointJob.job;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
public class UserPayJobExecutionListener implements JobExecutionListener {
    private final String date;

    public UserPayJobExecutionListener(String date) {
        this.date = date;
    }

    @Override
    //job 시작 전 이벤트
    public void beforeJob(JobExecution jobExecution) {
        log.info("Start jobName ==> [{}]", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!! JOB FINISHED! Time to verify the results");
        }
        Duration duration = Duration.between(jobExecution.getEndTime(), jobExecution.getStartTime());

        log.info("회원 포인트 집계 배치 실행, 실행 Parameter : {}", date);
        log.info("--------------------------");
        log.info("처리 시간 {}millis", duration.toMinutes());
    }
}
