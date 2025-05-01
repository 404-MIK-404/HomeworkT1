package org.mik.springhomeworkaop.task.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mik.springhomeworkaop.task.model.dto.TaskDto;
import org.mik.springhomeworkaop.task.model.entity.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {




    @Mapping(target = "id",source = "task.id")
    @Mapping(target = "title",source = "task.title")
    @Mapping(target = "description",source = "task.description")
    @Mapping(target = "statusName",source = "task.statusName")
    @Mapping(target = "idUser",source = "task.idUser")

    TaskDto convertEntityToDto(Task task);



    @Mapping(target = "id",source = "taskDTO.id")
    @Mapping(target = "title",source = "taskDTO.title")
    @Mapping(target = "description",source = "taskDTO.description")
    @Mapping(target = "statusName",source = "taskDTO.statusName")
    @Mapping(target = "idUser",source = "taskDTO.idUser")
    Task convertDtoToEntity(TaskDto taskDTO);

}
