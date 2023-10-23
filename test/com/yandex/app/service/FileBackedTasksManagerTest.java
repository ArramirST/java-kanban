package com.yandex.app.service;

import com.yandex.app.server.KVServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest {
    static KVServer kvServer;
    public static HttpTaskManager inMemoryTaskManager;

    @BeforeEach
    public void beforeEach() {
        try {
            kvServer = new KVServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        kvServer.start();
        inMemoryTaskManager = Managers.getDefault();
    }

    @AfterEach
    void tearDown() {
        kvServer.stop();
    }

    @Test
    public void shouldLoadEmptyMemory() {
        FileBackedTasksManager inMemoryTaskManager1 = Managers.getDefault();
        assertEquals("[]",
                inMemoryTaskManager1.getHistory().toString(),
                "В чистой истории присутствуют посторонние задачи");
    }
}