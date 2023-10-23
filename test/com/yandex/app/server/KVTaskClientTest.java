package com.yandex.app.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.yandex.app.converter.TaskDeserializer;
import com.yandex.app.converter.TaskSerializer;
import com.yandex.app.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KVTaskClientTest {
    static KVServer kvServer;
    static KVTaskClient kvTaskClient;
    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Task.class, new TaskSerializer())
            .registerTypeAdapter(Task.class, new TaskDeserializer())
            .serializeNulls()
            .create();

    @BeforeEach
    void setUp() {
        try {
            kvServer = new KVServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        kvServer.start();
        try {
            kvTaskClient = new KVTaskClient("http://localhost:8078");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() {
        kvServer.stop();
    }

    @Test
    void shouldLoadEmptyDataAtStart() {
        ArrayList<JsonElement> jsonElements = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            try {
                JsonElement TaskJE = kvTaskClient.load(Integer.toString(i));
                if (TaskJE.equals("null")) {
                    jsonElements.add(TaskJE);
                }
            } catch (IOException | InterruptedException e) {
            }
        }
        assertEquals(0, jsonElements.size(), "При создании сервера в нем присуствуют объекты");
    }

    @Test
    void shouldSaveAndLoadTask() throws IOException, InterruptedException {
        Task task = new Task("Домашние дела", "Помыть посуду");
        kvTaskClient.put("1", gson.toJson(task));
        JsonObject TaskJO = kvTaskClient.load("1").getAsJsonObject();
        Task loadedTask = gson.fromJson(TaskJO, Task.class);
        assertEquals(task, loadedTask, "Задачи после загрузки не совпадают");
    }

    @Test
    void shouldLoadEmptyDataAfterDelete() throws IOException, InterruptedException {
        Task task = new Task("Домашние дела", "Помыть посуду");
        kvTaskClient.put("5", gson.toJson(task));
        kvTaskClient.delete();
        ArrayList<JsonElement> jsonElements = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            try {
                JsonElement TaskJE = kvTaskClient.load(Integer.toString(i));
                if (TaskJE.equals("null")) {
                    jsonElements.add(TaskJE);
                }
            } catch (IOException | InterruptedException e) {
            }
        }
        assertEquals(0, jsonElements.size(), "При создании сервера в нем присуствуют объекты");
    }
}