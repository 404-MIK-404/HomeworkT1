package org.mik.springhomeworkaop.task.kafka;


import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mik.springhomeworkaop.config.KafkaTestTaskTemplateConfig;
import org.mik.springhomeworkaop.config.MailTestConfig;
import org.mik.springhomeworkaop.task.enums.TaskStatusEnum;
import org.mik.springhomeworkaop.task.exception.TaskException;
import org.mik.springhomeworkaop.task.kafka.producer.KafkaTaskProducer;
import org.mik.springhomeworkaop.task.model.dto.TaskDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(MailTestConfig.class)
public class KafkaTaskProducerConsumerIntegrationTest extends KafkaTestTaskTemplateConfig {

    @Autowired
    private KafkaTaskProducer kafkaTaskProducer;

    @Autowired
    private ConsumerFactory consumerFactory;


    @Test
    @DisplayName("Тест отправка список задач в Kafka и чтение сообщения из тестового топика")
    public void testOnSuccessSendListTask_SendListToKafka()  {
        List<TaskDto> listTaskDto = List.of(new TaskDto(1L, "Task title", "desc", TaskStatusEnum.DRAW, 123L));

        kafkaTaskProducer.sendListTo("test_task_status_change",listTaskDto);

        try (Consumer<String, TaskDto> consumer = consumerFactory.createConsumer()) {
            consumer.subscribe(List.of("test_task_status_change"));
            ConsumerRecords<String, TaskDto> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(30));
            assertThat(records.count()).isGreaterThan(0);

            TaskDto taskDto = records.iterator().next().value();

            System.out.println(taskDto.getTitle());

            /*
            records.forEach(record -> {
                System.out.println("Received task: " + record.value());
            });

             */
        }
    }



    @Test
    @DisplayName("Тест отправка ошибок с задачами в Kafka, чтобы уведомить пользователя")
    public void testOnSuccessSendListTaskException_SendListToKafka() {
        List<TaskException> listTaskException = List.of(new TaskException("1","2",new TaskDto(1L, "Task title", "desc", TaskStatusEnum.DRAW, 123L)));

        kafkaTaskProducer.sendListTo("test_spring_homework_task_change_status_error",listTaskException);

        try (Consumer<String, TaskException> consumer = consumerFactory.createConsumer()) {
            consumer.subscribe(List.of("test_spring_homework_task_change_status_error"));
            ConsumerRecords<String, TaskException> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(30));
            assertThat(records.count()).isGreaterThan(0);

            TaskException taskException = records.iterator().next().value();
            System.out.println(taskException.getTitle());
            /*
            records.forEach(record -> {
                System.out.println("Не таск: " + record.value());
            });

             */
        }
    }


}
