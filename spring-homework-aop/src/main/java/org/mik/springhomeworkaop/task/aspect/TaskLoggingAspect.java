package org.mik.springhomeworkaop.task.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.mik.springhomeworkaop.task.aspect.exception.TaskLoggingAspectException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Slf4j
@Component
public class TaskLoggingAspect {

    @Before(value = "@annotation(org.mik.springhomeworkaop.task.aspect.annotation.TaskLoggingExecution)")
    public void loggingTaskBefore(JoinPoint joinPoint){
        log.info("Вызов метода: " + joinPoint.getSignature().getName());
        log.info("Параметры метода: " + Arrays.asList(joinPoint.getArgs()));
    }

    @Around(value = "@annotation(org.mik.springhomeworkaop.task.aspect.annotation.TaskLoggingTracking)")
    public Object loggingTrackingTaskAround(ProceedingJoinPoint joinPoint) {
        long startProceeded = System.currentTimeMillis();
        Object proceeded;
        try {
            proceeded = joinPoint.proceed();
            long endProceeded = System.currentTimeMillis();
            long timeProceeded = endProceeded - startProceeded;
            log.info("Выполнение метода: " + joinPoint.getSignature().getName());
            log.info("Параметры метода: " + Arrays.asList(joinPoint.getArgs()));
            log.info("Время выполнения метода: " + timeProceeded + " миллисекунд");
            return proceeded;
        } catch (Throwable throwable){
            log.error("Произошла ошибка в выполнение метода: " + joinPoint.getSignature().getName());
            log.error("Параметры метода: " + Arrays.asList(joinPoint.getArgs()));
            log.error("StackTrace ошибки: " + Arrays.asList(throwable.getStackTrace()));
            throw new TaskLoggingAspectException("Ошибка в аспект классе.");
        }
    }


    @AfterThrowing(
            pointcut = "@annotation(org.mik.springhomeworkaop.task.aspect.annotation.TaskLoggingThrowingExecution)",
            throwing = "throwable"
    )
    public void loggingTaskAfterThrowing(JoinPoint joinPoint, Throwable throwable){
        log.error("Ошибка в работе метода: " + joinPoint.getSignature().getName());
        log.error("Паметры метода: " + Arrays.asList(joinPoint.getArgs()));
        log.error("Код ошибки: "  + Arrays.asList(throwable.getStackTrace()));
    }

    @AfterReturning(
            pointcut = "@annotation(org.mik.springhomeworkaop.task.aspect.annotation.TaskLoggingReturnExecution)",
            returning = "deleteTaskResult"
    )
    public void loggingTaskAfterReturning(JoinPoint joinPoint,boolean deleteTaskResult) {
        log.info("Выполнение метода: " + joinPoint.getSignature().getName());
        log.info("Параметры метода: " + Arrays.asList(joinPoint.getArgs()));
        log.info("Результат выполнения метода: " + deleteTaskResult);
    }

}
