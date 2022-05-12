package com.helixtesttask.helixdemo.controller;

import com.helixtesttask.helixdemo.service.BatchOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.helixtesttask.helixdemo.controller.BatchProcessingController.BATCH_OPERATIONS_API_URL;

@Slf4j
@RestController
@RequestMapping(path = BATCH_OPERATIONS_API_URL)
@RequiredArgsConstructor
@Validated
public class BatchProcessingController {
    public static final String BATCH_OPERATIONS_API_URL = "batch/operations";

    private final BatchOperationService batchOperationService;

    @PostMapping
    public void startJobExecution() {
        log.info("Start batch job execution...");
        batchOperationService.startJob();
    }
}
