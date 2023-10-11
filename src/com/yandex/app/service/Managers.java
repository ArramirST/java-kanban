package com.yandex.app.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Managers {
    private Managers() {
    }

    public static FileBackedTasksManager getDefault() {
        return new FileBackedTasksManager(Paths.get("history.txt"));
    }

    public static FileBackedTasksManager getDefaultClear() {
        try {
            Files.deleteIfExists(Paths.get("history.txt"));
            Files.createFile(Paths.get("history.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new FileBackedTasksManager(Paths.get("history.txt"));
    }

    public static FileBackedTasksManager loadFromFile(Path path) {
        return new FileBackedTasksManager(path);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
