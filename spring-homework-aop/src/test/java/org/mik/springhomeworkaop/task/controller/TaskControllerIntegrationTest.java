package org.mik.springhomeworkaop.task.controller;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mik.springhomeworkaop.config.TestEnvConfig;
import org.mik.springhomeworkaop.task.enums.TaskStatusEnum;
import org.mik.springhomeworkaop.task.model.dto.TaskDto;
import org.mik.springhomeworkaop.task.model.entity.Task;
import org.mik.springhomeworkaop.task.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestEnvConfig.class)
public class TaskControllerIntegrationTest {

    private final String URL_TASK_API = "/api/tasks";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    public void initSetUp() {
        taskRepository.deleteAll();
        this.createTestTask();
    }


    @Test
    @DisplayName("Тест получения списка задач")
    public void getListTask_ReturnOkStatusAndListTask() throws Exception {
        mockMvc.perform(get(URL_TASK_API + "/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Тест поиск таска по ID, ID существует в БД")
    public void getTaskById_WithValidId_ReturnStatusOkAndTaskDto() throws Exception {
        List<Task> listTask = taskRepository.findAll();
        Long taskId = listTask.get(listTask.size() - 1).getId();
        mockMvc.perform(get(URL_TASK_API + "/" + taskId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(taskId));
    }

    @Test
    @DisplayName("Тест поиск таска по ID, ID не существует в БД")
    public void getTaskById_WithNotValidTaskId_ThrowEntityNotFoundException_ReturnStatusNotFound() throws Exception {
        Long taskId = 404L;
        mockMvc.perform(get(URL_TASK_API + "/" + taskId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }


    @Test
    @DisplayName("Тест обновление таска, таск полностью заполнен")
    public void putUpdateTask_WithValidTask_ReturnStatusOkAndUpdateTaskDto() throws Exception {
        Task task = taskRepository.findAll().get(0);
        TaskDto taskDto = new TaskDto(5L,"Интеграционный таск","Описание таска",TaskStatusEnum.DRAW,125L);
        taskDto.setId(task.getId());
        String jsonTaskDto = new ObjectMapper().writeValueAsString(taskDto);
        mockMvc.perform(put(URL_TASK_API + "/" + taskDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonTaskDto)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(taskDto.getId()))
                .andExpect(jsonPath("$.title").value(taskDto.getTitle()))
                .andExpect(jsonPath("$.description").value(taskDto.getDescription()))
                .andExpect(jsonPath("$.idUser").value(taskDto.getIdUser()))
                .andExpect(jsonPath("$.statusName").value(taskDto.getStatusName().name()));
    }

    @Test
    @DisplayName("Тест обновление таска, таск частично заполнен, возвращает null")
    public void putUpdateTask_WithNotValidTask_ReturnIllegalArgumentException() throws Exception {
        TaskDto taskDto = new TaskDto(2L,null,null,TaskStatusEnum.DRAW,125L);
        String jsonTaskDto = new ObjectMapper().writeValueAsString(taskDto);
        mockMvc.perform(put(URL_TASK_API + "/" + taskDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonTaskDto)
                )
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Тест создание таска, таск создан")
    public void postCreateTask_WithValidTask_ReturnCreateTaskDto() throws Exception {
        TaskDto taskDto = new TaskDto(null,"Создаём новый таск","Описание нового таска",TaskStatusEnum.DRAW,23423L);
        String jsonTaskDto = new ObjectMapper().writeValueAsString(taskDto);
        mockMvc.perform(post(URL_TASK_API + "/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonTaskDto))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(taskDto.getTitle()))
                .andExpect(jsonPath("$.description").value(taskDto.getDescription()))
                .andExpect(jsonPath("$.idUser").value(taskDto.getIdUser()));
    }

    @Test
    @DisplayName("Тест создание таска, таск частично пустой")
    public void postCreateTask_WithNotValidTask_ReturnIllegalArgumentException() throws Exception {
        TaskDto taskDto = new TaskDto(null,null,"Описание нового таска",null,23423L);
        String jsonTaskDto = new ObjectMapper().writeValueAsString(taskDto);
        mockMvc.perform(post(URL_TASK_API + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonTaskDto))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Тест удаление таска, ID существует в БД")
    public void deleteTaskById_WithValidId_ReturnResponseEntityNoContent() throws Exception {
        List<Task> listTask = taskRepository.findAll();
        Long taskId = listTask.get(listTask.size() - 1).getId();
        mockMvc.perform(delete(URL_TASK_API + "/" + taskId))
                .andExpect(status().isNoContent());
    }



    @Test
    @DisplayName("Тест удаление таска, ID не существует в БД")
    public void deleteTaskById_WithNotValidId_ReturnResponseEntityNotFound() throws Exception  {
        Long taskId = 6662L;
        mockMvc.perform(delete(URL_TASK_API + "/" + taskId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Тест обновление списка задач на новый статус")
    public void testUpdateStatusTask_WithValidListTaskIdAndStatusName() throws Exception {
        Task task1 = new Task();
        task1.setTitle("Тестовый таск");
        task1.setIdUser(1233L);
        task1.setDescription("ОООО тестовый таск");
        task1.setStatusName(TaskStatusEnum.NONE);
        task1 = taskRepository.save(task1);

        List<Long> taskIds = List.of(task1.getId());

        mockMvc.perform(put( URL_TASK_API + "/")
                        .param("statusName",TaskStatusEnum.DRAW.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(taskIds)))
                .andExpect(status().isNoContent());

        Task updatedTask = taskRepository.findById(task1.getId()).orElseThrow();
        assertEquals(TaskStatusEnum.DRAW, updatedTask.getStatusName());
    }


    private void createTestTask() {
        Task taskOne = new Task();
        taskOne.setTitle("Первая задачка в жизни, напиши Hello World !");
        taskOne.setDescription("Описание задачки 1");
        taskOne.setStatusName(TaskStatusEnum.DRAW);
        taskOne.setIdUser(10L);

        Task taskTwo = new Task();
        taskTwo.setTitle("Реализовать алгоритм А* ");
        taskTwo.setDescription("Описание задачки 2");
        taskTwo.setStatusName(TaskStatusEnum.SEND);
        taskTwo.setIdUser(44L);

        Task taskThree = new Task();
        taskThree.setTitle("Реализую тесты, главное не убей БД полностью");
        taskThree.setDescription("Описание задачки 3");
        taskThree.setStatusName(TaskStatusEnum.ERROR);
        taskThree.setIdUser(403L);

        taskRepository.saveAll(Arrays.asList(taskOne,taskTwo,taskThree));

    }


}
