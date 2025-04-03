package org.mik.springhomeworkaop.task.aspect.exception;

public class TaskLoggingAspectException extends RuntimeException {
    public TaskLoggingAspectException(String message) {
        super(message);
    }
}
