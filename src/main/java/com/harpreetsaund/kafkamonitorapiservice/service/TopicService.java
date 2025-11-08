package com.harpreetsaund.kafkamonitorapiservice.service;

import com.harpreetsaund.kafkamonitorapiservice.model.TopicEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TopicService {

    private static final Logger logger = LoggerFactory.getLogger(TopicService.class);

    private final ValueOperations<String, TopicEntity> topicEntityValueOperations;

    private final String topicEntityKeyPrefix;

    public TopicService(RedisTemplate<String, TopicEntity> topicEntityRedisTemplate) {
        this.topicEntityValueOperations = topicEntityRedisTemplate.opsForValue();
        this.topicEntityKeyPrefix = "KafkaMonitor:" + TopicEntity.class.getSimpleName() + ":";
    }

    public void saveOrUpdate(TopicEntity topicEntity) {
        logger.debug("Saving topic entity: {}", topicEntity);
        topicEntityValueOperations.set(topicEntityKeyPrefix + topicEntity.getTopicName(), topicEntity);
    }

    public TopicEntity get(String topicName) {
        logger.debug("Retrieving topic entity: {}", topicName);
        return topicEntityValueOperations.get(topicEntityKeyPrefix + topicName);
    }

    public List<TopicEntity> getAll() {
        logger.debug("Retrieving all topic entities");
        return Objects.requireNonNull(topicEntityValueOperations.getOperations().keys(topicEntityKeyPrefix + "*"))
                .stream().map(topicEntityValueOperations::get).toList();
    }

    public void delete(String topicName) {
        logger.debug("Deleting topic entity: {}", topicName);
        topicEntityValueOperations.getOperations().delete(topicEntityKeyPrefix + topicName);
    }
}
