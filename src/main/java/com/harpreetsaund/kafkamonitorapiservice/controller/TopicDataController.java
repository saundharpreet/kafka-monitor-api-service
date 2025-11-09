package com.harpreetsaund.kafkamonitorapiservice.controller;

import com.harpreetsaund.kafkamonitorapiservice.model.TopicDataEntity;
import com.harpreetsaund.kafkamonitorapiservice.service.TopicDataService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Topic Controller", description = "APIs for managing Kafka topic consumers")
public class TopicDataController {

    private final Logger logger = LoggerFactory.getLogger(TopicDataController.class);

    private final TopicDataService topicDataService;

    public TopicDataController(TopicDataService topicDataService) {
        this.topicDataService = topicDataService;
    }

    @GetMapping("/v1/topic/data/{topicName}")
    public ResponseEntity<List<TopicDataEntity>> getData(@PathVariable String topicName) {
        logger.info("Received request to get all topics");
        return ResponseEntity.ok(topicDataService.get(topicName));
    }
}
