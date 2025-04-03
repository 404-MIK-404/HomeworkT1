package org.mik.springhomeworkaop.task.controller;

import lombok.AllArgsConstructor;
import org.mik.springhomeworkaop.app.entity.Task;
import org.mik.springhomeworkaop.task.service.TaskService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/tasks",produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/")
    public List<Task> findAllTask() {
        return taskService.listTask();
    }

    @GetMapping("/{taskId}")
    public Task findTaskById(@PathVariable("taskId") Long taskId){
        return taskService.taskById(taskId);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable("taskId") Long taskId) {
        boolean isDeleted = taskService.deleteTaskById(taskId);
        return isDeleted ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/{taskId}")
    public Task updateTask(@PathVariable("taskId") Long taskId,@RequestBody Task task) {
        return taskService.updateTask(taskId,task);
    }

    @PostMapping("/")
    public Task createTask(@RequestBody Task task){
        return taskService.createTask(task);
    }



}
