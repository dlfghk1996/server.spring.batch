//package server.spring.batch.basic.job;
//
//
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobExecution;
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.JobParametersBuilder;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.scheduling.TaskScheduler;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//import org.springframework.scheduling.support.ScheduledMethodRunnable;
//import org.springframework.stereotype.Component;
//import java.util.Date;
//import java.util.IdentityHashMap;
//import java.util.Map;
//import java.util.concurrent.ScheduledFuture;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicInteger;
//
//
//
//@Component
//@RequiredArgsConstructor
//public class SpringBatchScheduler {
//
//    private final Logger logger = LoggerFactory.getLogger(SpringBatchScheduler.class);
//
//    private AtomicBoolean enabled = new AtomicBoolean(true);
//
//    private AtomicInteger batchRunCounter = new AtomicInteger(0);
//
//    private final Map<Object, ScheduledFuture<?>> scheduledTasks = new IdentityHashMap<>();
//
//    private final ApplicationContext applicationContext;
//
//    private final JobLauncher jobLauncher;
//
//    @Scheduled(fixedRate = 2000)
//    public void launchJob() throws Exception {
//
//
//        Date date = new Date();
//
//        JobParameters jobParameters = new JobParametersBuilder()
//            .addDate("createDate", date)
//            .toJobParameters();
//
//        Job job = (Job) applicationContext.getBean("userPointJob");
//        logger.debug("scheduler starts at " + date);
//        if (enabled.get()) {
//            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
//            batchRunCounter.incrementAndGet();
//            logger.debug("Batch job ends with status as " + jobExecution.getStatus());
//        }
//        logger.debug("scheduler ends ");
//    }
//
//    public void stop() {
//        enabled.set(false);
//    }
//
//    public void start() {
//        enabled.set(true);
//    }
//
//    @Bean
//    public TaskScheduler poolScheduler() {
//        return new CustomTaskScheduler();
//    }
//
//    private class CustomTaskScheduler extends ThreadPoolTaskScheduler {
//
//        private static final long serialVersionUID = -7142624085505040603L;
//
//        @Override
//        public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long period) {
//            ScheduledFuture<?> future = super.scheduleAtFixedRate(task, period);
//
//            ScheduledMethodRunnable runnable = (ScheduledMethodRunnable) task;
//            scheduledTasks.put(runnable.getTarget(), future);
//
//            return future;
//        }
//
//    }
//
//    public void cancelFutureSchedulerTasks() {
//        scheduledTasks.forEach((k, v) -> {
//            if (k instanceof SpringBatchScheduler) {
//                v.cancel(false);
//            }
//        });
//    }
//
//
//    public AtomicInteger getBatchRunCounter() {
//        return batchRunCounter;
//    }
//}