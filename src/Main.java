public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();
        Task task = new Task("Спринт 3", "Выполнить до вечера");
        taskManager.addTask(task);
        System.out.println(taskManager.tasks);
    }

}
