package org.mik.springhomeworkaop.task.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.mail.senders")
public class EmailTaskProperties {

    //@Value("${spring.mail.username}")
    private String emailFrom;

    //@Value("${spring.sender.mail.username}")
    private String emailTo;

}
