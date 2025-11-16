package com.harpreetsaund.kafkamonitorapiservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfig implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(SchedulingConfig.class);

    @Value("${cache.recovery.data-reset.cron}")
    private String recoveryDataResetCron;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Scheduling configuration enabled.");
        logger.info("cache.recovery.data-reset.cron: {}", recoveryDataResetCron);
    }
}
