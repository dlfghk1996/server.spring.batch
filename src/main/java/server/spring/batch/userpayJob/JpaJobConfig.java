//package server.spring.batch.databaseJob;
//
//import jakarta.persistence.EntityManagerFactory;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.HashMap;
//import java.util.Map;
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.JobScope;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.batch.item.ItemStreamReader;
//import org.springframework.batch.item.database.JpaItemWriter;
//import org.springframework.batch.item.database.JpaPagingItemReader;
//import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
//import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.PlatformTransactionManager;
//import server.spring.batch.common.domain.User;
//
//
//// 수백만의 데이터에서 조건에 맞는 데이터를 추출하여 가공하는 Spring Batch를 구현해야했습니다.
//@RequiredArgsConstructor
//@Configuration
//@ConditionalOnProperty(name="job.name", havingValue = JpaJobConfig.JOB_NAME)
//public class JpaJobConfig {
//    public static final String JOB_NAME = "jpaJob";
//
//    private final EntityManagerFactory entityManagerFactory;
//    private final JobRepository jobRepository;
//    private final PlatformTransactionManager transactionManager;
//    private final static int CHUNK_SIZE = 10;
//
//
//    @Bean
//    public Job job() throws Exception {
//        return new JobBuilder(JOB_NAME, jobRepository)
//            .start(step())
//            .build();
//    }
//
//
//    @Bean(name=JOB_NAME+"_step")
//    @JobScope
//    public Step step() throws Exception {
//        return new StepBuilder(JOB_NAME + "_step", jobRepository)
//            .<User, User> chunk(CHUNK_SIZE, transactionManager)
//            .reader(reader(null, null))
//            .writer(writer())
//            .build();
//    }
//
//
//    @Bean(name=JOB_NAME+"_reader")
//    @StepScope
//    public ItemStreamReader<User> reader(
//        @Value("#{jobParameters[status]}") String status,
//        @Value("#{jobParameters[createDate]}") String createDateStr) throws Exception {
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDate createDate = LocalDate.parse(createDateStr, formatter); // (3)
//
//        Map<String, Object> params = new HashMap<>();
//        params.put("createDate", createDate);
//        params.put("status", status);
//
//        JpaPagingItemReader<User> jpaPagingItemReader = new JpaPagingItemReaderBuilder<User>()
//            .name(JOB_NAME+"_reader")
//            .entityManagerFactory(entityManagerFactory)
//            .pageSize(CHUNK_SIZE)
//            .currentItemCount(0)
//            .maxItemCount(100)
//            .queryString("SELECT p FROM Product p WHERE p.createDate =:createDate AND p.status =:status")
//            .parameterValues(params)
//            .build();
//
//        jpaPagingItemReader.afterPropertiesSet();
//
//        return jpaPagingItemReader;
//    }
//
//
//    @Bean
//    public JpaItemWriter writer() {
//        return new JpaItemWriterBuilder<User>()
//            .entityManagerFactory(entityManagerFactory)
//            .build();
//    }
//
//
//}
//// @Bean : 싱글턴 방식 => 최초 한 번만 메모리에 적재 이후로도 하나의 인스턴스로 계속 사용
//// @StepScope: Step 주기에 따라 새로운 빈을 생성 =>  즉 Step의 실행마다 새로운 빈을 만들기 때문에 지연 생성이 가능하다.
////           : 기본 프록시 모드가 반환되는 클래스 타입을 참조하기 때문에 @StepScope를 사용하면 반드시 구현된 반환 타입을 명시해 반환해야 한다.
//
//// ex.
//// 위 예제에서는 QueueItemReader라고 명시했습니다.
//// findByUpdatedDateBeforeAndStatusEquals() 메서드로 현재 날짜 기준 1년 전의 날짜값과 User의 상태값이 ACTIVE인 User 리스트를 조회하고,
//// QueueItemReader 객체 생성 시 파라미터로 넣어서 Queue에 담도록 하고 있습니다.
//
//
//// https://www.baeldung.com/spring-batch-start-stop-job 예약된 Spring 배치 작업을 트리거하고 중지하는 방법
