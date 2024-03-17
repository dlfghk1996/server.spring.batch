package server.spring.batch.job;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import server.spring.batch.common.user.domain.eums.UserStatus;
import server.spring.batch.userpointJob.job.UserPointJobConfig;

/** 통합 테스트 */
@ComponentScan(
    basePackages = "server.spring.batch"
//    useDefaultFilters = false,
//    includeFilters = {
//        @Filter(
//            type = FilterType.REGEX,
//            pattern = {".*point.*"} // 패키지와 객체 명에 point 라는 글자가 포함되어있는 것을 가져온다.
//        )
//    }
)
@SpringBootTest(classes = {UserPointJobConfig.class})
@ActiveProfiles(value = "local")
@SpringJUnitConfig
@EnableBatchProcessing
@EnableAutoConfiguration
@SpringBatchTest
public class BatchTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void 현재시간을_기준으로_유효기간이_만료된_포인트_처리() throws Exception {
        Job job = (Job) applicationContext.getBean("userPointJob");
        jobLauncherTestUtils.setJob(job);
        LocalDate createDate = LocalDate.of(2024,3,22);

        JobParameters jobParameters = new JobParametersBuilder()
            .addString("createDate", createDate.toString())
         //    .addString("userStatus", UserStatus.ACTIVATED.getCode())
         //    .addString("uniqueParam", LocalDateTime.now().toString())
            .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        //then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }
}
