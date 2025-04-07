package org.mik.springhomeworkaop.task.model.dto;


import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {

    private Long id;

    private String title;

    private String description;

    private String statusName;

    private Long idUser;

}
