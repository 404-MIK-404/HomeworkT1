package org.mik.springhomeworkaop.config;


import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static org.mockito.Mockito.mock;

@Configuration
public class TestEnvConfig {

    static KafkaContainer kafka;


    static {
        kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.0.1"));
        kafka.start();
        System.setProperty("org.mik.task.kafka.bootstrap-servers", kafka.getBootstrapServers());
        System.setProperty("org.mik.task.kafka.consumer.group-id","test-task-status-group-" + UUID.randomUUID());

    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSender mockMailSender = mock(JavaMailSender.class);
        return mockMailSender;
    }

}
