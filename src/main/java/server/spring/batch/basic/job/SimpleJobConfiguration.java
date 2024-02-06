//package server.spring.batch.basic.job;
//
//
//import jakarta.persistence.EntityManagerFactory;
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.batch.item.database.JpaItemWriter;
//import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
//import org.springframework.batch.item.file.FlatFileItemReader;
//import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
//import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.transaction.PlatformTransactionManager;
//
//// CSV 파일에서 커피 목록을 가져와서 사용자 정의 프로세서를 사용하여
//// 변환하고 최종 결과를 메모리 내 데이터베이스에 저장하는 작업을 구축하겠습니다 .
//@RequiredArgsConstructor //=> 생성자 DI를 위한 lombok 어노테이션
//@Configuration
//public class SimpleJobConfiguration {
//    @Value("${file.input}") //  @Value("classpath:csv/inputData.csv")
//    private String fileInput;
//
//    private final EntityManagerFactory entityManagerFactory;
//    private final JobRepository jobRepository;
//    private final PlatformTransactionManager transactionManager;
//
//    // simple Joa 이라는  Job 생성
//    @Bean
//    public Job simpleJobBuilder() {
//
//        return new JobBuilder("simpleJob", jobRepository)
//             // Job의 중복 실행을 방지합니다.
//            //.preventRestart()
//            // parameter 가 같아도 실행되게끔 설정
//            .incrementer(new RunIdIncrementer())
//            .listener(new JobCompletionNotificationListener())
//            .flow(step())
//            .end()
//            .build();
//    }
//
//// @Value("#{jobParameters[requestDate]}") String requestDate
//    @Bean
//    public Step step() {
//
//        return new StepBuilder("step", jobRepository)
//            // Chunk(10) 선언(트랜잭션 범위): 한 번에 최대 10개의 레코드,
//            // <?,?> (Reader의반환할 타입, Writer에 파라미터로 넘어올 타입)
//            .<Coffee, Coffee> chunk(3, transactionManager)
//            // 리더 메소드를 사용하여 설정한 리더 빈을 사용하여 커피 데이터를 읽습니다 .
//            .reader(reader())
//            // 각 커피 항목을 사용자 정의 비즈니스 로직을 적용하는 사용자 정의 프로세서에 전달합니다.
//            .processor(processor())
//            // 이전에 본 기록기를 사용하여 각 커피 항목을 데이터베이스에 기록합니다.
//            .writer(writer())
////          .throttleLimit(8)
////          .listener(new MemberPayAggregateJobExecutionListener(memberPayService, createDate))
////           .faultTolerant()
////           .skip(Exception.class)
////           // skip 제한을 1000으로 제한
////           .skipLimit(1000)
//            .build();
//    }
//
//
//
//    // 간단히 말해서, 위에 정의된 리더 빈은 Coffee-list.csv  라는 파일을 찾고
//    // 각 항목을 Coffee 객체 로 구문 분석합니다 .
//    @Bean
//    public FlatFileItemReader reader() {
//        return new FlatFileItemReaderBuilder().name("coffeeItemReader")
//            .resource(new ClassPathResource(fileInput))
//            .delimited()
//            .names(new String[] { "brand", "origin", "characteristics" })
//            // fieldSetMapper : FieldSet에서 얻은 데이터를 객체에 매핑하는 데 사용되는 인터페이스입니다 .
//            .fieldSetMapper(new BeanWrapperFieldSetMapper() {{
//                setTargetType(Coffee.class);
//            }})
//            .build();
//    }
//
//
//    // 이번에는 Coffee 개체의 Java Bean 속성에 따라 단일 커피 항목을 데이터베이스에 삽입하는 데 필요한 SQL 문을 포함하겠습니다.
//    // 마찬가지로 Writer Bean을 정의합니다.
//    // dataSource 는 Step에서 설정한 chuck단위로 받는다. =>  ItemWriter에서는 한 번에 10개 단위의 레코드를 DB로 커밋하게 된다.
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
//
//
//
