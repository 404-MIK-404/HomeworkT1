package org.mik.springhomeworkaop.task.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mik.springhomeworkaop.task.enums.TaskStatusEnum;

@Getter
@Setter
@Entity
@Table(name = "task")
@AllArgsConstructor
@NoArgsConstructor
public class Task {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatusEnum statusName;

    @Column(name = "userid")
    private Long idUser;

}
