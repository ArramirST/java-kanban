package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    int getIdentifier();
    int identifier();
    void addTask(Task task);
    void addEpic(Epic epic);
    void addSubtask(Subtask subtask, String epicName);
    void updateTask(Task task, Status status);
    void updateEpic(Epic epic, Status status);
    void updateSubtask(Subtask subtask, Status status);
    List<String> getTaskList();
    List<String> getEpicList();
    List<String> getSubtaskList();
    void removeTasks();
    void removeEpics();
    void removeSubtasks();
    int getTaskKey(String name);
    Task getTask(int identifier);
    Task getEpic(int identifier);
    Task getSubtask(int identifier);
    void removeTask(int identifier);
    void removeEpic(int identifier);
    void removeSubtask(int identifier);
    List<String> getEpicSubtasksList(String name);
    void updateEpicStatus(Epic epic);
    void updateEpicStatus(Subtask subtask);
    List<Task> getHistory();
}