package org.mik.springhomeworkaop.task.service;

import lombok.AllArgsConstructor;
import org.mik.springhomeworkaop.task.exception.handler.TaskErrorHandler;
import org.mik.springhomeworkaop.task.enums.TaskStatusEnum;
import org.mik.springhomeworkaop.task.exception.TaskException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final KafkaTaskProducer kafkaTaskProducer;

    private final TaskErrorHandler taskErrorHandler;

    private final TaskMapper taskMapper;

    private final TaskListMapper taskListMapper;

    private final KafkaTaskTopicsProperties kafkaTaskTopicsProperties;


    public void updateStatusTask(List<Long> listTaskId,String statusName) {
        List<TaskDto> listUpdTask = new ArrayList<>();
        List<TaskException> listExceptionTask = new ArrayList<>();
        listTaskId.forEach(taskId-> taskRepository.findById(taskId).ifPresentOrElse(task->{
            TaskDto taskDto = taskMapper.convertEntityToDto(task);
            try {
                TaskStatusEnum oldStatusName = task.getStatusName();
                if (Objects.equals(oldStatusName.name(),statusName)){
                    return;
                }
                taskDto.setStatusName(TaskStatusEnum.valueOf(statusName));
                task.setStatusName(TaskStatusEnum.valueOf(statusName));
                taskRepository.save(task);
                listUpdTask.add(taskMapper.convertEntityToDto(task));
            } catch (RuntimeException ex) {
                taskDto.setStatusName(TaskStatusEnum.NONE);
                TaskException taskException = taskErrorHandler.handle(ex,"Произошла ошибка при изменении статуса.\nКод ошибки: ",taskDto);
                listExceptionTask.add(taskException);
            }
        }, () -> {
            TaskException taskException = taskErrorHandler.handleNotFoundTask(taskId);
            listExceptionTask.add(taskException);
        }));
        if (!listUpdTask.isEmpty()){
            kafkaTaskProducer.sendListTo(kafkaTaskTopicsProperties.getTaskStatusChange(),listUpdTask);
        }
        if (!listExceptionTask.isEmpty()){
            kafkaTaskProducer.sendListTo(kafkaTaskTopicsProperties.getTaskStatusChangeError(),listExceptionTask);
        }
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
