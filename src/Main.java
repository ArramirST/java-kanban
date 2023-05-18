public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();
        Subtask task = new Subtask("Спринт 3", "Выполнить до вечера");
        Epic epic = new Epic("Учеба", "456");
        taskManager.addEpic(epic);
        taskManager.addSubtask(task, "Учеба");
        task = new Subtask("Спринт 4", "Выполнить до конца недели");
        taskManager.addSubtask(task, "Учеба");
        taskManager.completeSubtask(taskManager.subtasks.get(taskManager.getTaskKey("Спринт 3", "Subtask")), taskManager.getTaskKey("Спринт 3", "Subtask"));
        System.out.println(taskManager.epics.toString());
        System.out.println(taskManager.getList());
    }

}
