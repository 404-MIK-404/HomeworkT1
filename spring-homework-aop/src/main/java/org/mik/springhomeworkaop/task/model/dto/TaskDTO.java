package org.mik.springhomeworkaop.task.model.dto;


import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {

    private Long id;

    private String title;

    private String description;

    private Long idUser;

}
