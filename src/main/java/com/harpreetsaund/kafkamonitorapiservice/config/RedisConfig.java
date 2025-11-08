package com.harpreetsaund.kafkamonitorapiservice.config;

import com.harpreetsaund.kafkamonitorapiservice.model.TopicDataEntity;
import com.harpreetsaund.kafkamonitorapiservice.model.TopicEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
public class RedisConfig implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.username}")
    private String redisUsername;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    @Bean
    public RedisTemplate<String, TopicEntity> topicEntityRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, TopicEntity> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(TopicEntity.class));
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(TopicEntity.class));

        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, TopicDataEntity> topicDataEntityRedisTemplate(
            RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, TopicDataEntity> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(TopicDataEntity.class));
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(TopicDataEntity.class));

        return redisTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Redis configuration enabled.");
        logger.info("spring.data.redis.host: {}", redisHost);
        logger.info("spring.data.redis.port: {}", redisPort);
        logger.info("spring.data.redis.username: {}", redisUsername);
        logger.info("spring.data.redis.password: {}", StringUtils.isBlank(redisPassword) ? "<blank>" : "<hidden>");
    }
}
