package com.helixtesttask.helixdemo.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobCompletionListener extends JobExecutionListenerSupport {
    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Batch job (id={}) has been finished", jobExecution.getJobId());
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Batch job (id={}) has been started", jobExecution.getJobId());
    }
}
