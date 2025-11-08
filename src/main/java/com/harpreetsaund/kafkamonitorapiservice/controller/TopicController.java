package com.harpreetsaund.kafkamonitorapiservice.controller;

import com.harpreetsaund.kafkamonitorapiclient.request.KafkaMonitorRequest;
import com.harpreetsaund.kafkamonitorapiservice.mapper.TopicMapper;
import com.harpreetsaund.kafkamonitorapiservice.service.TopicConsumerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Topic Controller", description = "APIs for managing Kafka topic consumers")
public class TopicController {

    private final Logger logger = LoggerFactory.getLogger(TopicController.class);

    private final TopicConsumerService topicConsumerService;

    private final TopicMapper topicMapper;

    public TopicController(TopicConsumerService topicConsumerService, TopicMapper topicMapper) {
        this.topicConsumerService = topicConsumerService;
        this.topicMapper = topicMapper;
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
