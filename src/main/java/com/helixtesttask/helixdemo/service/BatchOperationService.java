package com.helixtesttask.helixdemo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchOperationService {

    private final JobLauncher jobLauncher;
    private final Job job;

    public void startJob() {
        try {
            val result = jobLauncher.run(job, new JobParameters());
            log.info("Execution status {} for job id {}", result.getStatus(), result.getJobId());
        } catch (JobExecutionAlreadyRunningException | JobRestartException |
                JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }
}
