package com.harpreetsaund.kafkamonitorapiservice.service;

import com.harpreetsaund.kafkamonitorapiservice.listener.TopicMessageListener;
import com.harpreetsaund.kafkamonitorapiservice.model.TopicEntity;
import com.harpreetsaund.kafkamonitorapiservice.model.constants.ConsumerState;
import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.util.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

@Service
public class TopicConsumerService implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(TopicConsumerService.class);

    private final ConsumerFactory<String, GenericRecord> consumerFactory;

    private final TopicService topicService;

    private final TopicDataService topicDataService;

    private final Map<String, ConcurrentMessageListenerContainer<String, GenericRecord>> listenerContainerMap;

    public TopicConsumerService(ConsumerFactory<String, GenericRecord> consumerFactory, TopicService topicService,
            TopicDataService topicDataService) {
        this.consumerFactory = consumerFactory;
        this.topicService = topicService;
        this.topicDataService = topicDataService;
        this.listenerContainerMap = new HashMap<>();
    }

    public void createAndStart(TopicEntity topicEntity) {
        if (listenerContainerMap.containsKey(topicEntity.getConsumerGroup())) {
            logger.info("Listener already exists for topic: {} with consumer group: {}", topicEntity.getTopicName(),
                    topicEntity.getConsumerGroup());

            ConcurrentMessageListenerContainer<String, GenericRecord> existingContainer = listenerContainerMap
                    .get(topicEntity.getConsumerGroup());
            if (!existingContainer.isRunning()) {
                existingContainer.start();
                logger.info("Restarted listener for topic: {} with consumer group: {}", topicEntity.getTopicName(),
                        topicEntity.getConsumerGroup());

                TopicEntity existingTopicEntity = topicService.get(topicEntity.getTopicName());
                existingTopicEntity.setConsumerState(ConsumerState.RUNNING);
                existingTopicEntity.setConsumerSeekTimestamp(null);

                topicService.saveOrUpdate(existingTopicEntity);

                return;
            }
        }

        ConcurrentKafkaListenerContainerFactory<String, GenericRecord> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(1);

        ConcurrentMessageListenerContainer<String, GenericRecord> newContainer = factory
                .createContainer(topicEntity.getTopicName());
        newContainer.getContainerProperties().setGroupId(topicEntity.getConsumerGroup());
        newContainer.getContainerProperties()
                .setMessageListener(new TopicMessageListener(topicEntity, topicDataService, null));
        newContainer.start();

        listenerContainerMap.put(topicEntity.getConsumerGroup(), newContainer);

        topicEntity.setConsumerState(ConsumerState.RUNNING);
        topicService.saveOrUpdate(topicEntity);

        logger.info("Started listener for topic: {} with consumer group: {}", topicEntity.getTopicName(),
                topicEntity.getConsumerGroup());
    }

    public boolean stop(TopicEntity topicEntity) {
        if (listenerContainerMap.containsKey(topicEntity.getConsumerGroup())) {
            ConcurrentMessageListenerContainer<String, GenericRecord> container = listenerContainerMap
                    .get(topicEntity.getConsumerGroup());
            if (container.isRunning()) {
                container.stop();
                logger.info("Stopped listener for topic: {} with consumer group: {}", topicEntity.getTopicName(),
                        topicEntity.getConsumerGroup());
            }
            return true;
        }

        logger.warn("No listener found for topic: {} with consumer group: {}", topicEntity.getTopicName(),
                topicEntity.getConsumerGroup());
        return false;
    }

    public void stopAndRemove(TopicEntity topicEntity) {
        if (stop(topicEntity)) {
            topicService.delete(topicEntity.getTopicName());
            topicDataService.delete(topicEntity.getTopicName());
            listenerContainerMap.remove(topicEntity.getConsumerGroup());
        }
    }

    public void seek(TopicEntity topicEntity, Long consumerSeekTimestamp) {
        if (listenerContainerMap.containsKey(topicEntity.getConsumerGroup())) {
            TopicEntity existingTopicEntity = topicService.get(topicEntity.getTopicName());

            if (existingTopicEntity.getConsumerState() != ConsumerState.SEEKING) {
                ConcurrentMessageListenerContainer<String, GenericRecord> existingContainer = listenerContainerMap
                        .get(topicEntity.getConsumerGroup());
                existingContainer.stop();
                existingContainer.getContainerProperties().setMessageListener(
                        new TopicMessageListener(topicEntity, topicDataService, consumerSeekTimestamp));
                existingContainer.start();

                existingTopicEntity.setConsumerState(ConsumerState.SEEKING);
                existingTopicEntity.setConsumerSeekTimestamp(topicEntity.getConsumerSeekTimestamp());

                topicService.saveOrUpdate(existingTopicEntity);

                logger.info("Seeking listener for topic: {} with consumer group: {} at instant: {}",
                        topicEntity.getTopicName(), topicEntity.getConsumerGroup(), consumerSeekTimestamp);
            }

            return;
        }

        logger.warn("No listener found for topic: {} with consumer group: {} to seek", topicEntity.getTopicName(),
                topicEntity.getConsumerGroup());
    }

    @Scheduled(fixedRate = 60000)
    public void pollForConsumerStateChanges() {
        topicService.getAll().forEach(topicEntity -> {
            switch (topicEntity.getConsumerState()) {
            case RUNNING -> createAndStart(topicEntity);
            case STOPPED -> stopAndRemove(topicEntity);
            case SEEKING -> seek(topicEntity, topicEntity.getConsumerSeekTimestamp());
            default -> logger.warn("Unknown consumer state: {} for topic: {}", topicEntity.getConsumerState(),
                    topicEntity.getTopicName());
            }
        });
    }

    @Scheduled(cron = "${cache.recovery.data-reset.cron}")
    public void resetSeek() {
        topicService.getAll().forEach(topic -> {
            if (topic.getConsumerState() == ConsumerState.SEEKING) {
                stop(topic);
                createAndStart(topic);
                logger.info("Seek reset complete for topic: {}", topic.getTopicName());
            }
        });
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        topicService.getAll().forEach(this::createAndStart);
    }
}
