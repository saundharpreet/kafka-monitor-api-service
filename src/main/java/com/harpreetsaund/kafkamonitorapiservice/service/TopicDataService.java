package com.harpreetsaund.kafkamonitorapiservice.service;

import com.harpreetsaund.kafkamonitorapiservice.model.TopicDataEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicDataService {

    private static final Logger logger = LoggerFactory.getLogger(TopicDataService.class);

    private final ListOperations<String, TopicDataEntity> topicDataEntityListOperations;

    private final String topicEntityKeyPrefix;

    public TopicDataService(RedisTemplate<String, TopicDataEntity> topicDataEntityRedisTemplate) {
        this.topicDataEntityListOperations = topicDataEntityRedisTemplate.opsForList();
        this.topicEntityKeyPrefix = "KafkaMonitor:" + TopicDataEntity.class.getSimpleName() + ":";
    }

    public void insert(TopicDataEntity topicDataEntity) {
        logger.debug("Inserting topic data entity: {}", topicDataEntity);

        String key = topicEntityKeyPrefix + topicDataEntity.getTopicName();

        topicDataEntityListOperations.rightPush(key, topicDataEntity);
        topicDataEntityListOperations.trim(key, -100, -1);
    }

    public List<TopicDataEntity> get(String topicName) {
        logger.debug("Retrieving topic data entities for topic: {}", topicName);
        return topicDataEntityListOperations.range(topicEntityKeyPrefix + topicName, 0, -1);
    }

    public void delete(String topicName) {
        logger.debug("Deleting topic data entities for topic: {}", topicName);
        topicDataEntityListOperations.getOperations().delete(topicEntityKeyPrefix + topicName);
    }
}
