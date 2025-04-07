package org.mik.springhomeworkaop.task.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.mik.springhomeworkaop.task.model.dto.TaskDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;

@Configuration
public class KafkaTaskProducerConfig {

    @Value("${org.mik.task.kafka.bootstrap-servers}")
    private String serversBootstrap;


    @Bean
    public ProducerFactory<String, TaskDto> producerTaskFactory(){
        return new DefaultKafkaProducerFactory<>(loadPropertiesProducerFactory(new HashMap<>()));
    }


    private HashMap<String,Object> loadPropertiesProducerFactory(HashMap<String,Object> properties){
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,serversBootstrap);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return properties;
    }


}
