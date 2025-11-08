package com.harpreetsaund.kafkamonitorapiservice.listener;

import com.harpreetsaund.kafkamonitorapiservice.model.TopicDataEntity;
import com.harpreetsaund.kafkamonitorapiservice.model.TopicEntity;
import com.harpreetsaund.kafkamonitorapiservice.service.TopicDataService;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.listener.MessageListener;
import reactor.util.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TopicMessageListener implements MessageListener<String, GenericRecord> {

    private final TopicEntity topicEntity;

    private final TopicDataService topicDataService;

    public TopicMessageListener(TopicEntity topicEntity, TopicDataService topicDataService) {
        this.topicEntity = topicEntity;
        this.topicDataService = topicDataService;
    }

    @Override
    public void onMessage(@NonNull ConsumerRecord<String, GenericRecord> consumerRecord) {
        TopicDataEntity topicDataEntity = new TopicDataEntity();
        topicDataEntity.setTopicName(topicEntity.getTopicName());
        topicDataEntity.setConsumerGroup(topicEntity.getConsumerGroup());
        topicDataEntity.setPartition(String.valueOf(consumerRecord.partition()));
        topicDataEntity.setOffset(String.valueOf(consumerRecord.offset()));
        topicDataEntity.setTimestamp(String.valueOf(consumerRecord.timestamp()));
        topicDataEntity.setHeaders(convertHeadersToMap(consumerRecord.headers()));
        topicDataEntity.setPayload(consumerRecord.value().toString());

        topicDataService.insert(topicDataEntity);
    }

    private Map<String, String> convertHeadersToMap(Headers headers) {
        Map<String, String> headerMap = new HashMap<>();
        if (headers != null) {
            for (Header header : headers) {
                String value = header.value() != null ? new String(header.value(), StandardCharsets.UTF_8) : null;
                headerMap.put(header.key(), value);
            }
        }

        return headerMap;
    }
}
