package server.spring.batch.basic.job.jpa;

import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import server.spring.batch.common.user.domain.User;


// DB Data 읽는 JOB -> JpaPagingItemReader 사용
@RequiredArgsConstructor
@Configuration
public class JpaJobConfig {
    public static final String JOB_NAME = "jpaJob";

    private final EntityManagerFactory entityManagerFactory;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final static int CHUNK_SIZE = 10;


    @Bean
    public Job job() throws Exception {
        return new JobBuilder(JOB_NAME, jobRepository)
            .start(step())
            .build();
    }


    @Bean(name=JOB_NAME+"_step")
    @JobScope
    public Step step() throws Exception {
        return new StepBuilder(JOB_NAME + "_step", jobRepository)
            .<User, User> chunk(CHUNK_SIZE, transactionManager)
            .reader(reader(null, null))
            .writer(writer())
            .build();
    }


    @Bean(name=JOB_NAME+"_reader")
    @StepScope
    public ItemStreamReader<User> reader(
        @Value("#{jobParameters[status]}") String status,
        @Value("#{jobParameters[createDate]}") String createDateStr) throws Exception {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate createDate = LocalDate.parse(createDateStr, formatter); // (3)

        Map<String, Object> params = new HashMap<>();
        params.put("createDate", createDate);
        params.put("status", status);

        JpaPagingItemReader<User> jpaPagingItemReader = new JpaPagingItemReaderBuilder<User>()
            .name(JOB_NAME+"_reader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(CHUNK_SIZE)
            .currentItemCount(0) // 지정값 다음부터 데이터를 조회
            .maxItemCount(100) // 최대로 조회할 데이터 갯수
            .queryString("SELECT p FROM User p WHERE p.createDate =:createDate AND p.status =:status")
            .parameterValues(params)
            .build();

        jpaPagingItemReader.afterPropertiesSet();

        return jpaPagingItemReader;
    }

    @Bean(name=JOB_NAME+"_writer")
    public JpaItemWriter writer() {
        return new JpaItemWriterBuilder<User>()
            .entityManagerFactory(entityManagerFactory)
            .build();
    }
}
