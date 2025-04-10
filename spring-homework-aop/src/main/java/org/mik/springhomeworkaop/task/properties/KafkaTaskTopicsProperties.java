package org.mik.springhomeworkaop.task.properties;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class KafkaTaskTopicsProperties {

    @Value("${org.mik.task.kafka.topics.task_status_change}")
    private String taskStatusChange;


}
