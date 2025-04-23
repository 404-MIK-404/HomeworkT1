package org.mik.springhomeworkaop.task.service;


import lombok.AllArgsConstructor;
import org.mik.springhomeworkaop.task.exception.TaskException;
import org.mik.springhomeworkaop.task.model.dto.TaskDto;
import org.mik.springhomeworkaop.task.properties.EmailTaskProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class TaskNotificationEmailService {

    private final JavaMailSender javaMailSender;

    private final EmailTaskProperties emailTaskProperties;


    public void notifyTaskByEmail(String textTmp, String titleTmp, List<TaskDto> listTaskDto){

        SimpleMailMessage mailMessageTask = initEmailSenders();

        String messageText = listTaskDto.stream()
                .map(taskDto -> String.format(textTmp, taskDto.getId(), taskDto.getStatusName()))
                .collect(Collectors.joining("\n"));
        String title = String.format(titleTmp);

        mailMessageTask.setSubject(title);
        mailMessageTask.setText(messageText);
        javaMailSender.send(mailMessageTask);
    }

    public void notifyTaskExceptionByEmail(String titleTmp, List<TaskException> listTaskException) {

        SimpleMailMessage mailMessageException = initEmailSenders();

        String messageText = listTaskException.stream()
                .map(taskException-> taskException.getTitle() + "\t" + taskException.getMessage())
                .collect(Collectors.joining("\n"));

        mailMessageException.setSubject(titleTmp);

        mailMessageException.setText(messageText);

        javaMailSender.send(mailMessageException);

    }

    private SimpleMailMessage initEmailSenders() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailTaskProperties.getEmailFrom());
        message.setTo(emailTaskProperties.getEmailTo());
        return message;
    }

}
