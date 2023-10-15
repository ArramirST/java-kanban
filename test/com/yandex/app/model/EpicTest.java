package com.yandex.app.model;

import com.yandex.app.service.FileBackedTasksManager;
import com.yandex.app.service.Managers;
import com.yandex.app.service.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EpicTest {
    public static FileBackedTasksManager inMemoryTaskManager;
    public static Epic epic;

    @BeforeEach
    public void beforeEach() {
        inMemoryTaskManager = Managers.getDefaultClear();
        epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.addEpic(epic);
    }

    @Test
    public void emptySubtaskEpicTest() {
        assertTrue(inMemoryTaskManager.getEpic(inMemoryTaskManager.getTaskKey("Учеба")).toString()
                .contains("EPIC','Учеба','NEW','Нужно учиться"), "Ошибка эпика без подзадач");
    }

    @Test
    public void newSubtaskEpicTest() {
        Subtask subTask1 = new Subtask("Спринт 3", "Выполнить до вечера");
        Subtask subTask2 = new Subtask("Спринт 4", "Выполнить до конца недели");
        inMemoryTaskManager.addSubtask(subTask1, "Учеба");
        inMemoryTaskManager.addSubtask(subTask2, "Учеба");
        assertTrue(inMemoryTaskManager.getEpic(inMemoryTaskManager.getTaskKey("Учеба")).toString()
                .contains("EPIC','Учеба','NEW','Нужно учиться"), "Ошибка эпика с новыми подзадачами");
    }

    @Test
    public void inProgressSubtaskEpicTest() {
        Subtask subTask1 = new Subtask("Спринт 3", "Выполнить до вечера");
        Subtask subTask2 = new Subtask("Спринт 4", "Выполнить до конца недели");
        inMemoryTaskManager.addSubtask(subTask1, "Учеба");
        inMemoryTaskManager.addSubtask(subTask2, "Учеба");
        inMemoryTaskManager.updateSubtask(subTask1, Status.IN_PROGRESS);
        assertTrue(inMemoryTaskManager.getEpic(inMemoryTaskManager.getTaskKey("Учеба")).toString()
                        .contains("EPIC','Учеба','IN_PROGRESS','Нужно учиться"),
                "Ошибка эпика с подзадачами в процессе выполнения");
    }

    @Test
    public void doneSubtaskEpicTest() {
        Subtask subTask1 = new Subtask("Спринт 3", "Выполнить до вечера");
        Subtask subTask2 = new Subtask("Спринт 4", "Выполнить до конца недели");
        inMemoryTaskManager.addSubtask(subTask1, "Учеба");
        inMemoryTaskManager.addSubtask(subTask2, "Учеба");
        inMemoryTaskManager.updateSubtask(subTask1, Status.DONE);
        inMemoryTaskManager.updateSubtask(subTask2, Status.DONE);
        assertTrue(inMemoryTaskManager.getEpic(inMemoryTaskManager.getTaskKey("Учеба")).toString()
                        .contains("EPIC','Учеба','DONE','Нужно учиться"),
                "Ошибка эпика с выполненными подзадачами");
    }

    @Test
    public void newAndDoneSubtaskEpicTest() {
        Subtask subTask1 = new Subtask("Спринт 3", "Выполнить до вечера");
        Subtask subTask2 = new Subtask("Спринт 4", "Выполнить до конца недели");
        inMemoryTaskManager.addSubtask(subTask1, "Учеба");
        inMemoryTaskManager.addSubtask(subTask2, "Учеба");
        inMemoryTaskManager.updateSubtask(subTask1, Status.DONE);
        assertTrue(inMemoryTaskManager.getEpic(inMemoryTaskManager.getTaskKey("Учеба")).toString()
                        .contains("EPIC','Учеба','IN_PROGRESS','Нужно учиться"),
                "Ошибка эпика с подзадачами разного типа");
    }
}