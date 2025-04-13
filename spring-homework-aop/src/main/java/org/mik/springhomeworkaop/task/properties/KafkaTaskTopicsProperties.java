package org.mik.springhomeworkaop.task.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "org.mik.task.kafka.topics")
public class KafkaTaskTopicsProperties {

    //@Value("${org.mik.task.kafka.topics.task_status_change}")
    //private String taskStatusChange;

    private String taskStatusChange;

    private String taskStatusChangeError;



}
