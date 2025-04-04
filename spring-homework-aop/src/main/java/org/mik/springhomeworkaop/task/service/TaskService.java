package org.mik.springhomeworkaop.task.service;

import lombok.AllArgsConstructor;
import org.mik.springhomeworkaop.task.mapper.TaskListMapper;
import org.mik.springhomeworkaop.task.mapper.TaskMapper;
import org.mik.springhomeworkaop.task.model.dto.TaskDTO;
import org.mik.springhomeworkaop.task.model.entity.Task;
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

    private final TaskMapper taskMapper;

    private final TaskListMapper taskListMapper;

    @TaskLoggingTracking
    public List<TaskDTO> listTask() {
        return taskListMapper.convertListEntityToListDTO(taskRepository.findAll());
    }

    @TaskLoggingExecution
    public TaskDTO taskById(Long idTask){
        return taskMapper.convertEntityToDTO(
                taskRepository.findById(idTask).orElse(null)
        );
    }

    @TaskLoggingThrowingExecution
    public TaskDTO updateTask(Long taskId,TaskDTO taskDTO){
        Task task = taskMapper.convertDtoToEntity(taskDTO);
        return taskMapper.convertEntityToDTO(taskRepository.save(task));
    }

    @TaskLoggingThrowingExecution
    public TaskDTO createTask(TaskDTO taskDTO) {
        Task task = taskMapper.convertDtoToEntity(taskDTO);
        return taskMapper.convertEntityToDTO(taskRepository.save(task));
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
