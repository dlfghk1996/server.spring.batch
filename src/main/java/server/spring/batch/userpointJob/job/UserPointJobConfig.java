package server.spring.batch.userpointJob.job;

import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
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
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import server.spring.batch.userpointJob.domain.PointDetail;
import server.spring.batch.userpointJob.service.UserPointService;

@Slf4j
@RequiredArgsConstructor
@Configuration
// @ConditionalOnProperty : application.yml 에 정의 되어있는 속성의 존재와 값에 따라 조건부 빈 생성
public class UserPointJobConfig {
    public static final String JOB_NAME = "userPointJob";

    private final EntityManagerFactory entityManagerFactory;

    private final TaskExecutor taskExecutor;

    private final PlatformTransactionManager transactionManager;

    private final JobRepository jobRepository;

    private final UserPointService userPointService;
    private static final Integer CHUNK_SIZE = 3;

  //  private final CreateDateJobParameter jobParameter;


//    @Bean(JOB_NAME + "job_Parameter")
//    @JobScope // JobParameter의 @Value로 값을 받기 위해 @JobScope, @StepScope 필수
//    public CreateDateJobParameter jobParameter(){
//        return new CreateDateJobParameter();
//    }


    @Bean(name=JOB_NAME)
    public Job job() throws Exception {
        return new JobBuilder(JOB_NAME, jobRepository)
            .start(step())
            .preventRestart() // 같은 파라미터 재시작 막음
           // .incrementer(new RunIdIncrementer()) // 동일 파라미터 재실행
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
           // .faultTolerant() // 장애처리 기능 활성화 (Skip, Retry)
           // .skip(Exception.class)
            // skip 제한을 1000으로 제한
           // .skipLimit(1000)
           // .taskExecutor(this.taskExecutor) // 동시성
            .listener(new UserPayJobExecutionListener(LocalDateTime.now().toString()))
            .build();
    }

    @Bean(name=JOB_NAME+"_reader")
    @StepScope
    public ItemStreamReader<PointDetail> reader() throws Exception {
        Map<String, Object> params = new HashMap<>();
        //params.put("createDate", jobParameter.getCreateDate());
        //params.put("status", jobParameter.getUserStatus());
        //log.info(">>>>>>>>>>> createDate={}, status={}", jobParameter.getCreateDate(), jobParameter.getUserStatus());


        JpaPagingItemReader<PointDetail> jpaPagingItemReader = new JpaPagingItemReaderBuilder<PointDetail>()
            .name(JOB_NAME+"_reader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(CHUNK_SIZE)
            .queryString("SELECT p FROM PointDetail p")
            //.queryString("SELECT p FROM PointDetail p WHERE p.createDate <:createDate")
            //.parameterValues(params)
            .build();

        // Writer 생성 후 필수 값 누락 확인
        jpaPagingItemReader.afterPropertiesSet();

        return jpaPagingItemReader;
    }

    @Bean(name=JOB_NAME+"writer")
    public CompositeItemWriter compositeDepositItemWriter() {
        List<ItemWriter> delegates = new ArrayList<>();
        delegates.add(pointDetailWriter());
        delegates.add(userPointWriter());

        CompositeItemWriter compositeItemWriter = new CompositeItemWriter();
        compositeItemWriter.setDelegates(delegates);
        return compositeItemWriter;
    }

    public JpaItemWriter pointDetailWriter() {
        return new JpaItemWriterBuilder<PointDetail>()
            .entityManagerFactory(entityManagerFactory)
            .build();
    }


    public ItemWriter<PointDetail> userPointWriter() {
        return pointDetails -> {
            for (PointDetail pointDetail : pointDetails) {
                userPointService.update(pointDetail);
            }
        };
    }

    @Bean(name=JOB_NAME+"processor")
    public UserPointJobCustomItemProcessor processor() {
        return new UserPointJobCustomItemProcessor();
    }

}


// https://jojoldu.tistory.com/337 - paging 문제