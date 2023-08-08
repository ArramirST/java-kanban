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
import java.util.ArrayList;
import java.util.List;


public class FileBackedTasksManager extends InMemoryTaskManager {

    static String savedTasks = "history.txt";
    public static Path pathOfSavedTasks = Paths.get(savedTasks);

    public FileBackedTasksManager() {
    }

    public FileBackedTasksManager(Path path) {
        try {
            String data = Files.readString(path);
            String[] split = data.split("\n");
            for (int i = 0; i < (split.length - 2); i++) {
                fromString(split[i]);
            }
            List<Integer> historyId = historyFromString(split[split.length - 1]);
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
            setIdentifier(maxId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void save() {
        try (Writer fileWriter = new FileWriter(savedTasks)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Integer id : tasks.keySet()) {
                fileWriter.write(id + "," + tasks.get(id).toString() + ",\n");
            }
            for (Integer id : epics.keySet()) {
                fileWriter.write(id + "," + epics.get(id).toString() + ",\n");
            }
            for (Integer id : subtasks.keySet()) {
                fileWriter.write(id + "," + subtasks.get(id).toString() + "\n");
            }
            fileWriter.write("\n");
            fileWriter.write(historyToString(historyManager));
        } catch (IOException e) {
            System.out.println("Не удалось записать файл");
        }
    }


    private void fromString(String value) {
        String[] split = value.split(",");
        switch (split[1]) {
            case ("TASK"):
                addTask(new Task(split[2], split[4]), Integer.parseInt(split[0]));
                if (split[3].equals("IN_PROGRESS")) {
                    tasks.get(Integer.parseInt(split[0])).setStatus(Status.IN_PROGRESS);
                } else if (split[3].equals("DONE")) {
                    tasks.get(Integer.parseInt(split[0])).setStatus(Status.DONE);
                }
                break;
            case ("EPIC"):
                addEpic(new Epic(split[2], split[4]), Integer.parseInt(split[0]));
                if (split[3].equals("IN_PROGRESS")) {
                    epics.get(Integer.parseInt(split[0])).setStatus(Status.IN_PROGRESS);
                } else if (split[3].equals("DONE")) {
                    epics.get(Integer.parseInt(split[0])).setStatus(Status.DONE);
                }
                break;
            case ("SUBTASK"):
                addSubtask(new Subtask(split[2], split[4]), Integer.parseInt(split[5]), Integer.parseInt(split[0]));
                if (split[3].equals("IN_PROGRESS")) {
                    subtasks.get(Integer.parseInt(split[0])).setStatus(Status.IN_PROGRESS);
                } else if (split[3].equals("DONE")) {
                    subtasks.get(Integer.parseInt(split[0])).setStatus(Status.DONE);
                }
                break;
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
    public void addSubtask(Subtask subtask, String epicName) {
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
