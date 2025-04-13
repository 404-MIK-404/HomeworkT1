package org.mik.springhomeworkaop.task.exception.handler;


import lombok.RequiredArgsConstructor;
import org.mik.springhomeworkaop.task.exception.TaskException;
import org.mik.springhomeworkaop.task.kafka.consumer.KafkaTaskConsumer;
import org.mik.springhomeworkaop.task.kafka.producer.KafkaTaskProducer;
import org.mik.springhomeworkaop.task.model.dto.TaskDto;
import org.mik.springhomeworkaop.task.properties.KafkaTaskTopicsProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskErrorHandler {

    private final KafkaTaskProducer kafkaTaskProducer;

    private final KafkaTaskTopicsProperties kafkaTaskTopicsProperties;


    public TaskException handle(RuntimeException runException, String message, TaskDto taskDto) {
        TaskException taskException = new TaskException("Ошибка в сохраний статуса",message + runException.getMessage(),taskDto);
        kafkaTaskProducer.sendTo(kafkaTaskTopicsProperties.getTaskStatusChangeError(), taskException);
        return taskException;
    }

    public TaskException handleNotFoundTask(Long taskId) {
        return new TaskException("Ошибка в поиске задачи","Задача с id = " + taskId + " не найдена.",null);
    }


}
