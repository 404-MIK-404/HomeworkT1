package org.mik.springhomeworkaop.config;


import org.mik.springhomeworkaop.task.model.dto.TaskDto;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

@TestConfiguration
public class KafkaTestTaskTemplateConfig {


    static KafkaContainer kafka;

    static {
        kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.0.1"));
        kafka.start();
        System.setProperty("org.mik.task.kafka.bootstrap-servers", kafka.getBootstrapServers());
        System.setProperty("org.mik.task.kafka.consumer.group-id","test-task-status-group-" + UUID.randomUUID());
    }


}
