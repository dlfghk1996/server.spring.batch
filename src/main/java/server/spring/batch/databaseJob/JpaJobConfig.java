//package server.spring.batch.databaseJob;
//
//import jakarta.persistence.EntityManagerFactory;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import javax.sql.DataSource;
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.batch.item.ItemStreamReader;
//import org.springframework.batch.item.database.JpaItemWriter;
//import org.springframework.batch.item.database.JpaPagingItemReader;
//import org.springframework.batch.item.database.Order;
//import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
//import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.PlatformTransactionManager;
//import server.spring.batch.basic.Coffee;
//import server.spring.batch.basic.CoffeeItemProcessor;
//
//
//// 수백만의 데이터에서 조건에 맞는 데이터를 추출하여 가공하는 Spring Batch를 구현해야했습니다.
//@RequiredArgsConstructor //=> 생성자 DI를 위한 lombok 어노테이션
//@Configuration
//public class JpaJobConfig {
//    public static final String JOB_NAME = "memberPayAggregateJob";
//    public static final String STEP_NAME = "memberPayAggregateStep";
//    public static final String READER_NAME = "memberPayAggregateReader";
//
//    private final EntityManagerFactory entityManagerFactory;
//    private final JobRepository jobRepository;
//    private final PlatformTransactionManager transactionManager;
//
//    private final static int CHUNK_SIZE = 10;
//
//    // simple Joa 이라는  Job 생성
//    @Bean
//    public Job job() {
//
//        return new JobBuilder(JOB_NAME, jobRepository)
//            .start(step())
//            .build();
//    }
//
//    @Bean
//    public Step step() {
//        return new StepBuilder(STEP_NAME, jobRepository)
//            .<ShopOrder, OrderHistory> chunk(3, transactionManager)
//            .reader(reader())
//            .processor(processor())
//            .writer(writer())
//            .build();
//    }
//
//    @Bean
//    public ItemStreamReader<Coffee> reader() throws Exception {
//        HashMap<String, Order> sortKey = new HashMap<>();
//        sortKey.put("first_name", Order.ASCENDING);
//
//        JpaPagingItemReader<Coffee> jpaPagingItemReader = new JpaPagingItemReader<>();
//
//        jpaPagingItemReader.setName(READER_NAME);
//        jpaPagingItemReader.setQueryString("select o from ShopOrder o join fetch o.customer c where c.id=1");
//        jpaPagingItemReader.setEntityManagerFactory(entityManagerFactory);
//        jpaPagingItemReader.setPageSize(CHUNK_SIZE);
//        jpaPagingItemReader.setCurrentItemCount(0);
//        jpaPagingItemReader.setMaxItemCount(100);
//        jpaPagingItemReader.afterPropertiesSet();
//
//        return jpaPagingItemReader;
//    }
//
//
//    //  void write(List<? extends T> var1) throws Exception;
//    @Bean
//    public JpaItemWriter writer() {
//        return new JpaItemWriterBuilder<Coffee>()
//            .entityManagerFactory(entityManagerFactory)
//            .build();
//
//    }
//
//    @Bean
//    public CoffeeItemProcessor processor() {
//        return new CoffeeItemProcessor();
//    }
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
