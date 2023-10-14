package com.yandex.test.model;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.service.FileBackedTasksManager;
import com.yandex.app.service.Managers;
import com.yandex.app.service.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SubtaskTest {

    public static FileBackedTasksManager inMemoryTaskManager;
    public static Epic epic;
    public static Subtask subTask1;

    @BeforeEach
    public void beforeEach() {
        inMemoryTaskManager = Managers.getDefaultClear();
        epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.addEpic(epic);
        subTask1 = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.addSubtask(subTask1, "Учеба");
    }

    @Test
    public void newSubtaskTest() {
        assertTrue(inMemoryTaskManager.getSubtask(inMemoryTaskManager.getTaskKey("Спринт 3")).toString()
                .contains("SUBTASK','Спринт 3','NEW','Выполнить до вечера"), "Ошибка новой подзадачи");
    }

    @Test
    public void inProgressSubtaskTest() {
        inMemoryTaskManager.updateSubtask(subTask1, Status.IN_PROGRESS);
        assertTrue(inMemoryTaskManager.getSubtask(inMemoryTaskManager.getTaskKey("Спринт 3")).toString()
                        .contains("SUBTASK','Спринт 3','IN_PROGRESS','Выполнить до вечера"),
                "Ошибка подзадачи в процессе выполнения");
    }

    @Test
    public void doneSubtaskTest() {
        inMemoryTaskManager.updateSubtask(subTask1, Status.DONE);
        assertTrue(inMemoryTaskManager.getSubtask(inMemoryTaskManager.getTaskKey("Спринт 3")).toString()
                .contains("SUBTASK','Спринт 3','DONE','Выполнить до вечера"), "Ошибка выполненной подзадачи");
    }
}