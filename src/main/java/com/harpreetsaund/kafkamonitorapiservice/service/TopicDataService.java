package com.harpreetsaund.kafkamonitorapiservice.service;

import com.harpreetsaund.kafkamonitorapiservice.model.TopicDataEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicDataService {

    private static final Logger logger = LoggerFactory.getLogger(TopicDataService.class);

    private final ListOperations<String, TopicDataEntity> topicDataEntityListOperations;

    private final String topicEntityKeyPrefix;

    private final TopicService topicService;

    @Value("${cache.max-records}")
    private Integer maxRecords;

    @Value("${cache.recovery.max-records}")
    private Integer recoveryMaxRecords;

    public TopicDataService(RedisTemplate<String, TopicDataEntity> topicDataEntityRedisTemplate,
            TopicService topicService) {
        this.topicDataEntityListOperations = topicDataEntityRedisTemplate.opsForList();
        this.topicService = topicService;
        this.topicEntityKeyPrefix = "KafkaMonitor:" + TopicDataEntity.class.getSimpleName() + ":";
    }

    public void insert(TopicDataEntity topicDataEntity, Boolean isInRecoveryMode) {
        logger.debug("Inserting topic data entity: {}", topicDataEntity);

        String key = topicEntityKeyPrefix + topicDataEntity.getTopicName();

        topicDataEntityListOperations.rightPush(key, topicDataEntity);
        topicDataEntityListOperations.trim(key, isInRecoveryMode ? -recoveryMaxRecords : -maxRecords, -1);
    }

    public List<TopicDataEntity> get(String topicName) {
        logger.debug("Retrieving topic data entities for topic: {}", topicName);
        return topicDataEntityListOperations.range(topicEntityKeyPrefix + topicName, 0, -1);
    }

    public void delete(String topicName) {
        logger.debug("Deleting topic data entities for topic: {}", topicName);
        topicDataEntityListOperations.getOperations().delete(topicEntityKeyPrefix + topicName);
    }

    @Scheduled(cron = "${cache.recovery.data-reset.cron}")
    public void resetSeekDataRecords() {
        topicService.getAll().forEach(topic -> topicDataEntityListOperations
                .trim(topicEntityKeyPrefix + topic.getTopicName(), -maxRecords, -1));
    }
}
