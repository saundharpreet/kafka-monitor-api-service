package com.harpreetsaund.kafkamonitorapiservice.config;

import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableKafka
public class KafkaConfig implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConfig.class);

    @Bean
    @Primary
    public KafkaTemplate<String, GenericRecord> kafkaTemplate(KafkaProperties kafkaProperties) {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(kafkaProperties.buildConsumerProperties()));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Kafka configuration enabled.");
    }
}
