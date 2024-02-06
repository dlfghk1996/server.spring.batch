package server.spring.batch.userpayJob.job;

import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import server.spring.batch.userpayJob.domain.PointDetail;
import server.spring.batch.userpayJob.domain.UserPoint;
import server.spring.batch.userpayJob.service.UserPointService;

@Slf4j
@RequiredArgsConstructor
@Configuration
//@ConditionalOnProperty(name = "job.name", havingValue = UserPointJobConfig.JOB_NAME)
public class UserPointJobConfig {
    public static final String JOB_NAME = "userPointJob";

    private final EntityManagerFactory entityManagerFactory;

    private final TaskExecutor taskExecutor;

    private final PlatformTransactionManager transactionManager;

    private final JobRepository jobRepository;

    private final UserPointService userPointService;
    private static final Integer CHUNK_SIZE = 3;


    @Bean
    public Job job() throws Exception {
        return new JobBuilder(JOB_NAME, jobRepository)
            .start(step())
            .incrementer(new RunIdIncrementer())
            .build();
    }

    @Bean(name=JOB_NAME+"_step")
    @JobScope
    public Step step() throws Exception {

        return new StepBuilder(JOB_NAME + "_step", jobRepository)
            .<PointDetail, PointDetail> chunk(CHUNK_SIZE, transactionManager)

            .reader(reader())
            .processor(processor())
            .writer(compositeDepositItemWriter())
            .faultTolerant()
            .skip(Exception.class)
            // skip 제한을 1000으로 제한
            .skipLimit(1000)
            .taskExecutor(this.taskExecutor) // 동시성
            .listener(new UserPayJobExecutionListener(LocalDateTime.now().toString()))
            .build();

    }

    @Bean(name=JOB_NAME+"_reader")
    @StepScope
    public ItemStreamReader<PointDetail> reader()
        throws Exception {
        String createDateStr = LocalDate.now().toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate createDate = LocalDate.parse(createDateStr, formatter); // (3)

        Map<String, Object> params = new HashMap<>();
        params.put("createDate", createDate);

       // new JpaPagingItemReaderBuilder<MemberPay>().queryString("SELECT u FROM MemberPay u")
        JpaPagingItemReader<PointDetail> jpaPagingItemReader = new JpaPagingItemReaderBuilder<PointDetail>()
            .name(JOB_NAME+"_reader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(CHUNK_SIZE)
           // .currentItemCount(0)
           // .maxItemCount(100)
            .queryString("SELECT p FROM PointDetail p")
           // .parameterValues(params)
            .build();

        // Writer 생성 후 필수 값 누락 확인
        jpaPagingItemReader.afterPropertiesSet();

        return jpaPagingItemReader;
    }

    @Bean
    public CompositeItemWriter compositeDepositItemWriter() {
        List<ItemWriter> delegates = new ArrayList<>();
        delegates.add(pointDetailWriter());
        delegates.add(userPointWriter());

        CompositeItemWriter compositeItemWriter = new CompositeItemWriter();
        compositeItemWriter.setDelegates(delegates);
        return compositeItemWriter;
    }

    public ItemWriter<PointDetail> userPointWriter() {
        return pointDetails -> {
            for (PointDetail pointDetail : pointDetails) {
                userPointService.add(pointDetail);
            }
        };
    }

    public JpaItemWriter pointDetailWriter() {
        return new JpaItemWriterBuilder<PointDetail>()
            .entityManagerFactory(entityManagerFactory)
            .build();
    }

    @Bean
    public UserPointJobCustomItemProcessor processor() {
        return new UserPointJobCustomItemProcessor();
    }

}
