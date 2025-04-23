package org.mik.springhomeworkaop.task.service;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mik.springhomeworkaop.task.enums.TaskStatusEnum;
import org.mik.springhomeworkaop.task.kafka.producer.KafkaTaskProducer;
import org.mik.springhomeworkaop.task.mapper.TaskListMapper;
import org.mik.springhomeworkaop.task.mapper.TaskMapper;
import org.mik.springhomeworkaop.task.model.dto.TaskDto;
import org.mik.springhomeworkaop.task.model.entity.Task;
import org.mik.springhomeworkaop.task.properties.KafkaTaskTopicsProperties;
import org.mik.springhomeworkaop.task.repository.TaskRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceUnitTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TaskListMapper taskListMapper;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private KafkaTaskProducer kafkaTaskProducer;

    @Mock
    private KafkaTaskTopicsProperties kafkaTaskTopicsProperties;


    @Test
    @DisplayName("Тест получения списка задач")
    public void testListTask() {
        List<Task> listTask = Arrays.asList(
                new Task(2L,"Проверка таска #1","Описание таска", TaskStatusEnum.DRAW,1000L),
                new Task(5L,"Проверка таска #5","Описание таска", TaskStatusEnum.DRAW,1020L)
        );
        List<TaskDto> listTaskDto = Arrays.asList(
                new TaskDto(2L,"Проверка таска #1","Описание таска", TaskStatusEnum.DRAW,1000L),
                new TaskDto(5L,"Проверка таска #5","Описание таска", TaskStatusEnum.DRAW,1020L)
        );

        when(taskRepository.findAll()).thenReturn(listTask);

        when(taskListMapper.convertListEntityToListDto(listTask)).thenReturn(listTaskDto);

        List<TaskDto> listTaskDtoResult = taskService.listTask();

        assertEquals(listTaskDto.size(),listTaskDtoResult.size());
    }

    @Test
    @DisplayName("Тест поиск таска по ID, ID существует в БД возвращает DTO")
    public void testTaskById_WithValidTaskId() {
        Task task = new Task(32L, "Проверяем таск !", "Тестовое описание", TaskStatusEnum.DRAW, 320L);
        TaskDto taskDto = new TaskDto(32L, "Проверяем таск !", "Тестовое описание", TaskStatusEnum.DRAW, 320L);

        when(taskRepository.findById(32L)).thenReturn(Optional.of(task));

        when(taskMapper.convertEntityToDto(task)).thenReturn(taskDto);

        TaskDto taskDtoResult = taskService.taskById(32L);

        assertNotNull(taskDtoResult);
        assertEquals(taskDto.getId(),taskDtoResult.getId());
        assertEquals(taskDto.getIdUser(),taskDtoResult.getIdUser());
        assertEquals(taskDto.getTitle(),taskDtoResult.getTitle());
        assertEquals(taskDto.getDescription(),taskDtoResult.getDescription());
        assertEquals(taskDto.getStatusName(),taskDtoResult.getStatusName());
    }

    @Test
    @DisplayName("Тест поиск таска по ID, ID не существует в БД выбрасывает исключение EntityNotFoundException")
    public void testTaskById_WithNotValidTaskId_ThrowEntityNotFoundException() {
        when(taskRepository.findById(32L)).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class,()->{
            taskService.taskById(32L);
        });
    }


    @Test
    @DisplayName("Тест обновление таска, таск полностью заполнен")
    public void testUpdateTask_WithValidTask() {
        Task oldTask = new Task(67L, "Старый таск", "Описание старого таска", TaskStatusEnum.NONE, 500L);
        TaskDto taskDtoNeedUpdate = new TaskDto(67L, "Новый титульник таска", "Тест !", TaskStatusEnum.DRAW, 44L);

        Task taskUpdate = new Task(67L, "Новый титульник таска", "Тест !", TaskStatusEnum.DRAW, 44L);
        TaskDto taskDtoUpdate = new TaskDto(67L, "Новый титульник таска", "Тест !", TaskStatusEnum.DRAW, 44L);

        when(taskRepository.findById(67L)).thenReturn(Optional.of(oldTask));
        when(taskRepository.save(oldTask)).thenReturn(taskUpdate);
        when(taskMapper.convertEntityToDto(taskUpdate)).thenReturn(taskDtoUpdate);

        TaskDto taskDtoResult = taskService.updateTask(67L,taskDtoNeedUpdate);

        assertEquals(taskDtoNeedUpdate.getId(),taskDtoResult.getId());
        assertEquals(taskDtoNeedUpdate.getTitle(),taskDtoResult.getTitle());
        assertEquals(taskDtoNeedUpdate.getDescription(),taskDtoResult.getDescription());
        assertEquals(taskDtoNeedUpdate.getStatusName(),taskDtoResult.getStatusName());
        assertEquals(taskDtoNeedUpdate.getIdUser(),taskDtoResult.getIdUser());
    }

    @Test
    @DisplayName("Тест обновление таска, таск частично заполнен, возвращает исключение IllegalArgumentException")
    public void testUpdateTask_WithNotValidTask_ThrowIllegalArgumentException() {
        TaskDto taskDtoNeedUpdate = new TaskDto(22L,"Таск",null, null,122L);
        Task oldTask = new Task(22L,"Таск","В таске была ошибка",TaskStatusEnum.ERROR,122L);

        when(taskRepository.findById(22L)).thenReturn(Optional.of(oldTask));
        when(taskRepository.save(any())).thenThrow(new IllegalArgumentException("Ошибка при обновлений таска"));
        assertThrows(IllegalArgumentException.class,()->{
            taskService.updateTask(22L, taskDtoNeedUpdate);
        });
    }

    @Test
    @DisplayName("Тест создание таска, таск создан")
    public void testCreateTask_WithValidTask() {
        TaskDto taskDtoNeedCreate = new TaskDto(null, "Новый таск", "Новый таск", TaskStatusEnum.DRAW, 123L);
        Task taskCreated = new Task(365L, "Новый таск", "Новый таск", TaskStatusEnum.DRAW, 123L);

        TaskDto taskDtoCreated = new TaskDto(365L, "Новый таск", "Новый таск", TaskStatusEnum.DRAW, 123L);

        when(taskRepository.save(any())).thenReturn(taskCreated);

        when(taskMapper.convertEntityToDto(taskCreated)).thenReturn(taskDtoCreated);

        TaskDto taskDtoResult = taskService.createTask(taskDtoNeedCreate);

        assertNotNull(taskDtoResult);
        assertEquals(taskCreated.getId(), taskDtoResult.getId());
        assertEquals(taskCreated.getDescription(),taskDtoResult.getDescription());
        assertEquals(taskCreated.getStatusName(),taskDtoResult.getStatusName());
        assertEquals(taskCreated.getIdUser(),taskDtoResult.getIdUser());
        assertEquals(taskCreated.getTitle(),taskDtoResult.getTitle());
    }

    @Test
    @DisplayName("Тест создание таска, таск частично пустой, возвращает исключение IllegalArgumentException")
    public void testCreateTask_WithNotValidTask_ThrowIllegalArgumentException() {
        TaskDto newTaskDto = new TaskDto(365L, "Новый таск", null, TaskStatusEnum.DRAW, 123L);

        Task newTask = new Task(365L, "Новый таск", null, TaskStatusEnum.DRAW, 123L);

        when(taskMapper.convertDtoToEntity(newTaskDto)).thenReturn(newTask);

        when(taskRepository.save(any())).thenThrow(new IllegalArgumentException("Ошибка при созданий таска"));
        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(newTaskDto));
    }



    @Test
    @DisplayName("Тест удаление таска, ID существует в БД")
    public void testDeleteTaskById_WithValidId() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        boolean result = taskService.deleteTaskById(1L);

        assertTrue(result);

    }



    @Test
    @DisplayName("Тест удаление таска, ID не существует в БД")
    public void testDeleteTaskById_WithNotValidId(){

        when(taskRepository.existsById(1L)).thenReturn(false);

        boolean result = taskService.deleteTaskById(1L);

        assertFalse(result);

    }


    @Test
    @DisplayName("Тест обновление статуса таска")
    public void testUpdateStatusTask_WithValidStatusNameAndId() {
        List<Long> listTaskId = List.of(1L, 2L);
        String taskStatusEnum = TaskStatusEnum.DRAW.name();
        Task taskOne = new Task(1L, "Новый таск", "Проверка", TaskStatusEnum.NONE, 123L);
        Task taskTwo = new Task(2L,"Пицца Таск","Сделай пиццу 4 сыра",TaskStatusEnum.DRAW,167L);
        TaskDto taskDtoOne = new TaskDto(1L, "Новый таск", "Проверка", TaskStatusEnum.NONE, 123L);
        TaskDto taskDtoTwo = new TaskDto(2L,"Пицца Таск","Сделай пиццу 4 сыра",TaskStatusEnum.DRAW,167L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(taskOne));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(taskTwo));
        when(taskMapper.convertEntityToDto(taskOne)).thenReturn(taskDtoOne);
        when(taskMapper.convertEntityToDto(taskTwo)).thenReturn(taskDtoTwo);

        when(kafkaTaskTopicsProperties.getTaskStatusChange()).thenReturn("task-status-change-topic");

        taskService.updateStatusTask(listTaskId,taskStatusEnum);
        verify(taskRepository).save(any(Task.class));
        verify(taskRepository).save(any(Task.class));
        verify(kafkaTaskProducer).sendListTo(eq("task-status-change-topic"), anyList());
    }


}
