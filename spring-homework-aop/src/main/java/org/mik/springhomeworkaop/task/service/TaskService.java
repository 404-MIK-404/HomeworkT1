package org.mik.springhomeworkaop.task.service;

import lombok.AllArgsConstructor;
import org.mik.springhomeworkaop.task.kafka.producer.KafkaTaskProducer;
import org.mik.springhomeworkaop.task.mapper.TaskListMapper;
import org.mik.springhomeworkaop.task.mapper.TaskMapper;
import org.mik.springhomeworkaop.task.model.dto.TaskDto;
import org.mik.springhomeworkaop.task.model.entity.Task;
import org.mik.springhomeworkaop.task.aspect.annotation.TaskLoggingExecution;
import org.mik.springhomeworkaop.task.aspect.annotation.TaskLoggingReturnExecution;
import org.mik.springhomeworkaop.task.aspect.annotation.TaskLoggingThrowingExecution;
import org.mik.springhomeworkaop.task.aspect.annotation.TaskLoggingTracking;
import org.mik.springhomeworkaop.task.properties.KafkaTaskTopicsProperties;
import org.mik.springhomeworkaop.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final KafkaTaskProducer kafkaTaskProducer;

    private final TaskMapper taskMapper;

    private final TaskListMapper taskListMapper;

    private final KafkaTaskTopicsProperties kafkaTaskTopicsProperties;


    public void updateStatusTask(Long taskId,String statusName) {
        TaskDto taskDTO = taskMapper.convertEntityToDto(taskRepository.findById(taskId).orElse(null));
        taskDTO.setStatusName(statusName);
        taskRepository.save(taskMapper.convertDtoToEntity(taskDTO));
        kafkaTaskProducer.sendTo(kafkaTaskTopicsProperties.getTaskStatusChange(),taskDTO);
    }

    @TaskLoggingTracking
    public List<TaskDto> listTask() {
        return taskListMapper.convertListEntityToListDto(taskRepository.findAll());
    }

    @TaskLoggingExecution
    public TaskDto taskById(Long idTask){
        return taskMapper.convertEntityToDto(
                taskRepository.findById(idTask).orElse(null)
        );
    }

    @TaskLoggingThrowingExecution
    public TaskDto updateTask(Long taskId, TaskDto taskDTO){
        Task task = taskMapper.convertDtoToEntity(taskDTO);
        return taskMapper.convertEntityToDto(taskRepository.save(task));
    }

    @TaskLoggingThrowingExecution
    public TaskDto createTask(TaskDto taskDTO) {
        Task task = taskMapper.convertDtoToEntity(taskDTO);
        return taskMapper.convertEntityToDto(taskRepository.save(task));
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
