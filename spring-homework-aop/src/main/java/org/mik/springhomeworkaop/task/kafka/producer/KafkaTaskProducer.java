package org.mik.springhomeworkaop.task.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class KafkaTaskProducer {

    private final KafkaTemplate kafkaTemplate;


    public void send(Long id) {
        try {
            kafkaTemplate.sendDefault(UUID.randomUUID().toString(),id);
            kafkaTemplate.flush();
        } catch (Exception ex) {

        }
    }

    public void sendTo(String topic, Object data) {
        try {
            kafkaTemplate.send(topic,data);
            kafkaTemplate.flush();
        } catch (Exception ex) {
        }
    }


    public void sendListTo(String topic, List<?> data) {
        try {
            for (Object dt : data) {
                kafkaTemplate.send(topic,dt);
            }
            kafkaTemplate.flush();
        } catch (Exception ex){

        }
    }

}
