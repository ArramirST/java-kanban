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
        for (Integer key : tasks.keySet()) {
            if (tasks.get(key).equals(task)) {
                break;
            }
        }
        tasks.put(identifier(), task);
    }
    public void addEpic(Epic epic) {
        for (Integer key : epics.keySet()) {
            if (epics.get(key).equals(epic)) {
                break;
            }
        }
        epics.put(identifier(), epic);
    }
    public void addSubtask(Subtask subtask, String epicName) {
        for (Integer key : subtasks.keySet()) {
            if (subtasks.get(key).equals(subtask)) {
                break;
            }
        }
        subtasks.put(identifier(), subtask);
        for (Integer key : epics.keySet()) {
            if (epics.get(key).getName().equals(epicName)) {
                subtasks.get(getIdentifier()).setAttachment(key);
                epics.get(key).addSubtasks(subtask);
            }
        }
    }
    public void changeTask(Task task, String status) {
        int identifier = getTaskKey(task.getName());
        tasks.remove(identifier);
        tasks.put(identifier, task);
        tasks.get(identifier).setStatus(status);
    }
    public void changeEpic(Epic epic, String status) {
        int identifier = getTaskKey(epic.getName());
        epics.remove(identifier);
        epics.put(identifier, epic);
        epics.get(identifier).setStatus(status);
    }
    public void changeSubtask(Subtask subtask, String status) {
        int identifier = getTaskKey(subtask.getName());
        subtasks.remove(identifier);
        subtasks.put(identifier, subtask);
        subtasks.get(identifier).setStatus(status);
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
        }
        for (Subtask value : subtasks.values()) {
            list += (value.getName()+"\n");
        }
        return list;
    }
    public void removeTasks() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }
    public int getTaskKey(String name) {
            for (Integer key : tasks.keySet()) {
                if (tasks.get(key).getName().equals(name)) {
                    return key;
                }
            }
            for (Integer key : epics.keySet()) {
                if (epics.get(key).getName().equals(name)) {
                    return key;
                }
            }
            for (Integer key : subtasks.keySet()) {
                if (subtasks.get(key).getName().equals(name)) {
                    return key;
                }
            }
        return 0;
    }
    public Task getTask(int identifier) {
        for (Integer key : tasks.keySet()) {
            if (key == identifier) {
                return tasks.get(key);
            }
        }
        for (Integer key : epics.keySet()) {
            if (key == identifier) {
                return epics.get(key);
            }
        }
        for (Integer key : subtasks.keySet()) {
            if (key == identifier) {
                return subtasks.get(key);
            }
        }
        return null;
    }
    public void removeTask(int identifier) {
        for (Integer key : tasks.keySet()) {
            if (key == identifier) {
                tasks.remove(identifier);
                break;
            }
        }
        for (Integer key : epics.keySet()) {
            if (key == identifier) {
                epics.remove(identifier);
                break;
            }
        }
        for (Integer key : subtasks.keySet()) {
            if (key == identifier) {
                subtasks.remove(identifier);
                break;
            }
        }
    }
    public String getEpicList(String name) {
        String list = "";
        for (Integer key : epics.keySet()) {
            if (epics.get(key).getName().equals(name)) {
                for (Subtask subtask : epics.get(key).getSubtasks()) {
                    list += (subtask.getName() + "\n");
                }
            }
        }
        return list;
    }
}
