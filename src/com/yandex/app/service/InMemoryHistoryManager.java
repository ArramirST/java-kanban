package com.yandex.app.service;

import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    static List<Task> tasks = new ArrayList<>();

    public void add(Task task) {
        tasks.add(task);
        if (tasks.size()>=10) {
            tasks.remove(0);
        }
    }

    public List<Task> getHistory() {
        return tasks;
    }

}
