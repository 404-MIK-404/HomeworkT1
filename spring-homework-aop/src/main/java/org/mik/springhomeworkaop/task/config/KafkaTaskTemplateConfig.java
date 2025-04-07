package org.mik.springhomeworkaop.task.config;

import lombok.extern.slf4j.Slf4j;
import org.mik.springhomeworkaop.task.model.dto.TaskDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class KafkaTaskTemplateConfig {


    @Bean("kafkaTaskTemplate")
    public KafkaTemplate<String, TaskDto> kafkaTaskTemplate(ProducerFactory<String, TaskDto> producerFactory){
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TaskDto>  concurrentKafkaListenerContainerFactory(
            @Qualifier("consumerTaskFactory")ConsumerFactory<String, TaskDto> consumerTaskFactory) {
        ConcurrentKafkaListenerContainerFactory<String, TaskDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factoryBuilder(consumerTaskFactory,factory);
        return factory;
    }

    private void factoryBuilder(ConsumerFactory<String, TaskDto> consumerFactory, ConcurrentKafkaListenerContainerFactory<String, TaskDto> factory) {
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setPollTimeout(5000);
        factory.getContainerProperties().setMicrometerEnabled(true);
        factory.setCommonErrorHandler(errorHandler());
    }

    private CommonErrorHandler errorHandler() {
        DefaultErrorHandler handler = new DefaultErrorHandler(new FixedBackOff(1000,3));
        handler.addNotRetryableExceptions(IllegalStateException.class);
        handler.setRetryListeners(((record, ex, deliveryAttempt) -> {
            log.error("Ошибка при попытке обработки сообщения. Сообщение: {}. Оффсет: {} Попытка: {}", ex.getMessage(),record.offset(),deliveryAttempt);
        }));
        return handler;
    }


}
