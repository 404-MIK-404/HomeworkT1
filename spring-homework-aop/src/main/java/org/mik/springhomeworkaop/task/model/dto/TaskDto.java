package org.mik.springhomeworkaop.task.model.dto;


import lombok.*;
import org.mik.springhomeworkaop.task.enums.TaskStatusEnum;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    private Long id;

    private String title;

    private String description;

    private TaskStatusEnum statusName;

    private Long idUser;

}
