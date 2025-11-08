package com.harpreetsaund.kafkamonitorapiservice.mapper;

import com.harpreetsaund.kafkamonitorapiclient.request.KafkaMonitorRequest;
import com.harpreetsaund.kafkamonitorapiservice.model.TopicEntity;
import com.harpreetsaund.kafkamonitorapiservice.model.constants.ConsumerState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TopicMapper {

    private static final Logger logger = LoggerFactory.getLogger(TopicMapper.class);

    public TopicEntity toTopicEntity(KafkaMonitorRequest.Version1 kafkaMonitorRequest) {
        logger.debug("Mapping KafkaMonitorRequest to TopicEntity: {}", kafkaMonitorRequest);

        TopicEntity topicEntity = new TopicEntity();
        topicEntity.setTopicName(kafkaMonitorRequest.getTopicName());
        topicEntity.setConsumerGroup(kafkaMonitorRequest.getConsumerGroup());
        topicEntity.setConsumerState(ConsumerState.STARTING);

        return topicEntity;
    }
}
