package com.harpreetsaund.kafkamonitorapiservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "${project.name}"))
public class KafkaMonitorApiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaMonitorApiServiceApplication.class, args);
    }
}
