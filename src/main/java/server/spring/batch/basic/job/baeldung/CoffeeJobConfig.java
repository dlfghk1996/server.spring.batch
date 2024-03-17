package server.spring.batch.basic.job.baeldung;


import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

// CSV 데이터를  데이터베이스에 저장하는 batch 작업
@RequiredArgsConstructor
@Configuration
public class CoffeeJobConfig {
    //  @Value("classpath:csv/coffee-list.csv")
    @Value("${file.input}")
    private String fileInput;

    private final EntityManagerFactory entityManagerFactory;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    // simple Joa 이라는  Job 생성
    @Bean
    public Job simpleJobBuilder() {

        return new JobBuilder("simpleJob", jobRepository)
             // Job 의 중복 실행을 방지
            //.preventRestart()
            // parameter 가 같아도 실행되게끔 설정
            .incrementer(new RunIdIncrementer())
            .listener(new JobCompletionNotificationListener())
            .flow(step())
            .end()
            .build();
    }

    @Bean
    public Step step() {

        return new StepBuilder("step", jobRepository)
            // Chunk(10) 선언(트랜잭션 범위): 한 번에 최대 10개의 레코드,
            // <?,?> (Reader의반환할 타입, Writer에 파라미터로 넘어올 타입)
            .<Coffee, Coffee> chunk(3, transactionManager)
            .reader(reader())
            // 각 커피 항목을 사용자 정의 비즈니스 로직을 적용하는 사용자 정의 프로세서에 전달
            .processor(processor())
            // 데이터베이스에 기록
            .writer(writer())
            .build();
    }

    // Coffee-list.csv 라는 파일을 찾고 각 항목을 Coffee 객체 로 구문 분석한다.
    @Bean
    public FlatFileItemReader reader() {
        return new FlatFileItemReaderBuilder().name("coffeeItemReader")
            .resource(new ClassPathResource(fileInput))
            .delimited()
            .names(new String[] { "brand", "origin", "characteristics" })
            // fieldSetMapper : FieldSet 에서 얻은 데이터를 객체에 매핑하는 데 사용되는 인터페이스.
            .fieldSetMapper(new BeanWrapperFieldSetMapper() {{
                setTargetType(Coffee.class);
            }})
            .build();
    }


    // dataSource 는 Step에서 설정한 chuck단위로 받는다. =>  ItemWriter에서는 한 번에 10개 단위의 레코드를 DB로 커밋하게 된다.
    @Bean
    public JpaItemWriter writer() {
        return new JpaItemWriterBuilder<Coffee>()
            .entityManagerFactory(entityManagerFactory)
            .build();

    }

    @Bean
    public CoffeeItemProcessor processor() {
        return new CoffeeItemProcessor();
    }

}



