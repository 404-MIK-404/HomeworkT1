package org.mik.springhomeworkaop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableAspectJAutoProxy
public class SpringHomeworkAopApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringHomeworkAopApplication.class, args);
    }

}
