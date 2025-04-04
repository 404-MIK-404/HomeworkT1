package org.mik.springhomeworkaop.task.controller;

import lombok.AllArgsConstructor;
import org.mik.springhomeworkaop.task.mapper.TaskListMapper;
import org.mik.springhomeworkaop.task.mapper.TaskMapper;
import org.mik.springhomeworkaop.task.model.dto.TaskDTO;
import org.mik.springhomeworkaop.task.service.TaskService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/tasks",produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/")
    public List<TaskDTO> findAllTask() {
        return taskService.listTask();
    }

    @GetMapping("/{taskId}")
    public TaskDTO findTaskById(@PathVariable("taskId") Long taskId){
        return taskService.taskById(taskId);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable("taskId") Long taskId) {
        boolean isDeleted = taskService.deleteTaskById(taskId);
        return isDeleted ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/{taskId}")
    public TaskDTO updateTask(@PathVariable("taskId") Long taskId,@RequestBody TaskDTO taskDTO) {
        return taskService.updateTask(taskId,taskDTO);
    }

    @PostMapping("/")
    public TaskDTO createTask(@RequestBody TaskDTO taskDTO){
        return taskService.createTask(taskDTO);
    }



}
