package org.mik.springhomeworkaop.task.properties;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class EmailTaskProperties {

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Value("${spring.sender.mail.username}")
    private String emailTo;

}
