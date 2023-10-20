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
    /* Начальное состояние inMemoryTaskManager обеспечивается методом Managers.getDefaultClear(),
     которое создает пустой файл для загрузки. Работоспособность данного метода проверяется в
     FileBackedTasksManagerTest и HistoryManagerTest */
    public static FileBackedTasksManager inMemoryTaskManager;

    @BeforeEach
    public void beforeEach() {
        inMemoryTaskManager = Managers.getDefaultClear();
    }

    @Test
    public void shouldReturnIdentifier() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.addTask(task);
        assertEquals(1,
                inMemoryTaskManager.getIdentifier(),
                "Изменено начальное значение идентификатора");
    }

    @Test
    public void shouldChangeIdentifier() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.addTask(task);
        assertEquals(2,
                inMemoryTaskManager.identifier(),
                "Идентификатор некорретно увеличивается");
    }

    @Test
    public void shouldFindAddedTask() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.addTask(task);
        Task taskTest = inMemoryTaskManager.getTask(inMemoryTaskManager.getTaskKey("Домашние дела"));
        assertNotNull(taskTest, "Задача не добавился");
        assertTrue(taskTest.toString().contains("TASK','Домашние дела','NEW','Помыть посуду"),
                "Задача добавлена не верно");
    }

    @Test
    public void shouldNotDuplicateAddedTask() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.addTask(task);
        List<Task> tasks = inMemoryTaskManager.getTaskList();
        assertEquals(1, tasks.size(), "Возник дубликат задачи");
    }

    @Test
    public void shouldFindAddedEpic() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.addEpic(epic);
        Task epicTest = inMemoryTaskManager.getEpic(inMemoryTaskManager.getTaskKey("Учеба"));
        assertNotNull(epicTest, "Эпик не добавился");
        assertTrue(epicTest.toString().contains("EPIC','Учеба','NEW','Нужно учиться"),
                "Эпик добавлен не верно");
    }

    @Test
    public void shouldNotDuplicateAddedEpic() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addEpic(epic);
        List<Task> epics = inMemoryTaskManager.getEpicList();
        assertEquals(1, epics.size(), "Возник дубликат эпика");
    }

    @Test
    public void shouldFindAddedSubtask() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.addEpic(epic);
        Subtask subTask1 = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.addSubtask(subTask1, "Учеба");
        Task subtaskTest = inMemoryTaskManager.getSubtask(inMemoryTaskManager.getTaskKey("Спринт 3"));
        assertNotNull(subtaskTest, "Подзадача не добавилась");
        assertTrue(subtaskTest.toString().contains("SUBTASK','Спринт 3','NEW','Выполнить до вечера"),
                "Подзадача добавлена не верно");
    }

    @Test
    public void shouldNotDuplicateAddedSubtask() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.addEpic(epic);
        Subtask subTask1 = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.addSubtask(subTask1, "Учеба");
        inMemoryTaskManager.addSubtask(subTask1, "Учеба");
        List<Task> subtasks = inMemoryTaskManager.getSubtaskList();
        assertEquals(1, subtasks.size(), "Возник дубликат подзадачи");
    }

    @Test
    public void shouldThrowExceptionForAddedSubtaskWithoutEpic() {
        Subtask subTask1 = new Subtask("Спринт 3", "Выполнить до вечера");
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> inMemoryTaskManager.addSubtask(subTask1, "Учеба")
        );
        assertEquals("Невозможно создать подзадачу без эпика", ex.getMessage(),
                "Невозможно создать подзадачу без эпика");
    }

    @Test
    public void shouldThrowExceptionForUpdateNotCreatedTask() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.updateTask(task, Status.IN_PROGRESS);
        assertEquals("Нельзя обновить задачу до ее создания",
                inMemoryTaskManager.getBugTracker(),
                "Нельзя обновить задачу до ее создания");
    }

    @Test
    public void shouldUpdateTask() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.updateTask(task, Status.IN_PROGRESS);
        assertTrue(inMemoryTaskManager.getTask(inMemoryTaskManager.getTaskKey("Домашние дела")).toString()
                        .contains("TASK','Домашние дела','IN_PROGRESS','Помыть посуду"),
                "Задача не обновляется");
    }

    @Test
    public void shouldThrowExceptionForUpdateNotCreatedEpic() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.updateEpic(epic, Status.IN_PROGRESS);
        assertEquals("Нельзя обновить эпик до его создания",
                inMemoryTaskManager.getBugTracker(),
                "Нельзя обновить эпик до его создания");
    }

    @Test
    public void shouldUpdateEpic() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.updateEpic(epic, Status.IN_PROGRESS);
        assertTrue(inMemoryTaskManager.getEpic(inMemoryTaskManager.getTaskKey("Учеба")).toString()
                .contains("EPIC','Учеба','IN_PROGRESS','Нужно учиться"), "Эпик не обновляется");
    }

    @Test
    public void shouldThrowExceptionForUpdateNotCreatedSubtask() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.addEpic(epic);
        Subtask subTask1 = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.updateSubtask(subTask1, Status.IN_PROGRESS);
        assertEquals("Нельзя обновить подзадачу до ее создания",
                inMemoryTaskManager.getBugTracker(),
                "Нельзя обновить подзадачу до ее создания");
    }

    @Test
    public void shouldUpdateSubtask() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.addEpic(epic);
        Subtask subTask1 = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.addSubtask(subTask1, "Учеба");
        inMemoryTaskManager.updateSubtask(subTask1, Status.IN_PROGRESS);
        assertTrue(inMemoryTaskManager.getSubtask(inMemoryTaskManager.getTaskKey("Спринт 3")).toString()
                        .contains("SUBTASK','Спринт 3','IN_PROGRESS','Выполнить до вечера"),
                "Подзадача не обновляется");
    }

    @Test
    public void shouldRemoveTasks() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.removeTasks();
        assertEquals(0, inMemoryTaskManager.getTaskList().size(),
                "Остались задачи после удаления");
    }

    @Test
    public void shouldRemoveEpics() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.removeEpics();
        assertEquals(0, inMemoryTaskManager.getEpicList().size(),
                "Остались эпики после удаления");
    }

    @Test
    public void shouldRemoveSubtasksAttachedToEpic() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        inMemoryTaskManager.removeEpics();
        assertEquals(0, inMemoryTaskManager.getSubtaskList().size(),
                "Остались подзадачи после удаления их эпика");
    }

    @Test
    public void shouldRemoveSubtasks() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        inMemoryTaskManager.removeSubtasks();
        assertEquals(0, inMemoryTaskManager.getSubtaskList().size(),
                "Остались подзадачи после удаления");
    }

    @Test
    public void shouldRemoveSubtasksFromEpicMemory() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        inMemoryTaskManager.removeSubtasks();
        assertEquals(0, inMemoryTaskManager.getEpicSubtasksList("Учеба").size(),
                "В данных эпика присутствуют удаленные подзадачи");
    }

    @Test
    public void shouldReturnTaskID() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.addTask(task);
        assertEquals(1, inMemoryTaskManager.getTaskKey("Домашние дела"),
                "Ошибка поиска ID задачи");
    }

    @Test
    public void shouldReturnEpicID() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        Epic epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.addEpic(epic);
        assertEquals(2, inMemoryTaskManager.getTaskKey("Учеба"),
                "Ошибка поиска ID эпика");
    }

    @Test
    public void shouldReturnSubtaskID() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        Epic epic = new Epic("Учеба", "Нужно учиться");
        Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        assertEquals(3, inMemoryTaskManager.getTaskKey("Спринт 3"),
                "Ошибка поиска ID подзадачи");
    }

    @Test
    public void shouldReturnEmptyTasksList() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.removeTask(1);
        assertEquals(0,
                inMemoryTaskManager.getTaskList().size(),
                "Задача не удаляется");
    }

    @Test
    public void shouldReturnEmptyEpicsList() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.removeEpic(1);
        assertEquals(0,
                inMemoryTaskManager.getTaskList().size(),
                "Эпик не удаляется");
    }

    @Test
    public void shouldReturnEmptySubtasksList() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        inMemoryTaskManager.removeSubtask(2);
        assertEquals(0,
                inMemoryTaskManager.getSubtaskList().size(),
                "Подзадача не удаляется");
    }

    @Test
    public void shouldReturnAttachedToEpicSubtasksList() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
        Subtask subTask1 = new Subtask("Спринт 4", "Выполнить до конца недели");
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        inMemoryTaskManager.addSubtask(subTask1, "Учеба");
        assertTrue(inMemoryTaskManager.getEpicSubtasksList("Учеба").toString()
                .contains("[Спринт 3, Спринт 4]"), "Ошибка получения подзадачи из эпика");
    }

    @Test
    public void shouldAddTimeAndDateToTask() {
        Task task = new Task("Домашние дела", "Помыть посуду");
        inMemoryTaskManager.addTask(task);
        LocalDateTime localDateTime = LocalDateTime.of(2000, 1, 1, 0, 0);
        inMemoryTaskManager.setTaskDate(1, localDateTime, 10);
        assertEquals(inMemoryTaskManager.tasks.get(1).getEndTime(), localDateTime.plus(Duration.ofMinutes(10)),
                "Некорректная передача времени задаче");
    }

    @Test
    public void shouldReturnTimeAndDateToEpic() {
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
                "Неверный подсчет окончания эпика");
    }

    @Test
    public void shouldThrowExceptionForAddedOverlapTime() {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
        Subtask subTask1 = new Subtask("Спринт 4", "Выполнить до конца недели");
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        inMemoryTaskManager.addSubtask(subTask1, "Учеба");
        LocalDateTime localDateTime = LocalDateTime.of(2000, 1, 1, 0, 0);
        inMemoryTaskManager.setSubtaskDate(2, localDateTime, 70);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> inMemoryTaskManager.setSubtaskDate(3, localDateTime.plus(Duration.ofMinutes(10)), 1000)
        );
        assertEquals("Время выполнения задач пересекается", ex.getMessage(),
                "Не обработано пересечение времени задач/подзадач");
    }
}