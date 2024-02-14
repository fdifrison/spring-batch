package com.example.billingjob;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.repository.JobRepository;

public class BillingJobWithException implements Job {

    private final JobRepository jobRepository;

    public BillingJobWithException(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public String getName() {
        return "BillingJobWithException";
    }

    @Override
    public void execute(JobExecution execution) {
        try {
            throw new Exception("Testing exception during Job Execution");
        } catch (Exception e) {
            execution.addFailureException(e);
            execution.setStatus(BatchStatus.COMPLETED);
            execution.setExitStatus(ExitStatus.FAILED.addExitDescription(e.getMessage()));
        } finally {
            this.jobRepository.update(execution);

        }
    }
}
