package org.mik.springhomeworkaop.task.kafka.consumer;

import lombok.RequiredArgsConstructor;
import org.mik.springhomeworkaop.task.model.dto.TaskDto;
import org.mik.springhomeworkaop.task.service.TaskNotificationEmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaTaskConsumer {

    private final TaskNotificationEmailService taskNotificationEmailService;

    @KafkaListener(id = "${org.mik.task.kafka.consumer.group-id}",
            topics = "${org.mik.task.kafka.topics.task_status_change}",
            containerFactory = "concurrentKafkaListenerContainerFactory")
    public void listen(@Payload TaskDto task,
                       Acknowledgment ack,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        try {
            taskNotificationEmailService.notifyTaskByEmail("Таск с id %s был изменён статус на %s","Изменение таска %s",task);
        } finally {
            ack.acknowledge();
        }
    }


}
