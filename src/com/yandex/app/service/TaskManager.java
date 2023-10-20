package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask, String epicName);

    void updateTask(Task task, Status status);

    void updateEpic(Epic epic, Status status);

    void updateSubtask(Subtask subtask, Status status);

    List<Task> getTaskList();

    List<Task> getEpicList();

    List<Task> getSubtaskList();

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

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();

    void setTaskDate(Integer id, LocalDateTime startTime, int duration);

    void setSubtaskDate(Integer id, LocalDateTime startTime, int duration);
}