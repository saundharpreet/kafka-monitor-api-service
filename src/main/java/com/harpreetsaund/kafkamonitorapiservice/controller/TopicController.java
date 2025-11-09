package com.harpreetsaund.kafkamonitorapiservice.controller;

import com.harpreetsaund.kafkamonitorapiclient.request.KafkaMonitorRequest;
import com.harpreetsaund.kafkamonitorapiservice.mapper.TopicMapper;
import com.harpreetsaund.kafkamonitorapiservice.model.TopicEntity;
import com.harpreetsaund.kafkamonitorapiservice.service.TopicConsumerService;
import com.harpreetsaund.kafkamonitorapiservice.service.TopicService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Topic Controller", description = "APIs for managing Kafka topic consumers")
public class TopicController {

    private final Logger logger = LoggerFactory.getLogger(TopicController.class);

    private final TopicConsumerService topicConsumerService;

    private final TopicService topicService;

    private final TopicMapper topicMapper;

    public TopicController(TopicConsumerService topicConsumerService, TopicService topicService,
            TopicMapper topicMapper) {
        this.topicConsumerService = topicConsumerService;
        this.topicService = topicService;
        this.topicMapper = topicMapper;
    }

    @GetMapping("/v1/topics")
    public ResponseEntity<List<TopicEntity>> getAllTopics() {
        logger.info("Received request to get all topics");
        return ResponseEntity.ok(topicService.getAll());
    }

    @PostMapping("/v1/create")
    public ResponseEntity<Void> createTopicConsumer(@RequestBody KafkaMonitorRequest.Version1 kafkaMonitorRequest) {
        logger.info("Received request to create topic consumer");
        topicConsumerService.createAndStart(topicMapper.toTopicEntity(kafkaMonitorRequest));

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/v1/delete")
    public ResponseEntity<Void> deleteTopicConsumer(@RequestBody KafkaMonitorRequest.Version1 kafkaMonitorRequest) {
        logger.info("Received request to delete topic consumer");
        topicConsumerService.stopAndRemove(topicMapper.toTopicEntity(kafkaMonitorRequest));

        return ResponseEntity.ok().build();
    }
}
