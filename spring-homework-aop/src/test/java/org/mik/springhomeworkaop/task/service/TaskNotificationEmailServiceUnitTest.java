package org.mik.springhomeworkaop.task.service;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mik.springhomeworkaop.task.enums.TaskStatusEnum;
import org.mik.springhomeworkaop.task.exception.TaskException;
import org.mik.springhomeworkaop.task.model.dto.TaskDto;
import org.mik.springhomeworkaop.task.properties.EmailTaskProperties;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskNotificationEmailServiceUnitTest {

    @InjectMocks
    private TaskNotificationEmailService taskNotificationEmailService;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private EmailTaskProperties emailTaskProperties;

    @Test
    @DisplayName("Тест отправки уведомлений по email о задачах")
    public void testNotifyTaskByEmail() {
        List<TaskDto> listTaskDto = Arrays.asList(
                new TaskDto(1L, "Первое задание", "Описание первого задания", TaskStatusEnum.DRAW, 123L),
                new TaskDto(2L,"Второе задание","Описание второго задания",TaskStatusEnum.SEND,333L)
        );
        String textTemplate = "Задача №%d имеет статус %s";
        String titleTemplate = "Уведомление по задачам";

        when(emailTaskProperties.getEmailTo()).thenReturn("to@example.com");
        when(emailTaskProperties.getEmailFrom()).thenReturn("from@example.com");

        taskNotificationEmailService.notifyTaskByEmail(textTemplate,titleTemplate,listTaskDto);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(captor.capture());
        SimpleMailMessage simpleMailMessage = captor.getValue();

        String expectedText = """
                Задача №1 имеет статус DRAW
                Задача №2 имеет статус SEND
                """.trim();

        assertEquals(expectedText,simpleMailMessage.getText());
        assertEquals("to@example.com",simpleMailMessage.getTo()[0]);
        assertEquals("from@example.com",simpleMailMessage.getFrom());
        assertEquals("Уведомление по задачам", simpleMailMessage.getSubject());

    }

    @Test
    @DisplayName("Тест выброс исключения при отправки уведомлений по email о задачах")
    public void testNotifyTaskByEmail_WithNotValidEmailTo_ReturnSenderMailException() {
        List<TaskDto> listTaskDto = Arrays.asList(
                new TaskDto(1L, "Первое задание", "Описание первого задания", TaskStatusEnum.DRAW, 123L),
                new TaskDto(2L,"Второе задание","Описание второго задания",TaskStatusEnum.COMPLETE,333L)
        );
        String textTemplate = "Задача №%d имеет статус %s";
        String titleTemplate = "Уведомление по задачам";

        when(emailTaskProperties.getEmailTo()).thenReturn(null);
        when(emailTaskProperties.getEmailFrom()).thenReturn("from@example.com");

        doThrow(new MailSendException("Ошибка отправки письма"))
                .when(javaMailSender).send(any(SimpleMailMessage.class));

        assertThrows(MailSendException.class,() ->{
            taskNotificationEmailService.notifyTaskByEmail(textTemplate,titleTemplate,listTaskDto);
        });
    }


    @Test
    @DisplayName("Тест отправки уведомлений по email о ошибках в задачах")
    public void testNotifyTaskExceptionByEmail() {
        List<TaskException> listTaskException = Arrays.asList(
                new TaskException("Задача №1","Произошла ошибка при поиске задачки с id 1",
                        new TaskDto(1L, "Первое задание", "Описание первого задания", TaskStatusEnum.DRAW, 123L)),
                new TaskException("Задача №2","Произошла ошибка при работе задачки с id 2",
                        new TaskDto(2L,"Второе задание","Описание второго задания",TaskStatusEnum.COMPLETE,333L))
        );

        String titleTemplate = "Уведомление по задачам";

        when(emailTaskProperties.getEmailTo()).thenReturn("to@example.com");
        when(emailTaskProperties.getEmailFrom()).thenReturn("from@example.com");

        taskNotificationEmailService.notifyTaskExceptionByEmail(titleTemplate,listTaskException);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(captor.capture());
        SimpleMailMessage simpleMailMessage = captor.getValue();

        String expectedText = """
                Задача №1\tПроизошла ошибка при поиске задачки с id 1
                Задача №2\tПроизошла ошибка при работе задачки с id 2
                """.trim();

        assertEquals(expectedText,simpleMailMessage.getText());
        assertEquals("to@example.com",simpleMailMessage.getTo()[0]);
        assertEquals("from@example.com",simpleMailMessage.getFrom());
        assertEquals("Уведомление по задачам", simpleMailMessage.getSubject());

    }

    @Test
    @DisplayName("Тест выброс исключения при отправки уведомлений по email о ошибках в задачах")
    public void testNotifyTaskExceptionByEmail_NotValidIdTaskDto_ThrowsException() {
        List<TaskException> listTaskException = List.of(
                new TaskException("Задача №1", "Произошла ошибка при поиске задачки с id 1",
                        new TaskDto(null, "Первое задание", "Описание первого задания", TaskStatusEnum.DRAW, 123L))
        );

        String titleTemplate = "Уведомление по задачам";

        when(emailTaskProperties.getEmailTo()).thenReturn("to@example.com");
        when(emailTaskProperties.getEmailFrom()).thenReturn("from@example.com");

        doThrow(new MailSendException("Ошибка отправки письма"))
                .when(javaMailSender).send(any(SimpleMailMessage.class));

        assertThrows(MailSendException.class,() ->{
            taskNotificationEmailService.notifyTaskExceptionByEmail(titleTemplate,listTaskException);
        });
    }


}
