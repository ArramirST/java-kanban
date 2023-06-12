package com.yandex.app;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.service.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        TaskManager inMemoryTaskManager = Managers.getDefault();
        Scanner scanner = new Scanner(System.in);
        Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
        Epic epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.addEpic(epic);
        inMemoryTaskManager.addSubtask(subTask, "Учеба");
        subTask = new Subtask("Спринт 4", "Выполнить до конца недели");
        Task task = new Task("Домашние дела", "Помыть посуду");
        int choice;
        while (true) {
            printMenu();
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.println(inMemoryTaskManager.getTaskList());
                    System.out.println(inMemoryTaskManager.getEpicList());
                    System.out.println(inMemoryTaskManager.getSubtaskList());
                    break;
                case 2:
                    inMemoryTaskManager.removeTasks();
                    inMemoryTaskManager.removeEpics();
                    inMemoryTaskManager.removeSubtasks();
                    break;
                case 3:
                    System.out.println(inMemoryTaskManager.getEpic(inMemoryTaskManager.getTaskKey("Учеба")));
                    System.out.println(inMemoryTaskManager.getTask(inMemoryTaskManager.getTaskKey("Домашние дела")));
                    System.out.println(inMemoryTaskManager.getSubtask(inMemoryTaskManager.getTaskKey("Спринт 3")));
                    System.out.println(inMemoryTaskManager.getSubtask(inMemoryTaskManager.getTaskKey("Спринт 4")));
                    System.out.println(inMemoryTaskManager.getHistory());
                    break;
                case 4:
                    inMemoryTaskManager.addEpic(epic);
                    inMemoryTaskManager.addSubtask(subTask, "Учеба");
                    inMemoryTaskManager.addTask(task);
                    break;
                case 5:
                    inMemoryTaskManager.updateEpic(epic, Status.IN_PROGRESS);
                    inMemoryTaskManager.updateTask(task, Status.IN_PROGRESS);
                    inMemoryTaskManager.updateSubtask(subTask, Status.IN_PROGRESS);
                    break;
                case 6:
                    inMemoryTaskManager.removeSubtask(inMemoryTaskManager.getTaskKey("Спринт 3"));
                    inMemoryTaskManager.removeEpic(inMemoryTaskManager.getTaskKey("Учеба"));
                    inMemoryTaskManager.removeTask(inMemoryTaskManager.getTaskKey("Домашние дела"));
                    break;
                case 7:
                    System.out.println(inMemoryTaskManager.getEpicSubtasksList("Учеба"));
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
