import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();
        Scanner scanner = new Scanner(System.in);
        Subtask task = new Subtask("Спринт 3", "Выполнить до вечера");
        Epic epic = new Epic("Учеба", "Нужно учиться");
        taskManager.addEpic(epic);
        taskManager.addSubtask(task, "Учеба");
        task = new Subtask("Спринт 4", "Выполнить до конца недели");
        taskManager.addSubtask(task, "Учеба");
        int choice;
        String type;
        int status;
        while (true) {
            printMenu();
            choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.println(taskManager.getList());
                    break;
                case 2:
                    taskManager.removeTasks();
                    break;
                case 3:
                    System.out.println(taskManager.getTask(taskManager.getTaskKey("Спринт 3")));
                    break;
                case 4:
                    taskManager.addEpic(epic);
                    break;
                case 5:
                    taskManager.changeEpic(epic, "IN_PROGRESS");
                    break;
                case 6:
                    taskManager.removeTask(taskManager.getTaskKey("Спринт 3"));
                    break;
                case 7:
                    System.out.println(taskManager.getEpicList("Учеба"));
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
