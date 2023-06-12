package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int identifier = 1;

    @Override
    public int getIdentifier() {
        return identifier;
    }

    @Override
    public int identifier() {
        return ++identifier;
    }
    private HashMap<Integer, Task>  tasks = new HashMap<>();
    private HashMap<Integer, Epic>  epics = new HashMap<>();
    private HashMap<Integer, Subtask>  subtasks = new HashMap<>();
    HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void addTask(Task task) {
        for (Integer key : tasks.keySet()) {
            if (tasks.get(key).equals(task)) {
                return;
            }
        }
        tasks.put(identifier(), task);
        task.setIdentifier(getIdentifier());
    }

    @Override
    public void addEpic(Epic epic) {
        for (Integer key : epics.keySet()) {
            if (epics.get(key).equals(epic)) {
                return;
            }
        }
        epics.put(identifier(), epic);
        epic.setIdentifier(getIdentifier());
    }

    @Override
    public void addSubtask(Subtask subtask, String epicName) {
        for (Integer key : subtasks.keySet()) {
            if (subtasks.get(key).equals(subtask)) {
                return;
            }
        }
        subtasks.put(identifier(), subtask);
        subtask.setIdentifier(getIdentifier());
        for (Integer key : epics.keySet()) {
            if (epics.get(key).getName().equals(epicName)) {
                subtasks.get(getIdentifier()).setAttachment(key);
                epics.get(key).addSubtasksId(subtask.getIdentifier());
                updateEpicStatus(epics.get(key));
            }
        }
    }

    @Override
    public void updateTask(Task task, Status status) {
        int identifier = getTaskKey(task.getName());
        boolean isCreated = false;
        for (Integer key : tasks.keySet()) {
            if (key == identifier) {
                isCreated = true;
                break;
            }
        }
        if (!isCreated) {
            System.out.println("Нельзя обновить задачу до ее создания");
            return;
        }
        tasks.remove(identifier);
        tasks.put(identifier, task);
        tasks.get(identifier).setStatus(status);
    }

    @Override
    public void updateEpic(Epic epic, Status status) {
        int identifier = getTaskKey(epic.getName());
        boolean isCreated = false;
        for (Integer key : epics.keySet()) {
            if (key == identifier) {
                isCreated = true;
                break;
            }
        }
        if (!isCreated) {
            System.out.println("Нельзя обновить эпик до его создания");
            return;
        }
        epics.remove(identifier);
        epics.put(identifier, epic);
        epics.get(identifier).setStatus(status);
    }

    @Override
    public void updateSubtask(Subtask subtask, Status status) {
        int identifier = getTaskKey(subtask.getName());
        boolean isCreated = false;
        for (Integer key : subtasks.keySet()) {
            if (key == identifier) {
                isCreated = true;
                break;
            }
        }
        if (!isCreated) {
            System.out.println("Нельзя обновить подзадачу до ее создания");
            return;
        }
        subtasks.remove(identifier);
        subtasks.put(identifier, subtask);
        subtasks.get(identifier).setStatus(status);
        updateEpicStatus(subtask);
    }

    @Override
    public List<String> getTaskList() {
        List<String> list = new ArrayList<>();
        for (Task value : tasks.values()) {
            list.add(value.getName());
        }
        return list;
    }

    @Override
    public List<String> getEpicList() {
        List<String> list = new ArrayList<>();
        for (Epic value : epics.values()) {
            list.add(value.getName());
        }
        return list;
    }

    @Override
    public List<String> getSubtaskList() {
        List<String> list = new ArrayList<>();
        for (Subtask value : subtasks.values()) {
            list.add(value.getName());
        }
        return list;
    }

    @Override
    public void removeTasks() {
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        for (Integer key : epics.keySet()) {
            epics.get(key).removeSubtasksId();
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            updateEpicStatus(epic);
        }
    }

    @Override
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

    @Override
    public Task getTask(int identifier) {
        for (Integer key : tasks.keySet()) {
            if (key == identifier) {
                historyManager.add(tasks.get(key));
                return tasks.get(key);
            }
        }
        return null;
    }

    @Override
    public Task getEpic(int identifier) {
        for (Integer key : epics.keySet()) {
            if (key == identifier) {
                historyManager.add(epics.get(key));
                return epics.get(key);
            }
        }
        return null;
    }

    @Override
    public Task getSubtask(int identifier) {
        for (Integer key : subtasks.keySet()) {
            if (key == identifier) {
                historyManager.add(subtasks.get(key));
                return subtasks.get(key);
            }
        }
        return null;
    }

    @Override
    public void removeTask(int identifier) {
        tasks.remove(identifier);
    }

    @Override
    public void removeEpic(int identifier) {
        for (Integer id : subtasks.keySet()) {
            if (subtasks.get(id).getAttachment()==identifier) {
                subtasks.remove(id);
            }
        }
        epics.get(identifier).removeSubtasksId();
        epics.remove(identifier);
    }

    @Override
    public void removeSubtask(int identifier) {
        subtasks.remove(identifier);
        for (Integer key : epics.keySet()) {
            if (epics.get(key).getSubtasks().contains(identifier)) {
                epics.get(key).getSubtasks().remove((Integer)identifier);
            }
            updateEpicStatus(epics.get(key));
        }
    }

    @Override
    public List<String> getEpicSubtasksList(String name) {
        List<String> list = new ArrayList<>();
        for (Integer key : epics.keySet()) {
            if (epics.get(key).getName().equals(name)) {
                for (Integer subtaskId : epics.get(key).getSubtasks()) {
                    list.add(subtasks.get(subtaskId).getName());
                }
            }
        }
        return list;
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        int newCount = 0;
        int inProgressCount = 0;
        int doneCount = 0;
        for (Task task : tasks.values()) {
            for (Integer subtasksId : epic.getSubtasks()) {
                if (task.getIdentifier()==subtasksId) {
                    switch (task.getStatus()) {
                        case IN_PROGRESS:
                            inProgressCount++;
                            break;
                        case DONE:
                            doneCount++;
                            break;
                        case NEW:
                            newCount++;
                            break;
                    }
                }
            }
        }
        if (newCount==0&&inProgressCount==0&&doneCount>0) {
            epic.setStatus(Status.DONE);
        } else if (doneCount==0&&inProgressCount==0) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public void updateEpicStatus(Subtask subtask) {
        int newCount = 0;
        int inProgressCount = 0;
        int doneCount = 0;
        for (Integer key : epics.keySet()) {
            if (subtask.getAttachment()==key) {
                for (Integer subtasksId : epics.get(key).getSubtasks()) {
                    if (subtasks.get(subtasksId).getStatus().equals(Status.IN_PROGRESS)) {
                        inProgressCount++;
                    } else if (subtasks.get(subtasksId).getStatus().equals(Status.DONE)) {
                        doneCount++;
                    } else if (subtasks.get(subtasksId).getStatus().equals(Status.NEW)) {
                        newCount++;
                    }
                }
                if (newCount==0&&inProgressCount==0&&doneCount>0) {
                    epics.get(key).setStatus(Status.DONE);
                } else if (doneCount==0&&inProgressCount==0) {
                    epics.get(key).setStatus(Status.NEW);
                } else {
                    epics.get(key).setStatus(Status.IN_PROGRESS);
                }
                break;
            }
        }

    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}