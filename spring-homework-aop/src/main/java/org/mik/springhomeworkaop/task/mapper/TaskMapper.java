package org.mik.springhomeworkaop.task.mapper;

import org.mapstruct.Mapper;
import org.mik.springhomeworkaop.task.model.dto.TaskDTO;
import org.mik.springhomeworkaop.task.model.entity.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {


    TaskDTO convertEntityToDTO(Task task);

    Task convertDtoToEntity(TaskDTO taskDTO);

}
