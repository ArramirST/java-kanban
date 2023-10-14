package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int identifier = 1;
    private String bugTracker = "";

    public String getBugTracker() {
        return bugTracker;
    }

    @Override
    public int getIdentifier() {
        return identifier;
    }

    protected void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    @Override
    public int identifier() {
        return ++identifier;
    }

    public HashMap<Integer, Task> tasks = new HashMap<>();
    public HashMap<Integer, Epic> epics = new HashMap<>();
    public HashMap<Integer, Subtask> subtasks = new HashMap<>();
    public HistoryManager historyManager = Managers.getDefaultHistory();
    protected TreeSet<Task> sortedTasks = new TreeSet<>((Task task1, Task task2) -> {
        if (task1.getStartTime() == null) {
            return 1;
        }
        if (task2.getStartTime() == null) {
            return -1;
        } else if (task1.getStartTime().isAfter(task2.getStartTime())) {
            return 1;
        } else if (task1.getStartTime().isBefore(task2.getStartTime())) {
            return -1;
        } else {
            return 0;
        }

    });

    @Override
    public void addTask(Task task) {
        for (Integer key : tasks.keySet()) {
            if (tasks.get(key).equals(task)) {
                return;
            }
        }
        tasks.put(identifier(), task);
        task.setIdentifier(getIdentifier());
        sortedTasks.add(task);
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
    public void addSubtask(Subtask subtask, String epicName) throws IllegalArgumentException {
        for (Integer key : subtasks.keySet()) {
            if (subtasks.get(key).equals(subtask)) {
                return;
            }
        }
        subtasks.put(identifier(), subtask);
        subtask.setIdentifier(getIdentifier());
        boolean isAttached = false;
        for (Integer key : epics.keySet()) {
            if (epics.get(key).getName().equals(epicName)) {
                subtasks.get(getIdentifier()).setAttachment(key);
                epics.get(key).addSubtasksId(subtask.getIdentifier());
                updateEpicStatus(epics.get(key));
                isAttached = true;
            }
        }
        if (!isAttached) throw new IllegalArgumentException("Невозможно создать подзадачу без эпика");
        sortedTasks.add(subtask);
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
        try {
            if (!isCreated) {
                throw new IllegalArgumentException("Нельзя обновить задачу до ее создания");
            }
        } catch (IllegalArgumentException e) {
            bugTracker = e.getMessage();
            System.out.println(bugTracker);
            return;
        }
        sortedTasks.remove(tasks.get(identifier));
        tasks.remove(identifier);
        tasks.put(identifier, task);
        tasks.get(identifier).setStatus(status);
        sortedTasks.add(task);
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
        try {
            if (!isCreated) {
                throw new IllegalArgumentException("Нельзя обновить эпик до его создания");
            }
        } catch (IllegalArgumentException e) {
            bugTracker = e.getMessage();
            System.out.println(bugTracker);
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
        try {
            if (!isCreated) {
                throw new IllegalArgumentException("Нельзя обновить подзадачу до ее создания");
            }
        } catch (IllegalArgumentException e) {
            bugTracker = e.getMessage();
            System.out.println(bugTracker);
            return;
        }
        sortedTasks.remove(subtasks.get(identifier));
        subtasks.remove(identifier);
        subtasks.put(identifier, subtask);
        subtasks.get(identifier).setStatus(status);
        updateEpicStatus(subtask);
        sortedTasks.add(subtask);
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
        for (Integer key : tasks.keySet()) {
            historyManager.remove(key);
        }
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        for (Integer key : epics.keySet()) {
            historyManager.remove(key);
            epics.get(key).removeSubtasksId();
        }
        epics.clear();
        for (Integer key : subtasks.keySet()) {
            historyManager.remove(key);
        }
        subtasks.clear();
    }

    @Override
    public void removeSubtasks() {
        for (Integer key : subtasks.keySet()) {
            historyManager.remove(key);
        }
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
                Task chekedTask = tasks.get(key);
                historyManager.add(chekedTask);
                return chekedTask;
            }
        }
        return null;
    }

    @Override
    public Task getEpic(int identifier) {
        for (Integer key : epics.keySet()) {
            if (key == identifier) {
                Task chekedTask = epics.get(key);
                historyManager.add(chekedTask);
                return chekedTask;
            }
        }
        return null;
    }

    @Override
    public Task getSubtask(int identifier) {
        for (Integer key : subtasks.keySet()) {
            if (key == identifier) {
                Task chekedTask = subtasks.get(key);
                historyManager.add(chekedTask);
                return chekedTask;
            }
        }
        return null;
    }

    @Override
    public void removeTask(int identifier) {
        historyManager.remove(identifier);
        tasks.remove(identifier);
    }

    @Override
    public void removeEpic(int identifier) {
        if (identifier == 0) return;
        for (Iterator<Subtask> iterator = subtasks.values().iterator(); iterator.hasNext(); ) {
            Subtask subtask = iterator.next();
            if (subtask.getAttachment() == identifier) {
                historyManager.remove(subtask.getIdentifier());
                iterator.remove();
            }
        }
        epics.get(identifier).removeSubtasksId();
        historyManager.remove(identifier);
        epics.remove(identifier);
    }

    @Override
    public void removeSubtask(int identifier) {
        historyManager.remove(identifier);
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

    public void setTaskDate(Integer id, LocalDateTime startTime, int duration) {
        for (Task sortedTask : sortedTasks) {
            try {
                if (isOverlap(sortedTask.getStartTime(), sortedTask.getEndTime(), startTime, duration)
                        && sortedTask.getIdentifier() != id) {
                    throw new IllegalArgumentException("Время выполнения задач пересекается");
                }
            } catch (NullPointerException e) {

            }
        }
        sortedTasks.remove(tasks.get(id));
        tasks.get(id).setDuration(duration);
        tasks.get(id).setStartTime(startTime);
        sortedTasks.add(tasks.get(id));
    }

    public void setSubtaskDate(Integer id, LocalDateTime startTime, int duration) {
        for (Task sortedTask : sortedTasks) {
            try {
                if (isOverlap(sortedTask.getStartTime(), sortedTask.getEndTime(), startTime, duration)
                        && sortedTask.getIdentifier() != id) {
                    throw new IllegalArgumentException("Время выполнения задач пересекается");
                }
            } catch (NullPointerException e) {

            }
        }
        sortedTasks.remove(subtasks.get(id));
        subtasks.get(id).setDuration(duration);
        subtasks.get(id).setStartTime(startTime);
        setEpicDate(id, startTime, duration);
        sortedTasks.add(subtasks.get(id));
    }

    public void setEpicDate(Integer id, LocalDateTime startTime, int duration) {
        LocalDateTime minStartTime = startTime;
        LocalDateTime maxEndTime = startTime.plus(Duration.ofMinutes(duration));
        for (Integer key : epics.keySet()) {
            if (subtasks.get(id).getAttachment() == key) {
                for (Integer subtaskId : epics.get(key).getSubtasks()) {
                    try {
                        if (subtasks.get(subtaskId).getStartTime().isBefore(minStartTime)) {
                            minStartTime = subtasks.get(subtaskId).getStartTime();
                        }
                        if (subtasks.get(subtaskId).getEndTime().isAfter(maxEndTime)) {
                            maxEndTime = subtasks.get(subtaskId).getEndTime();
                        }
                    } catch (NullPointerException e) {

                    }
                }
            }
            epics.get(key).setStartTime(minStartTime);
            epics.get(key).setEndTime(maxEndTime);
            epics.get(key).setDuration((int) Duration.between(minStartTime, maxEndTime).toMinutes());
        }
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return sortedTasks;
    }

    public boolean isOverlap(LocalDateTime startTime1, LocalDateTime endTime1, LocalDateTime startTime2, int duration2) {
        return (startTime1.isBefore(startTime2) && endTime1.isAfter(startTime2))
                || (startTime1.isBefore(startTime2.plus(Duration.ofMinutes(duration2)))
                && endTime1.isAfter(startTime2.plus(Duration.ofMinutes(duration2))))
                || (startTime1.isAfter(startTime2)
                && endTime1.isBefore(startTime2.plus(Duration.ofMinutes(duration2))));
    }
}