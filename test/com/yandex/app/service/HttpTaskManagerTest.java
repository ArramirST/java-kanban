package com.yandex.app.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.yandex.app.converter.TaskDeserializer;
import com.yandex.app.converter.TaskSerializer;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Subtask;
import com.yandex.app.model.Task;
import com.yandex.app.server.KVServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.yandex.app.service.HttpTaskManager.kvTaskClient;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest {
    static KVServer kvServer;
    static HttpTaskManager inMemoryTaskManager;
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
        ;
        kvServer.start();
        inMemoryTaskManager = Managers.getServerDefault();
    }

    @AfterEach
    void tearDown() {
        kvServer.stop();
    }

    @Test
    void shouldSaveCurrentState() throws IOException, InterruptedException {
        Epic epic = new Epic("Учеба", "Нужно учиться");
        inMemoryTaskManager.addEpic(epic, 1);
        inMemoryTaskManager.getEpic(1);
        inMemoryTaskManager.save();
        inMemoryTaskManager.epics.remove(1);
        inMemoryTaskManager.tryFindTaskAndEpic(10);
        JsonObject TaskJO = kvTaskClient.load("1").getAsJsonObject();
        Epic loadedTask = gson.fromJson(TaskJO, Epic.class);
        assertEquals("Учеба", loadedTask.getName(), "Эпик не сохранятеся в памяти");
    }

    @Test
    void shouldLoadState() throws IOException, InterruptedException {
        Task task = new Task("Домашние дела", "Помыть посуду");
        Epic epic = new Epic("Учеба", "Нужно учиться");
        Subtask subTask = new Subtask("Спринт 3", "Выполнить до вечера");
        inMemoryTaskManager.addTask(task, 1);
        inMemoryTaskManager.addEpic(epic, 2);
        inMemoryTaskManager.addSubtask(subTask, 2, 3);
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getEpic(2);
        inMemoryTaskManager.getSubtask(3);
        HttpTaskManager inMemoryTaskManager1 = Managers.getServerDefault();
        assertEquals("[TASK','Домашние дела','NEW','Помыть посуду','null','0, EPIC','Учеба'" +
                        ",'NEW','Нужно учиться','null','0, SUBTASK','Спринт 3','NEW','Выполнить до вечера','null','0','2]",
                inMemoryTaskManager1.getHistory().toString(),
                "История нового менеджера и старого не совпадает");
    }
}