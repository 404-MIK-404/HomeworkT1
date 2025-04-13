package org.mik.springhomeworkaop;

import org.mik.springhomeworkaop.task.properties.EmailTaskProperties;
import org.mik.springhomeworkaop.task.properties.KafkaTaskTopicsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableConfigurationProperties({KafkaTaskTopicsProperties.class,EmailTaskProperties.class})
public class SpringHomeworkAopApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringHomeworkAopApplication.class, args);
    }

}
