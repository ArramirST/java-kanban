package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    public static FileBackedTasksManager inMemoryTaskManager;

    @BeforeEach
    public void beforeEach() {
        inMemoryTaskManager = Managers.getDefaultClear();
    }

    @Test
    public void getIdentifierTest() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.addTask(task);
        assertEquals(1,
                inMemoryTaskManager.getIdentifier(),
                "Ошибка вызова идентификатора");
    }

    @Test
    public void identifierTest() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.addTask(task);
        assertEquals(2,
                inMemoryTaskManager.identifier(),
                "Ошибка смены идентификатора");
    }

    @Test
    public void addTaskTest() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.addTask(task);
        Task taskTest = inMemoryTaskManager.getTask(inMemoryTaskManager.getTaskKey("Домашние дела"));
        assertNotNull(taskTest, "Задача не найдена");
        assertTrue(taskTest.toString().contains("TASK','Домашние дела','NEW','Помыть посуду"),
                "Ошибка добавления задачи");
        List<String> tasks = inMemoryTaskManager.getTaskList();
        assertNotNull(tasks, "Задачи не найдены");
        assertEquals("Домашние дела",
                tasks.get(0), "Ошибка добавления задач");
        assertEquals(1, tasks.size(), "Неверное количество задач");
        inMemoryTaskManager.addTask(task);
        taskTest = inMemoryTaskManager.getTask(inMemoryTaskManager.getTaskKey("Домашние дела"));
        tasks = inMemoryTaskManager.getTaskList();
        assertTrue(taskTest.toString().contains("TASK','Домашние дела','NEW','Помыть посуду"),
                "Ошибка повторного добавления задачи");
        assertEquals(1, tasks.size(), "Неверное количество задач");
    }

    @Test
    public void addEpicTest() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.addEpic(epic);
        Task epicTest = inMemoryTaskManager.getEpic(inMemoryTaskManager.getTaskKey("Учеба"));
        assertNotNull(epicTest, "Эпик не найден");
        assertTrue(epicTest.toString().contains("EPIC','Учеба','NEW','Нужно учиться"),
                "Ошибка добавления эпика");
        List<String> epics = inMemoryTaskManager.getEpicList();
        assertNotNull(epics, "Эпики не найдены");
        assertEquals("Учеба",
                epics.get(0), "Ошибка добавления эпика");
        assertEquals(1, epics.size(), "Неверное количество эпиков");
        inMemoryTaskManager.addEpic(epic);
        epics = inMemoryTaskManager.getEpicList();
        epicTest = inMemoryTaskManager.getEpic(inMemoryTaskManager.getTaskKey("Учеба"));
        assertTrue(epicTest.toString().contains("EPIC','Учеба','NEW','Нужно учиться"),
                "Ошибка повторного добавления эпика");
        assertEquals(1, epics.size(), "Неверное количество эпиков");
    }

    @Test
    public void addSubtaskTest() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.addEpic(epic);
        Subtask subTask1 = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.addSubtask(subTask1, "Учеба");
        Task subtaskTest = inMemoryTaskManager.getSubtask(inMemoryTaskManager.getTaskKey("Спринт 3"));
        assertNotNull(subtaskTest, "Подзадача не найдена");
        assertTrue(subtaskTest.toString().contains("SUBTASK','Спринт 3','NEW','Выполнить до вечера"),
                "Ошибка добавления подзадачи");
        List<String> subtasks = inMemoryTaskManager.getSubtaskList();
        assertNotNull(subtasks, "Подзадачи не найдены");
        assertEquals("Спринт 3",
                subtasks.get(0), "Ошибка добавления подзадач");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач");
        inMemoryTaskManager.addSubtask(subTask1, "Учеба");
        subtaskTest = inMemoryTaskManager.getSubtask(inMemoryTaskManager.getTaskKey("Спринт 3"));
        subtasks = inMemoryTaskManager.getSubtaskList();
        assertTrue(subtaskTest.toString().contains("SUBTASK','Спринт 3','NEW','Выполнить до вечера"),
                "Ошибка повторного добавления подзадачи");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач");
    }

    @Test
    public void addSubtaskWithoutEpicTest() {
        Subtask subTask1 = new Subtask("Спринт 3", "Выполнить до вечера");
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> inMemoryTaskManager.addSubtask(subTask1, "Учеба")
        );
        assertEquals("Невозможно создать подзадачу без эпика", ex.getMessage(),
                "Невозможно создать подзадачу без эпика");
    }

    @Test
    public void updateTaskTest() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.updateTask(task, Status.IN_PROGRESS);
        assertEquals("Нельзя обновить задачу до ее создания",
                inMemoryTaskManager.getBugTracker(),
                "Нельзя обновить задачу до ее создания");
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.updateTask(task, Status.IN_PROGRESS);
        assertTrue(inMemoryTaskManager.getTask(inMemoryTaskManager.getTaskKey("Домашние дела")).toString()
                .contains("TASK','Домашние дела','IN_PROGRESS','Помыть посуду"), "Ошибка обновления задачи");
    }

    @Test
    public void updateEpicTest() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.updateEpic(epic, Status.IN_PROGRESS);
        assertEquals("Нельзя обновить эпик до его создания",
                inMemoryTaskManager.getBugTracker(),
                "Нельзя обновить эпик до его создания");
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.updateEpic(epic, Status.IN_PROGRESS);
        assertTrue(inMemoryTaskManager.getEpic(inMemoryTaskManager.getTaskKey("Учеба")).toString()
                .contains("EPIC','Учеба','IN_PROGRESS','Нужно учиться"), "Ошибка обновления эпика");
    }

    @Test
    public void updateSubtaskTest() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.addEpic(epic);
        Subtask subTask1 = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.updateSubtask(subTask1, Status.IN_PROGRESS);
        assertEquals("Нельзя обновить подзадачу до ее создания",
                inMemoryTaskManager.getBugTracker(),
                "Нельзя обновить подзадачу до ее создания");
        inMemoryTaskManager.addSubtask(subTask1, "Учеба");
        inMemoryTaskManager.updateSubtask(subTask1, Status.IN_PROGRESS);
        assertTrue(inMemoryTaskManager.getSubtask(inMemoryTaskManager.getTaskKey("Спринт 3")).toString()
                        .contains("SUBTASK','Спринт 3','IN_PROGRESS','Выполнить до вечера"),
                "Ошибка обновления подзадачи");
    }

    @Test
    public void removeTasksTest() {
        inMemoryTaskManager.removeTasks();
        assertEquals(0, inMemoryTaskManager.getTaskList().size(),
                "Остались задачи после удаления");
        Task task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.removeTasks();
        assertEquals(0, inMemoryTaskManager.getTaskList().size(),
                "Остались задачи после удаления");
    }

    @Test
    public void removeEpicsTest() {
        inMemoryTaskManager.removeEpics();
        assertEquals(0, inMemoryTaskManager.getEpicList().size(),
                "Остались эпики после удаления");
        assertEquals(0, inMemoryTaskManager.getSubtaskList().size(),
                "Остались подзадачи после удаления");
        Epic epic = new Epic("Учеба", "Нужно учиться");
        Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        inMemoryTaskManager.removeEpics();
        assertEquals(0, inMemoryTaskManager.getEpicList().size(),
                "Остались эпики после удаления");
        assertEquals(0, inMemoryTaskManager.getSubtaskList().size(),
                "Остались подзадачи после удаления");
    }

    @Test
    public void removeSubtasksTest() {
        inMemoryTaskManager.removeSubtasks();
        assertEquals(0, inMemoryTaskManager.getSubtaskList().size(),
                "Остались подзадачи после удаления");
        Epic epic = new Epic("Учеба", "Нужно учиться");
        Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        inMemoryTaskManager.removeSubtasks();
        assertEquals(0, inMemoryTaskManager.getEpicSubtasksList("Учеба").size(),
                "Остались подзадачи после удаления");
        assertEquals(0, inMemoryTaskManager.getSubtaskList().size(),
                "Остались подзадачи после удаления");
    }

    @Test
    public void getTaskKeyTest() {
        assertEquals(0, inMemoryTaskManager.getTaskKey("123"),
                "Ошибка поиска ID до добавления задач");
        Task task = new Task("Домашние дела", "Помыть посуду");
        Epic epic = new Epic("Учеба", "Нужно учиться");
        Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        assertEquals(0, inMemoryTaskManager.getTaskKey("123"),
                "Ошибка поиска ID отсутствующей задачи");
        assertEquals(1, inMemoryTaskManager.getTaskKey("Домашние дела"),
                "Ошибка поиска ID задачи");
        assertEquals(2, inMemoryTaskManager.getTaskKey("Учеба"),
                "Ошибка поиска ID эпика");
        assertEquals(3, inMemoryTaskManager.getTaskKey("Спринт 3"),
                "Ошибка поиска ID подзадачи");
    }

    @Test
    public void getTaskTest() {
        assertNull(inMemoryTaskManager.getTask(-1),
                "Ошибка получения задачи по неверному ID или в пустом списке");
        Task task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.addTask(task);
        assertTrue(inMemoryTaskManager.getTask(1).toString()
                .contains("TASK','Домашние дела','NEW','Помыть посуду"), "Ошибка получения задачи");
        assertNull(inMemoryTaskManager.getTask(0),
                "Ошибка получения задачи по неверному ID");
    }

    @Test
    public void getEpicTest() {
        assertNull(inMemoryTaskManager.getEpic(-1),
                "Ошибка получения эпика по неверному ID или в пустом списке");
        Epic epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.addEpic(epic);
        assertTrue(inMemoryTaskManager.getEpic(1).toString().contains("EPIC','Учеба','NEW','Нужно учиться"),
                "Ошибка получения эпика");
        assertNull(inMemoryTaskManager.getEpic(0),
                "Ошибка получения эпика по неверному ID");
    }

    @Test
    public void getSubtaskTest() {
        assertNull(inMemoryTaskManager.getSubtask(-1),
                "Ошибка получения подзадачи по неверному ID или в пустом списке");
        Epic epic = new Epic("Учеба", "Нужно учиться");
        Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        assertTrue(inMemoryTaskManager.getSubtask(2).toString()
                .contains("SUBTASK','Спринт 3','NEW','Выполнить до вечера"), "Ошибка получения подзадачи");
        assertNull(inMemoryTaskManager.getSubtask(0),
                "Ошибка получения подзадачи по неверному ID");
    }

    @Test
    public void removeTaskTest() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.removeTask(0);
        assertTrue(inMemoryTaskManager.getTask(1).toString()
                .contains("TASK','Домашние дела','NEW','Помыть посуду"), "Удалена неправильная задача");
        inMemoryTaskManager.removeTask(1);
        assertEquals(0,
                inMemoryTaskManager.getTaskList().size(),
                "Ошибка удаления задачи");
    }

    @Test
    public void removeEpicTest() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.removeEpic(0);
        assertTrue(inMemoryTaskManager.getEpic(1).toString().contains("EPIC','Учеба','NEW','Нужно учиться"),
                "Удален неправильный эпик");
        inMemoryTaskManager.removeEpic(1);
        assertEquals(0,
                inMemoryTaskManager.getTaskList().size(),
                "Ошибка удаления эпика");
    }

    @Test
    public void removeSubtaskTest() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        inMemoryTaskManager.removeSubtask(1);
        assertTrue(inMemoryTaskManager.getSubtask(2).toString()
                .contains("SUBTASK','Спринт 3','NEW','Выполнить до вечера"), "Удалена неправильная задача");
        inMemoryTaskManager.removeSubtask(2);
        assertEquals(0,
                inMemoryTaskManager.getSubtaskList().size(),
                "Ошибка удаления задачи");
    }

    @Test
    public void getEpicSubtasksListTest() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
        Subtask subTask1 = new Subtask("Спринт 4", "Выполнить до конца недели");
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        assertTrue(inMemoryTaskManager.getEpicSubtasksList("Учеба").toString()
                .contains("[Спринт 3]"), "Ошибка получения подзадачи из эпика");
        inMemoryTaskManager.addSubtask(subTask1, "Учеба");
        assertTrue(inMemoryTaskManager.getEpicSubtasksList("Учеба").toString()
                .contains("[Спринт 3, Спринт 4]"), "Ошибка получения подзадачи из эпика");
    }

    @Test
    public void getHistoryTest() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        Epic epic = new Epic("Учеба", "Нужно учиться");
        Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getEpic(2);
        inMemoryTaskManager.getSubtask(3);
        assertTrue(inMemoryTaskManager.getHistory().toString()
                        .contains("EPIC','Учеба','NEW','Нужно учиться")
                        & inMemoryTaskManager.getHistory().toString().contains("SUBTASK','Спринт 3"),
                "Ошибка чтения истории");
    }

    @Test
    public void setTaskDateTest() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.addTask(task);
        LocalDateTime localDateTime = LocalDateTime.of(2000, 1, 1, 0, 0);
        inMemoryTaskManager.setTaskDate(1, localDateTime, 10);
        assertEquals(inMemoryTaskManager.tasks.get(1).getEndTime(), localDateTime.plus(Duration.ofMinutes(10)),
                "Ошибка передачи времени");
    }

    @Test
    public void setSubtaskDateTest() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
        Subtask subTask1 = new Subtask("Спринт 4", "Выполнить до конца недели");
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        inMemoryTaskManager.addSubtask(subTask1, "Учеба");
        LocalDateTime localDateTime = LocalDateTime.of(2000, 1, 1, 0, 0);
        inMemoryTaskManager.setSubtaskDate(2, localDateTime, 10);
        inMemoryTaskManager.setSubtaskDate(3, localDateTime.plus(Duration.ofMinutes(10)), 20);
        assertEquals(inMemoryTaskManager.epics.get(1).getEndTime(), localDateTime.plus(Duration.ofMinutes(30)),
                "Ошибка передачи времени");
        assertEquals(inMemoryTaskManager.epics.get(1).getStartTime(), localDateTime,
                "Ошибка передачи времени");
    }
}