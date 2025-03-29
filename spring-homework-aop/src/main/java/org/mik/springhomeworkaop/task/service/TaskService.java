package org.mik.springhomeworkaop.task.service;

import lombok.AllArgsConstructor;
import org.mik.springhomeworkaop.app.entity.Task;
import org.mik.springhomeworkaop.task.aspect.annotation.TaskLoggingExecution;
import org.mik.springhomeworkaop.task.aspect.annotation.TaskLoggingReturnExecution;
import org.mik.springhomeworkaop.task.aspect.annotation.TaskLoggingThrowingExecution;
import org.mik.springhomeworkaop.task.aspect.annotation.TaskLoggingTracking;
import org.mik.springhomeworkaop.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    @TaskLoggingTracking
    public List<Task> listTask() {
        return taskRepository.findAll();
    }

    @TaskLoggingExecution
    public Task taskById(Long idTask){
        return taskRepository.findById(idTask).orElse(null);
    }

    @TaskLoggingThrowingExecution
    public Task updateTask(Long taskId,Task task){
       return taskRepository.save(task);
    }

    @TaskLoggingThrowingExecution
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }


    @TaskLoggingReturnExecution
    public boolean deleteTaskById(Long idTask) {
        if (taskRepository.existsById(idTask)){
            taskRepository.deleteById(idTask);
            return true;
        } else {
            return false;
        }
    }

}
