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
    void shouldReturnHistory() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.historyManager.add(task);
        inMemoryTaskManager.historyManager.getHistory();
        assertTrue(inMemoryTaskManager.historyManager.getHistory().toString()
                .contains("TASK','Домашние дела','NEW','Помыть посуду"), "Не добавляется задача в историю");
    }

    @Test
    void shouldDeleteTaskFromHistory() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        Epic epic = new Epic("Учеба", "Нужно учиться");
        Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getEpic(2);
        inMemoryTaskManager.getSubtask(3);
        inMemoryTaskManager.historyManager.remove(1);
        assertEquals(2,
                inMemoryTaskManager.historyManager.getHistory().size(),
                "Не удалилась задача из истории");
        assertTrue(inMemoryTaskManager.historyManager.getHistory().toString()
                        .contains("EPIC','Учеба','NEW','Нужно учиться")
                        && inMemoryTaskManager.historyManager.getHistory().toString()
                        .contains("SUBTASK','Спринт 3','NEW','Выполнить до вечера"),
                "Удалилась не та задача из истории");
    }

    @Test
    void shouldDeleteEpicFromHistory() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        Epic epic = new Epic("Учеба", "Нужно учиться");
        Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getEpic(2);
        inMemoryTaskManager.getSubtask(3);
        inMemoryTaskManager.historyManager.remove(3);
        assertEquals(2,
                inMemoryTaskManager.historyManager.getHistory().size(),
                "Не удалился эпик из истории");
        assertTrue(inMemoryTaskManager.historyManager.getHistory().toString()
                        .contains("EPIC','Учеба','NEW','Нужно учиться")
                        && inMemoryTaskManager.historyManager.getHistory().toString()
                        .contains("TASK','Домашние дела','NEW','Помыть посуду"),
                "Удалилась не та задача из истории");
    }

    @Test
    void shouldDeleteSubtaskFromHistory() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        Epic epic = new Epic("Учеба", "Нужно учиться");
        Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getEpic(2);
        inMemoryTaskManager.getSubtask(3);
        inMemoryTaskManager.historyManager.remove(2);
        assertEquals(2,
                inMemoryTaskManager.historyManager.getHistory().size(),
                "Не удалилась подзадача из истории");
        assertTrue(inMemoryTaskManager.historyManager.getHistory().toString()
                        .contains("TASK','Домашние дела','NEW','Помыть посуду")
                        && inMemoryTaskManager.historyManager.getHistory().toString()
                        .contains("SUBTASK','Спринт 3','NEW','Выполнить до вечера"),
                "Удалилась не та задача из истории");
    }

    @Test
    void shouldReturnEmptyHistory() {
        assertEquals("[]",
                inMemoryTaskManager.historyManager.getHistory().toString(),
                "Пустая история считывается как содержащая элементы");
    }
}