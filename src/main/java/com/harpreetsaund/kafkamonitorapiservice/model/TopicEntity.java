package com.harpreetsaund.kafkamonitorapiservice.model;

import com.harpreetsaund.kafkamonitorapiservice.model.constants.ConsumerState;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class TopicEntity {

    private String topicName;
    private String consumerGroup;
    private ConsumerState consumerState;
    private Long consumerSeekTimestamp;

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

    public ConsumerState getConsumerState() {
        return consumerState;
    }

    public void setConsumerState(ConsumerState consumerState) {
        this.consumerState = consumerState;
    }

    public Long getConsumerSeekTimestamp() {
        return consumerSeekTimestamp;
    }

    public void setConsumerSeekTimestamp(Long consumerSeekTimestamp) {
        this.consumerSeekTimestamp = consumerSeekTimestamp;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("topicName", topicName).append("consumerGroup", consumerGroup)
                .append("consumerState", consumerState).append("consumerSeekTimestamp", consumerSeekTimestamp)
                .toString();
    }
}
