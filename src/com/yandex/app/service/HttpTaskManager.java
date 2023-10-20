package com.yandex.app.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.yandex.app.converter.TaskDeserializer;
import com.yandex.app.converter.TaskSerializer;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.server.KVTaskClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Task.class, new TaskSerializer())
            .registerTypeAdapter(Task.class, new TaskDeserializer())
            .serializeNulls()
            .create();
    public static KVTaskClient kvTaskClient;

    public HttpTaskManager(String urlString) {
        try {
            kvTaskClient = new KVTaskClient(urlString);
            setCurrentId();
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка клиента");
            ;
        }
    }

    @Override
    protected void save() {
        try {
            kvTaskClient.delete();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (Integer key : tasks.keySet()) {
            try {
                kvTaskClient.put(Integer.toString(key), gson.toJson(tasks.get(key)));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        for (Integer key : epics.keySet()) {
            try {
                String key1 = Integer.toString(key);
                kvTaskClient.put(key1, gson.toJson(epics.get(key)));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        for (Integer key : subtasks.keySet()) {
            try {
                kvTaskClient.put(Integer.toString(key), gson.toJson(subtasks.get(key)));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            String history = historyToString(historyManager);
            kvTaskClient.put("0", history);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void setCurrentId() {
        tryFindTaskAndEpic(100);
        tryFindSubtask(100);
        try {
            String history = kvTaskClient.loadHistory();
            if (history == null) {
                List<Integer> historyId = new ArrayList<>();
                historyId.add(0);
                findMaxId(historyId);
                return;
            }
            List<Integer> historyId = historyFromString(history);
            int maxId = findMaxId(historyId);
            setIdentifier(maxId);
        } catch (IOException | InterruptedException e) {

        }
    }

    public void tryFindTaskAndEpic(int count) {
        for (int i = 1; i <= count; i++) {
            try {
                JsonObject TaskJO = kvTaskClient.load(Integer.toString(i)).getAsJsonObject();
                if (TaskJO != null) {
                    switch (TaskJO.get("type").getAsString()) {
                        case ("Task"):
                            Task task = gson.fromJson(TaskJO, Task.class);
                            addTask(task, task.getIdentifier());
                            break;
                        case ("Epic"):
                            Epic epic = gson.fromJson(TaskJO, Epic.class);
                            addEpic(epic, epic.getIdentifier());
                            break;
                    }
                }
            } catch (IOException | InterruptedException e) {
            }
        }
    }

    public void tryFindSubtask(int count) {
        for (int i = 1; i <= count; i++) {
            try {
                JsonObject TaskJO = kvTaskClient.load(Integer.toString(i)).getAsJsonObject();
                if (TaskJO != null) {
                    if (TaskJO.get("type").getAsString().equals("Subtask")) {
                        Subtask subtask = gson.fromJson(TaskJO, Subtask.class);
                        addSubtask(subtask, subtask.getAttachment(), subtask.getIdentifier());
                    }
                }
            } catch (IOException | InterruptedException e) {
            }
        }
    }
}
