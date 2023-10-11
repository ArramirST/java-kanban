package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTasksManagerTest {

    @Test
    public void emptyTaskHistoryTest() {
        FileBackedTasksManager inMemoryTaskManager = Managers.getDefaultClear();
        FileBackedTasksManager inMemoryTaskManager1 = Managers.getDefault();
        assertEquals("[]",
                inMemoryTaskManager1.getHistory().toString(),
                "Ошибка чтения пустой истории");
    }

    @Test
    public void epicHistoryTest() {
        FileBackedTasksManager inMemoryTaskManager = Managers.getDefaultClear();
        Epic epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.getEpic(1);
        FileBackedTasksManager inMemoryTaskManager1 = Managers.getDefault();
        assertTrue(inMemoryTaskManager1.getHistory().toString()
                .contains("EPIC','Учеба','NEW','Нужно учиться"), "Ошибка чтения истории");
    }

    @Test
    public void clearHistoryTest() {
        FileBackedTasksManager inMemoryTaskManager = Managers.getDefaultClear();
        FileBackedTasksManager inMemoryTaskManager1 = Managers.getDefault();
        Task task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.historyManager.remove(1);
        assertEquals("[]",
                inMemoryTaskManager1.getHistory().toString(),
                "Ошибка чтения пустой истории");
    }
}