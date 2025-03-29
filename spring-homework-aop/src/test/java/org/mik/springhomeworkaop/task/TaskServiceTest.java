package org.mik.springhomeworkaop.task;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mik.springhomeworkaop.app.entity.Task;
import org.mik.springhomeworkaop.task.repository.TaskRepository;
import org.mik.springhomeworkaop.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TaskServiceTest {

    @Autowired
    private TaskService taskService;


    @MockitoBean
    private TaskRepository taskRepository;


    @Test
    @DisplayName("Список задач")
    public void testListTask() throws InterruptedException {
        List<Task> listTaskByDefault = Arrays.asList(
                new Task(2L,"Проверка таска #1","Описание таска",1000L),
                new Task(5L,"Проверка таска #5","Описание таска",1020L),
                new Task(1L,"Проверка таска #1","Описание таска",1040L),
                new Task(777L,"Проверка таска #777","Описание таска",1000L),
                new Task(3L,"Проверка таска #3","Описание таска",13200L)
        );
        when(taskRepository.findAll()).thenAnswer(invocation->{
            Thread.sleep(500);
            return listTaskByDefault;
        });

        List<Task> listTask = taskService.listTask();

        assertNotNull(listTask);
        assertEquals(5,listTask.size());
    }

    @Test
    @DisplayName("Поиск таска по ID, ID существует в БД")
    public void testTaskById_WithValidTaskId() {
        Task taskDefault = new Task(32L, "Проверяем таск !", "Тестовое описание",320L);

        when(taskRepository.findById(32L)).thenReturn(Optional.of(taskDefault));

        Task task = taskService.taskById(32L);

        assertNotNull(task);
        assertEquals(32L,task.getId());
        assertEquals("Проверяем таск !",task.getTitle());
    }

    @Test
    @DisplayName("Поиск таска по ID, ID не существует в БД")
    public void testTaskById_WithNotValidTaskId() {
        when(taskRepository.findById(32L)).thenReturn(Optional.empty());

        Task task = taskService.taskById(32L);

        assertNull(task);
    }

    @Test
    @DisplayName("Обновление таска, таск полностью заполнен")
    public void testUpdateTask_WithValidTask() {
        Task existingTask = new Task(55L, "Старт", "Оп", 123L);
        Task updatedTask = new Task(67L, "Новый", "К-поп", 44L);

        when(taskRepository.save(updatedTask)).thenReturn(updatedTask);
        when(taskRepository.findById(67L)).thenReturn(Optional.of(existingTask));

        Task result = taskService.updateTask(55L, updatedTask);

        assertNotNull(result);
        assertEquals("Новый", result.getTitle());
    }

    @Test
    @DisplayName("Обновление таска, таск частично заполнен")
    public void testUpdateTask_WithNotValidTask() {
        Task updateTask = new Task(22L,"Таск новый",null,122L);

        when(taskRepository.save(updateTask)).thenThrow(new RuntimeException(""));

        assertThrows(RuntimeException.class, () -> taskService.updateTask(22L, updateTask));
    }


    @Test
    @DisplayName("Создание таска, таск создан")
    public void testCreateTask_WithValidTask() {
        Task newTask = new Task(365L, "Новый таск", "Новый таск", 123L);

        when(taskRepository.save(newTask)).thenReturn(newTask);

        Task result = taskService.createTask(newTask);

        assertNotNull(result);
        assertEquals("Новый таск", result.getTitle());
    }

    @Test
    @DisplayName("Создание таска, таск частично пустой")
    public void testCreateTask_WithNotValidTask() {
        Task newTask = new Task(365L, "Новый таск", null, 123L);

        when(taskRepository.save(newTask)).thenThrow(new RuntimeException(""));

        assertThrows(RuntimeException.class, () -> taskService.createTask(newTask));
    }



    @Test
    @DisplayName("Удаление таска, ID существует в БД")
    public void testDeleteTaskById_WithNotValidId() {
        when(taskRepository.existsById(1L)).thenReturn(false);

        boolean result = taskService.deleteTaskById(1L);

        assertFalse(result);
    }



    @Test
    @DisplayName("Удаление таска, ID не существует в БД")
    public void testDeleteTaskById_WithValidId(){
        when(taskRepository.existsById(1L)).thenReturn(true);

        boolean result = taskService.deleteTaskById(1L);

        assertTrue(result);
    }




}
