package com.yandex.app.service;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class Managers {
    private Managers() {
    }

    public static FileBackedTasksManager getDefault() {
        return new FileBackedTasksManager(Paths.get("history.txt"));
    }

    public static FileBackedTasksManager loadFromFile(Path path) {
        return new FileBackedTasksManager(path);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
