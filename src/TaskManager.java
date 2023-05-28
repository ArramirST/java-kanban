import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                return;
            }
        }
        tasks.put(identifier(), task);
    }
    public void addEpic(Epic epic) {
        for (Integer key : epics.keySet()) {
            if (epics.get(key).equals(epic)) {
                return;
            }
        }
        epics.put(identifier(), epic);
    }
    public void addSubtask(Subtask subtask, String epicName) {
        for (Integer key : subtasks.keySet()) {
            if (subtasks.get(key).equals(subtask)) {
                return;
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

    public List<String> getTaskList() {
        List<String> list = new ArrayList<>();
        for (Task value : tasks.values()) {
            list.add(value.getName());
        }
        return list;
    }
    public List<String> getEpicList() {
        List<String> list = new ArrayList<>();
        for (Epic value : epics.values()) {
            list.add(value.getName());
        }
        return list;
    }
    public List<String> getSubtaskList() {
        List<String> list = new ArrayList<>();
        for (Subtask value : subtasks.values()) {
            list.add(value.getName());
        }
        return list;
    }
    public void removeTasks() {
        tasks.clear();
    }
    public void removeEpics() {
        for (Integer key : epics.keySet()) {
            epics.get(key).removeSubtasks();
        }
        epics.clear();
        subtasks.clear();
    }
    public void removeSubtasks() {
        subtasks.clear();
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
        return null;
    }
    public Task getEpic(int identifier) {
        for (Integer key : epics.keySet()) {
            if (key == identifier) {
                return epics.get(key);
            }
        }
        return null;
    }
    public Task getSubtask(int identifier) {
        for (Integer key : subtasks.keySet()) {
            if (key == identifier) {
                return subtasks.get(key);
            }
        }
        return null;
    }
    public void removeTask(int identifier) {
        tasks.remove(identifier);
    }
    public void removeEpic(int identifier) {
        for (Integer id : subtasks.keySet()) {
            if (subtasks.get(id).getAttachment()==identifier) {
                subtasks.remove(id);
            }
        }
        epics.get(identifier).removeSubtasks();
        epics.remove(identifier);
    }
    public void removeSubtask(int identifier) {
        subtasks.remove(identifier);
    }
    public List getEpicSubtasksList(String name) {
        List<String> list = new ArrayList<>();
        for (Integer key : epics.keySet()) {
            if (epics.get(key).getName().equals(name)) {
                for (Subtask subtask : epics.get(key).getSubtasks()) {
                    list.add(subtask.getName());
                }
            }
        }
        return list;
    }
}
