package com.yandex.app.model;

import com.yandex.app.service.FileBackedTasksManager;
import com.yandex.app.service.Managers;
import com.yandex.app.service.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {

    public static FileBackedTasksManager inMemoryTaskManager;
    public static Task task;

    @BeforeEach
    public void beforeEach() {
        inMemoryTaskManager = Managers.getDefaultClear();
        task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.addTask(task);
    }

    @Test
    public void newTaskTest() {
        assertTrue(inMemoryTaskManager.getTask(inMemoryTaskManager.getTaskKey("Домашние дела")).toString()
                .contains("TASK','Домашние дела','NEW','Помыть посуду"), "Ошибка новой задачи");
    }

    @Test
    public void inProgressTaskTest() {
        inMemoryTaskManager.updateTask(task, Status.IN_PROGRESS);
        assertTrue(inMemoryTaskManager.getTask(inMemoryTaskManager.getTaskKey("Домашние дела")).toString()
                        .contains("TASK','Домашние дела','IN_PROGRESS','Помыть посуду"),
                "Ошибка задачи в процессе выполнения");
    }

    @Test
    public void doneTaskTest() {
        inMemoryTaskManager.updateTask(task, Status.DONE);
        assertTrue(inMemoryTaskManager.getTask(inMemoryTaskManager.getTaskKey("Домашние дела")).toString()
                        .contains("TASK','Домашние дела','DONE','Помыть посуду"),
                "Ошибка выполненной задачи");
    }
}