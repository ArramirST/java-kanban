package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HistoryManagerTest {
    public static FileBackedTasksManager inMemoryTaskManager;

    @BeforeEach
    public void beforeEach() {
        inMemoryTaskManager = Managers.getDefaultClear();
    }

    @Test
    void addTest() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.historyManager.add(task);
        inMemoryTaskManager.historyManager.getHistory();
        assertTrue(inMemoryTaskManager.historyManager.getHistory().toString()
                .contains("TASK','Домашние дела','NEW','Помыть посуду"), "Ошибка добавления истории");
        inMemoryTaskManager.historyManager.add(task);
        assertTrue(inMemoryTaskManager.historyManager.getHistory().toString()
                .contains("TASK','Домашние дела','NEW','Помыть посуду"), "Ошибка добавления историю");
    }

    @Test
    void removeTest() {
        inMemoryTaskManager.historyManager.remove(1);
        assertEquals(0,
                inMemoryTaskManager.historyManager.getHistory().size(),
                "Ошибка удаления пустой истории");
        Task task = new Task("Домашние дела", "Помыть посуду");
        Epic epic = new Epic("Учеба", "Нужно учиться");
        Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getEpic(2);
        inMemoryTaskManager.getSubtask(3);
        inMemoryTaskManager.historyManager.remove(0);
        assertEquals(3,
                inMemoryTaskManager.historyManager.getHistory().size(),
                "Ошибка удаления истории");
        inMemoryTaskManager.historyManager.remove(1);
        assertEquals(2,
                inMemoryTaskManager.historyManager.getHistory().size(),
                "Ошибка удаления истории");
        assertTrue(inMemoryTaskManager.historyManager.getHistory().toString()
                .contains("EPIC','Учеба','NEW','Нужно учиться")
                & inMemoryTaskManager.historyManager.getHistory().toString()
                .contains("SUBTASK','Спринт 3','NEW','Выполнить до вечера"), "Ошибка удаления истории");
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.historyManager.remove(3);
        assertEquals(2,
                inMemoryTaskManager.historyManager.getHistory().size(),
                "Ошибка удаления истории");
        assertTrue(inMemoryTaskManager.historyManager.getHistory().toString()
                .contains("EPIC','Учеба','NEW','Нужно учиться")
                & inMemoryTaskManager.historyManager.getHistory().toString()
                .contains("TASK','Домашние дела','NEW','Помыть посуду"), "Ошибка удаления истории");
        inMemoryTaskManager.getSubtask(3);
        inMemoryTaskManager.historyManager.remove(2);
        assertEquals(2,
                inMemoryTaskManager.historyManager.getHistory().size(),
                "Ошибка удаления истории");
        assertTrue(inMemoryTaskManager.historyManager.getHistory().toString()
                .contains("TASK','Домашние дела','NEW','Помыть посуду")
                & inMemoryTaskManager.historyManager.getHistory().toString()
                .contains("SUBTASK','Спринт 3','NEW','Выполнить до вечера"), "Ошибка удаления истории");
    }

    @Test
    void getHistoryTest() {
        assertEquals("[]",
                inMemoryTaskManager.historyManager.getHistory().toString(),
                "Ошибка чтения пустой истории");
    }
}