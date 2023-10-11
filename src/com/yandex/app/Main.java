package com.yandex.app;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.FileBackedTasksManager;
import com.yandex.app.service.Managers;
import com.yandex.app.service.Status;
import com.yandex.app.service.TaskManager;

import java.time.LocalDateTime;
import java.util.Scanner;

public class Main {
    public static FileBackedTasksManager inMemoryTaskManager = Managers.getDefault();
    public static Scanner scanner = new Scanner(System.in);
    public static Epic epic = new Epic("Учеба", "Нужно учиться");
    public static Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
    public static Task task = new Task("Домашние дела", "Помыть посуду");

    public static void main(String[] args) {

        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        subTask = new Subtask("Спринт 4", "Выполнить до конца недели");
        int choice;
        while (true) {
            printMenu();
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    getAllTasks();
                    break;
                case 2:
                    removeAllTasks();
                    break;
                case 3:
                    getTasksById();
                    break;
                case 4:
                    createTask();
                    break;
                case 5:
                    updateTasks();
                    break;
                case 6:
                    removeTaskById();
                    break;
                case 7:
                    subtasksCheck();
                    break;
                case 8:
                    historyTest();
                    break;
                case 9:
                    loadTest();
                    break;
                case 10:
                    timeTest();
                    break;
                case 11:
                    sortedListTest();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Такой комманды нет");
            }
            if (choice==0) {
                break;
            }
        }
    }

    public static void printMenu() {
        System.out.println("Введите комманду:");
        System.out.println("1 - Получение списка всех задач");
        System.out.println("2 - Удаление всех задач");
        System.out.println("3 - Получение по идентификатору");
        System.out.println("4 - Создание задачи");
        System.out.println("5 - Обновление задачи");
        System.out.println("6 - Удаление по идентификатору");
        System.out.println("7 - Получение всех списков задач отдельного Epic");
        System.out.println("8 - Проверить работу истории доступа");
        System.out.println("9 - Проверить возможность загрузки из сохранения");
        System.out.println("10 - Проверить возможность установки дат");
        System.out.println("11 - Получить сортированный список задач");
        System.out.println("0 - Завершение работы");
    }

    public static void getAllTasks() {
        System.out.println(inMemoryTaskManager.getTaskList());
        System.out.println(inMemoryTaskManager.getEpicList());
        System.out.println(inMemoryTaskManager.getSubtaskList());
    }

    public static void removeAllTasks() {
        inMemoryTaskManager.removeTasks();
        inMemoryTaskManager.removeEpics();
        inMemoryTaskManager.removeSubtasks();
    }

    public static void getTasksById() {
        System.out.println(inMemoryTaskManager.getEpic(inMemoryTaskManager.getTaskKey("Учеба")));
        System.out.println(inMemoryTaskManager.getTask(inMemoryTaskManager.getTaskKey("Домашние дела")));
        System.out.println(inMemoryTaskManager.getSubtask(inMemoryTaskManager.getTaskKey("Спринт 3")));
        System.out.println(inMemoryTaskManager.getSubtask(inMemoryTaskManager.getTaskKey("Спринт 4")));
        System.out.println(inMemoryTaskManager.getHistory());
    }

    public static void createTask() {
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        inMemoryTaskManager.addTask(task);
    }

    public static void updateTasks() {
        inMemoryTaskManager.updateEpic(epic, Status.IN_PROGRESS);
        inMemoryTaskManager.updateTask(task, Status.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subTask, Status.IN_PROGRESS);
    }

    public static void removeTaskById() {
        inMemoryTaskManager.removeSubtask(inMemoryTaskManager.getTaskKey("Спринт 3"));
        inMemoryTaskManager.removeEpic(inMemoryTaskManager.getTaskKey("Учеба"));
        inMemoryTaskManager.removeTask(inMemoryTaskManager.getTaskKey("Домашние дела"));
    }

    public static void subtasksCheck() {
        System.out.println(inMemoryTaskManager.getEpicSubtasksList("Учеба"));
    }

    public static void historyTest() {
        System.out.println("История до добавления задач: " + inMemoryTaskManager.getHistory());
        Task task1 = new Task("Прогуляться", "Выйти на улицу");
        Task task2 = new Task("Позвонить родителям", "Пообщаться");
        Epic epic1 = new Epic("Сходить в магазин, за свежими продуктами", "Купить продукты");
        Epic epic2 = new Epic("Приготовить ужин", "Нужны продукты");
        Subtask subTask1 = new Subtask("Купить мясо", "В мясном отделе");
        Subtask subTask2 = new Subtask("Купить молоко", "В молочном отделе");
        Subtask subTask3 = new Subtask("Купить макарон", "В отделе с крупами");
        inMemoryTaskManager.addTask(task1);
        inMemoryTaskManager.addTask(task2);
        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.addEpic(epic2);
        inMemoryTaskManager.addSubtask(subTask1, "Сходить в магазин, за свежими продуктами");
        inMemoryTaskManager.addSubtask(subTask2, "Сходить в магазин, за свежими продуктами");
        inMemoryTaskManager.addSubtask(subTask3, "Сходить в магазин, за свежими продуктами");
        inMemoryTaskManager.getEpic(inMemoryTaskManager.getTaskKey("Сходить в магазин, за свежими продуктами"));
        inMemoryTaskManager.getTask(inMemoryTaskManager.getTaskKey("Прогуляться"));
        inMemoryTaskManager.getTask(inMemoryTaskManager.getTaskKey("Позвонить родителям"));
        inMemoryTaskManager.getSubtask(inMemoryTaskManager.getTaskKey("Купить мясо"));
        inMemoryTaskManager.getSubtask(inMemoryTaskManager.getTaskKey("Купить молоко"));
        inMemoryTaskManager.getEpic(inMemoryTaskManager.getTaskKey("Сходить в магазин"));
        inMemoryTaskManager.getEpic(inMemoryTaskManager.getTaskKey("Приготовить ужин"));
        System.out.println("История после добавления задач: " + inMemoryTaskManager.getHistory());
        inMemoryTaskManager.removeSubtask(inMemoryTaskManager.getTaskKey("Купить мясо"));
        System.out.println("История после удаления подзадачи: " + inMemoryTaskManager.getHistory());
        inMemoryTaskManager.removeEpic(inMemoryTaskManager.getTaskKey("Сходить в магазин, за свежими продуктами"));
        System.out.println("История после удаления эпика: " + inMemoryTaskManager.getHistory());
    }

    public static void timeTest() {
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.setTaskDate(inMemoryTaskManager.getTaskKey("Домашние дела"),
                LocalDateTime.now(), 10);
        System.out.println(inMemoryTaskManager.getTask(inMemoryTaskManager.getTaskKey("Домашние дела")));
    }

    public static void sortedListTest() {
        inMemoryTaskManager.addTask(task);
        inMemoryTaskManager.setTaskDate(inMemoryTaskManager.getTaskKey("Домашние дела"),
                LocalDateTime.now(), 10);
        Epic epic1 = new Epic("Сходить в магазин, за свежими продуктами", "Купить продукты");
        Subtask subTask1 = new Subtask("Купить мясо", "В мясном отделе");
        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.addSubtask(subTask1, "Сходить в магазин, за свежими продуктами");
        inMemoryTaskManager.setSubtaskDate(inMemoryTaskManager.getTaskKey("Купить мясо"),
                LocalDateTime.now().plusHours(1), 10);
        System.out.println(inMemoryTaskManager.getPrioritizedTasks());
    }

    public static void loadTest() {
        System.out.println("История первого файла: " + inMemoryTaskManager.getHistory());
        TaskManager loadedTaskManager = Managers.loadFromFile(FileBackedTasksManager.pathOfSavedTasks);
        System.out.println("История нового файла: " + loadedTaskManager.getHistory());
    }
}
