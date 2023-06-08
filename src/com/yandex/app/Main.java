package com.yandex.app;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskManager;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();
        Scanner scanner = new Scanner(System.in);
        Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
        Epic epic = new Epic("Учеба", "Нужно учиться");
        taskManager.addEpic(epic);
        taskManager.addSubtask(subTask, "Учеба");
        subTask = new Subtask("Спринт 4", "Выполнить до конца недели");
        Task task = new Task("Домашние дела", "Помыть посуду");
        int choice;
        String type;
        int status;
        while (true) {
            printMenu();
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.println(taskManager.getTaskList());
                    System.out.println(taskManager.getEpicList());
                    System.out.println(taskManager.getSubtaskList());
                    break;
                case 2:
                    taskManager.removeTasks();
                    taskManager.removeEpics();
                    taskManager.removeSubtasks();
                    break;
                case 3:
                    System.out.println(taskManager.getEpic(taskManager.getTaskKey("Учеба")));
                    System.out.println(taskManager.getTask(taskManager.getTaskKey("Домашние дела")));
                    System.out.println(taskManager.getSubtask(taskManager.getTaskKey("Спринт 3")));
                    System.out.println(taskManager.getSubtask(taskManager.getTaskKey("Спринт 4")));
                    break;
                case 4:
                    taskManager.addEpic(epic);
                    taskManager.addSubtask(subTask, "Учеба");
                    taskManager.addTask(task);
                    break;
                case 5:
                    taskManager.updateEpic(epic, "IN_PROGRESS");
                    taskManager.updateTask(task, "IN_PROGRESS");
                    taskManager.updateSubtask(subTask, "IN_PROGRESS");
                    break;
                case 6:
                    taskManager.removeSubtask(taskManager.getTaskKey("Спринт 3"));
                    taskManager.removeEpic(taskManager.getTaskKey("Учеба"));
                    taskManager.removeTask(taskManager.getTaskKey("Домашние дела"));
                    break;
                case 7:
                    System.out.println(taskManager.getEpicSubtasksList("Учеба"));
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
        System.out.println("0 - Завершение работы");
    }

}
