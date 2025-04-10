package org.mik.springhomeworkaop.task.mapper;


import org.mapstruct.Mapper;
import org.mik.springhomeworkaop.task.model.dto.TaskDto;
import org.mik.springhomeworkaop.task.model.entity.Task;

import java.util.List;

@Mapper(componentModel = "spring",uses = TaskMapper.class)
public interface TaskListMapper {

    List<TaskDto> convertListEntityToListDto(List<Task> listTask);

    List<Task> convertListDtoToListEntity(List<TaskDto> listTaskDto);

}
