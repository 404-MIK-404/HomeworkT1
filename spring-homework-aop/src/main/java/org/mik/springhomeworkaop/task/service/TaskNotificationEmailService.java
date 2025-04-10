package org.mik.springhomeworkaop.task.service;


import lombok.AllArgsConstructor;
import org.mik.springhomeworkaop.task.model.dto.TaskDto;
import org.mik.springhomeworkaop.task.properties.EmailTaskProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class TaskNotificationEmailService {

    private final JavaMailSender javaMailSender;

    private final EmailTaskProperties emailTaskProperties;
    public void notifyTaskByEmail(String textTmp,String titleTmp,TaskDto taskDto){

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailTaskProperties.getEmailFrom());
        message.setTo(emailTaskProperties.getEmailTo());

        String title = String.format(titleTmp,taskDto.getId());
        String text = String.format(textTmp,taskDto.getId(),taskDto.getStatusName());

        message.setSubject(title);
        message.setText(text);
        javaMailSender.send(message);
    }

}
