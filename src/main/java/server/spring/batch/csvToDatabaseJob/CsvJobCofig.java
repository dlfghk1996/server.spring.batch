//import org.springframework.batch.core.job.DefaultJobParametersValidator;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.context.annotation.Bean;//package server.spring.batch.csvToDatabaseJob;
//import server.spring.batch.basic.job.JobCompletionNotificationListener;
////
//import jakarta.persistence.EntityManagerFactory;
//import javax.sql.DataSource;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.job.DefaultJobParametersValidator;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.batch.item.database.JpaItemWriter;
//import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
//import org.springframework.batch.item.file.FlatFileItemReader;
//import org.springframework.batch.item.file.LineMapper;
//import org.springframework.batch.item.file.MultiResourceItemReader;
//import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
//import org.springframework.batch.item.file.mapping.DefaultLineMapper;
//import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
//import org.springframework.batch.item.file.transform.LineTokenizer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.PlatformTransactionManager;
//import server.spring.batch.basic.job.Coffee;
//import server.spring.batch.basic.job.JobCompletionNotificationListener;
//import org.springframework.core.io.Resource;
//import server.spring.batch.common.domain.User;
//
///* MultiResourceItemReader 클래스를 사용하여 여러 CSV 파일을 병렬로 읽고 처리
//* MutiResourceItemReader :
//* 여러 파일에 걸쳐 많은 양의 데이터가 분산되어 있고 동시에 처리하여 성능을 향상시키려는 경우에 사용
//* 각 리소스는 별도의 입력으로 처리되며 각 위임 리더로부터 읽은 항목이 집계되어 단일 배치로 처리된다.
//*/
//// 목표: csv 파일을 읽고, DB Insert 및 csv 파일 생성
//@Slf4j
//@RequiredArgsConstructor
//@Configuration
//@ConditionalOnProperty(name = "job.name", havingValue = JOB_NAME)
//public class CsvJobCofig {
//    public static final String JOB_NAME = "insertIntoDbFromCsvJob";
//
//    private final EntityManagerFactory entityManagerFactory;
//    private final JobRepository jobRepository;
//    private final PlatformTransactionManager transactionManager;
//
//    @Value("input/inputData*.csv")
//    private Resource[] inputResources;
//
//    @Bean
//    public Job readCSVFilesJob() {
//        return new JobBuilder(JOB_NAME, jobRepository)
//            .start(step1())
//            .build();
//    }
//
//
//    @Bean
//    public Step step1() {
//        return new StepBuilder("step", jobRepository)
//            .<User, User>chunk(5, transactionManager)
//            .reader(multiResourceItemReader())
//            .writer(writer())
//            .build();
//    }
//
//    @Bean
//    public MultiResourceItemReader<User> multiResourceItemReader()
//    {
//        MultiResourceItemReader<User> resourceItemReader = new MultiResourceItemReader<Employee>();
//        resourceItemReader.setResources(inputResources);
//        resourceItemReader.setDelegate(reader());
//        return resourceItemReader;
//    }
//
//    @Bean
//    public FlatFileItemReader<User> reader()
//    {
//        //Create reader instance
//        FlatFileItemReader<User> reader = new FlatFileItemReader<User>();
//
//        //Set number of lines to skips. Use it if file has header rows.
//        reader.setLinesToSkip(1);
//
//        //Configure how each line will be parsed and mapped to different values
//        reader.setLineMapper(new DefaultLineMapper() {
//            {
//                //3 columns in each row
//                setLineTokenizer(new DelimitedLineTokenizer() {
//                    {
//                        setNames(new String[] { "id", "firstName", "lastName" });
//                    }
//                });
//                //Set values in Employee class
//                setFieldSetMapper(new BeanWrapperFieldSetMapper<User>() {
//                    {
//                        setTargetType(User.class);
//                    }
//                });
//            }
//        });
//        return reader;
//    }
//
//    @Bean
//    public JpaItemWriter writer() {
//        return new JpaItemWriterBuilder<Coffee>()
//            .entityManagerFactory(entityManagerFactory)
//            .build();
//
//    }
//
//    @Bean
//    public ItemWriter<MyObject> loggingItemWriter() {
//        return items -> {
//            for (MyObject item : items) {
//                // Log each item
//                log.info("Writing item: {}", item.toString());
//            }
//        };
//    }
//}
