package org.mik.springhomeworkaop.task.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.mik.springhomeworkaop.task.kafka.serializer.MessageTaskDeserializer;
import org.mik.springhomeworkaop.task.model.dto.TaskDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;

@Configuration
public class KafkaTaskConsumerConfig {

    @Value("${org.mik.task.kafka.bootstrap-servers}")
    private String serversBootstrap;

    @Value("${org.mik.task.kafka.consumer.group-id}")
    private String groupId;

    @Value("${org.mik.task.kafka.consumer.max-poll-records}")
    private Integer maxPollRecords;

    @Value("${org.mik.task.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;

    @Bean()
    public ConsumerFactory<String, TaskDto> consumerTaskFactory(){
        DefaultKafkaConsumerFactory<String, TaskDto> factory = new DefaultKafkaConsumerFactory<>(loadPropertiesConsumerFactory(new HashMap<>()));
        factory.setKeyDeserializer(new StringDeserializer());
        return factory;
    }


    private HashMap<String,Object> loadPropertiesConsumerFactory(HashMap<String,Object> properties) {
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, serversBootstrap);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, Boolean.FALSE);
        properties.put(JsonDeserializer.VALUE_DEFAULT_TYPE,"org.mik.springhomeworkaop.task.model.dto.TaskDto");
        properties.put(JsonDeserializer.TRUSTED_PACKAGES,"*");

        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        properties.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class.getName());
        properties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, MessageTaskDeserializer.class.getName());

        return properties;
    }

}
