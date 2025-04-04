package org.mik.springhomeworkaop.task.mapper;


import org.mapstruct.Mapper;
import org.mik.springhomeworkaop.task.model.dto.TaskDTO;
import org.mik.springhomeworkaop.task.model.entity.Task;

import java.util.List;

@Mapper(componentModel = "spring",uses = TaskMapper.class)
public interface TaskListMapper {

    List<TaskDTO> convertListEntityToListDTO(List<Task> listTask);

    List<Task> convertListDtoToListEntity(List<TaskDTO> listTaskDTO);

}
