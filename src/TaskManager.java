import java.util.HashMap;
import java.util.Objects;

public class TaskManager {
    private int identifier = 1;

    public int getIdentifier() {
        return identifier;
    }

    private int identifier() {
        return ++identifier;
    }
    HashMap<Integer, Task>  tasks = new HashMap<>();
    HashMap<Integer, Epic>  epics = new HashMap<>();
    HashMap<Integer, Subtask>  subtasks = new HashMap<>();

    public void addTask(Task task) {
        tasks.put(identifier(), task);
    }
    public void addEpic(Epic epic) {
        epics.put(identifier(), epic);
    }
    public void addSubtask(Subtask subtask, String epicName) {
        subtasks.put(identifier(), subtask);
        for (Integer key : epics.keySet()) {
            if (epics.get(key).getName().equals(epicName)) {
                subtasks.get(getIdentifier()).setAttachment(key);
                epics.get(key).addSubtasks(subtask);
            }
        }
    }
    public void changeTask(Task task, int identifier) {
        tasks.remove(identifier);
        tasks.put(identifier, task);
        tasks.get(identifier).setStatus("IN_PROGRESS");
    }
    public void changeSubtask(Subtask subtask, int identifier) {
        subtasks.remove(identifier);
        subtasks.put(identifier, subtask);
        subtasks.get(identifier).setStatus("IN_PROGRESS");
        epics.get(subtasks.get(identifier).getAttachment()).setStatus("IN_PROGRESS");
    }
    public void completeTask(Task task, int identifier) {
        tasks.remove(identifier);
        tasks.put(identifier, task);
        tasks.get(identifier).setStatus("DONE");
    }
    public void completeSubtask(Subtask subtask, int identifier) {
        subtasks.remove(identifier);
        subtasks.put(identifier, subtask);
        subtasks.get(identifier).setStatus("DONE");
        boolean epicCheck = true;
        for (Integer key : epics.keySet()) {
            for (Subtask subtaskCheck : epics.get(key).getSubtasks()) {
                if (!subtaskCheck.getStatus().equals("DONE")) {
                    epicCheck = false;
                }
            }
            if (epicCheck) {
                epics.get(key).setStatus("DONE");
            } else {
                epics.get(key).setStatus("IN_PROGRESS");
            }

        }
    }

    public String getList() {
        String list = "";
        for (Task value : tasks.values()) {
            list += (value.getName()+"\n");
        }
        for (Epic value : epics.values()) {
            list += (value.getName()+"\n");
            for (Subtask subtask : value.getSubtasks()) {
                list += (subtask.getName()+"\n");
            }
        }
        return list;
    }
    public void removeTasks() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }
    public int getTaskKey(String name, String type) { //todo
        if (type.equals("Task")) {
            for (Integer key : tasks.keySet()) {
                if (tasks.get(key).getName().equals(name)) {
                    return key;
                }
            }
        } else if (type.equals("Epic")) {
            for (Integer key : epics.keySet()) {
                if (epics.get(key).getName().equals(name)) {
                    return key;
                }
            }
        } else if (type.equals("Subtask")) {
            for (Integer key : subtasks.keySet()) {
                if (subtasks.get(key).getName().equals(name)) {
                    return key;
                }
            }
        }
        return 0;
    }
    public Task getTask(int identifier) {
        return tasks.get(identifier);
    }
    public Epic getEpic(int identifier) {
        return epics.get(identifier);
    }
    public Subtask getSubtask(int identifier) {
        return subtasks.get(identifier);
    }
    public void removeTask(int identifier) {
        tasks.remove(identifier);
    }
    public void removeEpic(int identifier) {
        epics.remove(identifier);
    }
    public void removeSubtask(int identifier) {
        subtasks.remove(identifier);
    }
}
