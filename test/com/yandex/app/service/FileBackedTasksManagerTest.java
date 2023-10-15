package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTasksManagerTest {
    public static FileBackedTasksManager inMemoryTaskManager;

    @BeforeEach
    public void beforeEach() {
        inMemoryTaskManager = Managers.getDefaultClear();
    }

    @Test
    public void shouldLoadEmptyMemory() {
        FileBackedTasksManager inMemoryTaskManager1 = Managers.getDefault();
        assertEquals("[]",
                inMemoryTaskManager1.getHistory().toString(),
                "В чистой истории присутствуют посторонние задачи");
    }

    @Test
    public void shouldLoadEpic() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.getEpic(1);
        FileBackedTasksManager inMemoryTaskManager1 = Managers.getDefault();
        assertTrue(inMemoryTaskManager1.getHistory().toString()
                .contains("EPIC','Учеба','NEW','Нужно учиться"), "Эпик не загружается из истории");
    }

    @Test
    public void shouldLoadEmptyMemoryAfterClear() {
        FileBackedTasksManager inMemoryTaskManager1 = Managers.getDefault();
        Task task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.historyManager.remove(1);
        assertEquals("[]",
                inMemoryTaskManager1.getHistory().toString(),
                "Функция удаления задач из истории не работает");
    }
}