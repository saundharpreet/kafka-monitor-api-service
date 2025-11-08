package com.harpreetsaund.kafkamonitorapiservice.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;

public class TopicDataEntity {

    private String topicName;
    private String consumerGroup;
    private String partition;
    private String offset;
    private String timestamp;
    private Map<String, String> headers;
    private String payload;

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("topicName", topicName).append("consumerGroup", consumerGroup)
                .append("partition", partition).append("offset", offset).append("timestamp", timestamp)
                .append("headers", headers).append("payload", payload).toString();
    }
}
