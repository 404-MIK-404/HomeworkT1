package org.mik.springhomeworkaop.task.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.mik.springhomeworkaop.task.exception.handler.TaskErrorHandler;
import org.mik.springhomeworkaop.task.enums.TaskStatusEnum;
import org.mik.springhomeworkaop.task.exception.TaskException;
import org.mik.springhomeworkaop.task.kafka.producer.KafkaTaskProducer;
import org.mik.springhomeworkaop.task.mapper.TaskListMapper;
import org.mik.springhomeworkaop.task.mapper.TaskMapper;
import org.mik.springhomeworkaop.task.model.dto.TaskDto;
import org.mik.springhomeworkaop.task.model.entity.Task;
import org.mik.springhomeworkaop.task.properties.KafkaTaskTopicsProperties;
import org.mik.springhomeworkaop.task.repository.TaskRepository;
import org.mik.starterhomeworkaspect.aspect.annotation.LoggingAroundTrackingExecution;
import org.mik.starterhomeworkaspect.aspect.annotation.LoggingBeforeTrackingExecution;
import org.mik.starterhomeworkaspect.aspect.annotation.LoggingReturnTrackingExecution;
import org.mik.starterhomeworkaspect.aspect.annotation.LoggingThrowTrackingExecution;
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
            processTaskStatusUpdate(task,statusName,listUpdTask,listExceptionTask);
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

    @LoggingAroundTrackingExecution
    public List<TaskDto> listTask() {
        return taskListMapper.convertListEntityToListDto(taskRepository.findAll());
    }

    @LoggingBeforeTrackingExecution
    public TaskDto taskById(Long idTask){
        return taskMapper.convertEntityToDto(taskRepository.findById(idTask)
                .orElseThrow(EntityNotFoundException::new));
    }

    @LoggingThrowTrackingExecution
    public TaskDto updateTask(Long taskId, TaskDto taskDTO){
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(EntityNotFoundException::new);

        existingTask.setTitle(taskDTO.getTitle());
        existingTask.setDescription(taskDTO.getDescription());
        existingTask.setStatusName(taskDTO.getStatusName());
        existingTask.setIdUser(taskDTO.getIdUser());

        Task saved = taskRepository.save(existingTask);
        return taskMapper.convertEntityToDto(saved);
    }

    @LoggingThrowTrackingExecution
    public TaskDto createTask(TaskDto taskDTO) {
        Task task = taskMapper.convertDtoToEntity(taskDTO);
        return taskMapper.convertEntityToDto(taskRepository.save(task));
    }


    @LoggingReturnTrackingExecution
    public boolean deleteTaskById(Long idTask) {
        if (taskRepository.existsById(idTask)){
            taskRepository.deleteById(idTask);
            return true;
        } else {
            return false;
        }
    }

    private void processTaskStatusUpdate(Task task, String statusName, List<TaskDto> updatedTasks, List<TaskException> failedTasks) {
        TaskDto taskDto = taskMapper.convertEntityToDto(task);
        try {
            TaskStatusEnum oldStatusName = task.getStatusName();
            if (Objects.equals(oldStatusName.name(),statusName)){
                return;
            }
            taskDto.setStatusName(TaskStatusEnum.valueOf(statusName));
            task.setStatusName(TaskStatusEnum.valueOf(statusName));
            taskRepository.save(task);
            updatedTasks.add(taskMapper.convertEntityToDto(task));
        } catch (RuntimeException ex) {
            taskDto.setStatusName(TaskStatusEnum.NONE);
            TaskException taskException = taskErrorHandler.handle(ex,"Произошла ошибка при изменении статуса.\nКод ошибки: ",taskDto);
            failedTasks.add(taskException);
        }
    }

}
