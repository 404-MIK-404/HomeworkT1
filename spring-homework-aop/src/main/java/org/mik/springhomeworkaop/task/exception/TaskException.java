package org.mik.springhomeworkaop.task.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.mik.springhomeworkaop.task.model.dto.TaskDto;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class TaskException extends RuntimeException  {


    private String title;

    private String message;

    private TaskDto taskDto;

}
