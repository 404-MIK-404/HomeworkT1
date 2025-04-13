package org.mik.springhomeworkaop.task.controller;

import lombok.AllArgsConstructor;
import org.mik.springhomeworkaop.task.model.dto.TaskDto;
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
    public List<TaskDto> findAllTask() {
        return taskService.listTask();
    }

    @PutMapping("/")
    public ResponseEntity<Void> updateStatusTask(@RequestParam("statusName") String statusName,@RequestBody List<Long> listTaskId) {
        taskService.updateStatusTask(listTaskId, statusName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}")
    public TaskDto findTaskById(@PathVariable("taskId") Long taskId){
        return taskService.taskById(taskId);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable("taskId") Long taskId) {
        boolean isDeleted = taskService.deleteTaskById(taskId);
        return isDeleted ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/{taskId}")
    public TaskDto updateTask(@PathVariable("taskId") Long taskId, @RequestBody TaskDto taskDTO) {
        return taskService.updateTask(taskId,taskDTO);
    }

    @PostMapping("/")
    public TaskDto createTask(@RequestBody TaskDto taskDTO){
        return taskService.createTask(taskDTO);
    }



}
