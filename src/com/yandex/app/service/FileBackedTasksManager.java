package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


public class FileBackedTasksManager extends InMemoryTaskManager {

    private static String savedTasks = "history.txt";
    private static Path pathOfSavedTasks = Paths.get(savedTasks);


    public FileBackedTasksManager(Path path) {
        try {
            String data = Files.readString(path);
            setCurrentId(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileBackedTasksManager() {
    }

    protected void save() {
        try (Writer fileWriter = new FileWriter(savedTasks)) {
            fileWriter.write("id','type','name','status','description','start time','duration','epic\n");
            for (Integer id : tasks.keySet()) {
                fileWriter.write(id + "','" + tasks.get(id).toString() + "','\n");
            }
            for (Integer id : epics.keySet()) {
                fileWriter.write(id + "','" + epics.get(id).toString() + "','\n");
            }
            for (Integer id : subtasks.keySet()) {
                fileWriter.write(id + "','" + subtasks.get(id).toString() + "\n");
            }
            fileWriter.write("\n");
            fileWriter.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }


    private void fromString(String value) {
        String[] split = value.split("','");
        switch (split[1]) {
            case ("TASK"):
                fromTask(split);
                break;
            case ("EPIC"):
                fromEpic(split);
                break;
            case ("SUBTASK"):
                fromSubTask(split);
                break;
        }
    }

    private void fromTask(String[] split) {
        addTask(new Task(split[2], split[4]), Integer.parseInt(split[0]));
        try {
            setTaskDate(Integer.parseInt(split[0]), LocalDateTime.parse(split[5]), Integer.parseInt(split[6]));
        } catch (DateTimeParseException | NullPointerException e) {
        }
        if (split[3].equals("IN_PROGRESS")) {
            tasks.get(Integer.parseInt(split[0])).setStatus(Status.IN_PROGRESS);
        } else if (split[3].equals("DONE")) {
            tasks.get(Integer.parseInt(split[0])).setStatus(Status.DONE);
        }
    }

    private void fromEpic(String[] split) {
        addEpic(new Epic(split[2], split[4]), Integer.parseInt(split[0]));
        if (split[3].equals("IN_PROGRESS")) {
            epics.get(Integer.parseInt(split[0])).setStatus(Status.IN_PROGRESS);
        } else if (split[3].equals("DONE")) {
            epics.get(Integer.parseInt(split[0])).setStatus(Status.DONE);
        }
    }

    private void fromSubTask(String[] split) {
        addSubtask(new Subtask(split[2], split[4]), Integer.parseInt(split[7]), Integer.parseInt(split[0]));
        try {
            setSubtaskDate(Integer.parseInt(split[0]), LocalDateTime.parse(split[5]), Integer.parseInt(split[6]));
        } catch (DateTimeParseException | NullPointerException e) {
        }
        if (split[3].equals("IN_PROGRESS")) {
            subtasks.get(Integer.parseInt(split[0])).setStatus(Status.IN_PROGRESS);
        } else if (split[3].equals("DONE")) {
            subtasks.get(Integer.parseInt(split[0])).setStatus(Status.DONE);
        }
    }

    static String historyToString(HistoryManager manager) {
        StringBuilder builder = new StringBuilder();
        for (Task task : manager.getHistory()) {
            builder.append(task.getIdentifier() + ",");
        }
        return builder.toString();
    }

    static List<Integer> historyFromString(String value) {
        String[] split = value.split(",");
        List<Integer> historyList = new ArrayList<>();
        try {
            for (int i = 0; i < (split.length); i++) {
                historyList.add(Integer.parseInt(split[i]));
            }
        } catch (NumberFormatException e) {
            return new ArrayList<>();
        }

        return historyList;
    }

    public void addTask(Task task, int identifier) {
        for (Integer key : tasks.keySet()) {
            if (tasks.get(key).equals(task)) {
                return;
            }
        }
        tasks.put(identifier, task);
        task.setIdentifier(identifier);
    }

    public void addEpic(Epic epic, int identifier) {
        for (Integer key : epics.keySet()) {
            if (epics.get(key).equals(epic)) {
                return;
            }
        }
        epics.put(identifier, epic);
        epic.setIdentifier(identifier);
    }

    public void addSubtask(Subtask subtask, int epicId, int identifier) {
        for (Integer key : subtasks.keySet()) {
            if (subtasks.get(key).equals(subtask)) {
                return;
            }
        }
        subtasks.put(identifier, subtask);
        subtask.setIdentifier(identifier);
        for (Epic epic : epics.values()) {
            if (epic.getIdentifier() == epicId) {
                subtasks.get(identifier).setAttachment(epicId);
                epic.addSubtasksId(identifier);
            }
        }
    }

    protected void setCurrentId(String data) {
        String[] split = data.split("\n");
        for (int i = 0; i < (split.length - 2); i++) {
            fromString(split[i]);
        }
        List<Integer> historyId = historyFromString(split[split.length - 1]);
        int maxId = findMaxId(historyId);
        setIdentifier(maxId);
    }

    protected int findMaxId(List<Integer> historyId) {
        int maxId = 0;
        for (Integer id : historyId) {
            for (Integer key : tasks.keySet()) {
                if (key.equals(id)) {
                    historyManager.add(tasks.get(key));
                }
                if (key > maxId) {
                    maxId = key;
                }
            }
            for (Integer key : epics.keySet()) {
                if (key.equals(id)) {
                    historyManager.add(epics.get(key));
                }
                if (key > maxId) {
                    maxId = key;
                }
            }
            for (Integer key : subtasks.keySet()) {
                if (key.equals(id)) {
                    historyManager.add(subtasks.get(key));
                }
                if (key > maxId) {
                    maxId = key;
                }
            }
        }
        return maxId;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask, String epicName) throws IllegalArgumentException {
        super.addSubtask(subtask, epicName);
        save();
    }

    @Override
    public void updateTask(Task task, Status status) {
        super.updateTask(task, status);
        save();
    }

    @Override
    public void updateEpic(Epic epic, Status status) {
        super.updateEpic(epic, status);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask, Status status) {
        super.updateSubtask(subtask, status);
        save();
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    @Override
    public Task getTask(int identifier) {
        try {
            return super.getTask(identifier);
        } finally {
            save();
        }
    }

    @Override
    public Task getEpic(int identifier) {
        try {
            return super.getEpic(identifier);
        } finally {
            save();
        }
    }

    @Override
    public Task getSubtask(int identifier) {
        try {
            return super.getSubtask(identifier);
        } finally {
            save();
        }
    }

    @Override
    public void removeTask(int identifier) {
        super.removeTask(identifier);
        save();
    }

    @Override
    public void removeEpic(int identifier) {
        super.removeEpic(identifier);
        save();
    }

    @Override
    public void removeSubtask(int identifier) {
        super.removeSubtask(identifier);
        save();
    }
}
