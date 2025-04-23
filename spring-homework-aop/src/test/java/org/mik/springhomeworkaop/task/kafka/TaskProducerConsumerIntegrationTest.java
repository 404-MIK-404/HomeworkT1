package org.mik.springhomeworkaop.task.kafka;


import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mik.springhomeworkaop.config.TestEnvConfig;
import org.mik.springhomeworkaop.task.enums.TaskStatusEnum;
import org.mik.springhomeworkaop.task.exception.TaskException;
import org.mik.springhomeworkaop.task.kafka.producer.KafkaTaskProducer;
import org.mik.springhomeworkaop.task.model.dto.TaskDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Import(TestEnvConfig.class)
public class TaskProducerConsumerIntegrationTest {

    @Autowired
    private KafkaTaskProducer kafkaTaskProducer;

    @Autowired
    private ConsumerFactory consumerFactory;


    private final String TOPIC_SEND_TASK_DTO_TEST = "test_task_status_change";

    private final String TOPIC_SEND_EXCEPTION_TASK_TEST = "test_spring_homework_task_change_status_error";


    @BeforeEach
    public void initSetUp() {

    }

    @Test
    @DisplayName("Тест отправка список задач в Kafka и чтение сообщения из тестового топика")
    public void testOnSuccessSendListTask_SendListToKafka()  {
        TaskDto taskOne = new TaskDto(1L, "Task title", "desc", TaskStatusEnum.DRAW, 123L);
        List<TaskDto> listTaskDto = List.of(taskOne);

        sendListToKafka(TOPIC_SEND_TASK_DTO_TEST,listTaskDto);

        try (Consumer<String, TaskDto> consumer = consumerFactory.createConsumer()) {
            ConsumerRecords<String, TaskDto> records = getRecordsFromKafka(consumer, TOPIC_SEND_TASK_DTO_TEST);
            assertThat(records.count()).isGreaterThan(0);

            TaskDto taskDto = records.iterator().next().value();
            assertEquals(taskOne.getId(),taskDto.getId());
            assertEquals(taskOne.getDescription(),taskDto.getDescription());
            assertEquals(taskOne.getTitle(),taskDto.getTitle());
            assertEquals(taskOne.getIdUser(),taskDto.getIdUser());
            assertEquals(taskOne.getStatusName().name(),taskDto.getStatusName().name());
        }
    }



    @Test
    @DisplayName("Тест отправка ошибок с задачами в Kafka, чтобы уведомить пользователя")
    public void testOnSuccessSendListTaskException_SendListToKafka() {
        TaskException taskExceptionOne = new TaskException("Ошибка с таском","Описание ошибки",new TaskDto(1L, "Task title", "desc", TaskStatusEnum.DRAW, 123L));
        List<TaskException> listTaskException = List.of(taskExceptionOne);

        sendListToKafka(TOPIC_SEND_EXCEPTION_TASK_TEST,listTaskException);

        try (Consumer<String, TaskException> consumer = consumerFactory.createConsumer()) {
            ConsumerRecords<String, TaskException> records = getRecordsFromKafka(consumer, TOPIC_SEND_EXCEPTION_TASK_TEST);
            assertThat(records.count()).isGreaterThan(0);

            TaskException taskException = records.iterator().next().value();

            assertEquals(taskExceptionOne.getTitle(),taskException.getTitle());
            assertEquals(taskExceptionOne.getMessage(),taskException.getMessage());
            assertEquals(taskExceptionOne.getTaskDto().getId(),taskException.getTaskDto().getId());
            assertEquals(taskExceptionOne.getTaskDto().getDescription(),taskException.getTaskDto().getDescription());
            assertEquals(taskExceptionOne.getTaskDto().getTitle(),taskException.getTaskDto().getTitle());
            assertEquals(taskExceptionOne.getTaskDto().getIdUser(),taskException.getTaskDto().getIdUser());
            assertEquals(taskExceptionOne.getTaskDto().getStatusName().name(),taskException.getTaskDto().getStatusName().name());
        }
    }

    private <T> void sendListToKafka(String topic, List<T> list) {
        kafkaTaskProducer.sendListTo(topic, list);
    }

    private <T> ConsumerRecords<String, T> getRecordsFromKafka(Consumer<String, T> consumer, String topic) {
        consumer.subscribe(List.of(topic));
        return KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(30));
    }


}
