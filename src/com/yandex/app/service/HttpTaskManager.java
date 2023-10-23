package com.yandex.app.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.yandex.app.converter.SubtaskDeserializer;
import com.yandex.app.converter.SubtaskSerializer;
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
            .registerTypeAdapter(Subtask.class, new SubtaskDeserializer())
            .registerTypeAdapter(Subtask.class, new SubtaskSerializer())
            .serializeNulls()
            .create();
    public static KVTaskClient kvTaskClient;

    public HttpTaskManager(String urlString) {
        try {
            kvTaskClient = new KVTaskClient(urlString);
            setCurrentId();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
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
        loadTasksToMemory();
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

    public void loadTasksToMemory() {
        try {
            List<JsonElement> tasksList = kvTaskClient.load();
            for (JsonElement jsonElement : tasksList) {
                JsonObject taskJO = jsonElement.getAsJsonObject();
                switch (taskJO.get("type").getAsString()) {
                    case ("Task"):
                        Task task = gson.fromJson(taskJO, Task.class);
                        addTask(task, task.getIdentifier());
                        break;
                    case ("Epic"):
                        Epic epic = gson.fromJson(taskJO, Epic.class);
                        addEpic(epic, epic.getIdentifier());
                        break;
                }
            }
            for (JsonElement jsonElement : tasksList) {
                JsonObject taskJO = jsonElement.getAsJsonObject();
                if (taskJO.get("type").getAsString().equals("Subtask")) {
                    Subtask subtask = gson.fromJson(taskJO, Subtask.class);
                    addSubtask(subtask, taskJO.get("epicId").getAsInt(), subtask.getIdentifier());
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
