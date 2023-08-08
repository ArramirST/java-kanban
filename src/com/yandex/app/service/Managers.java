package com.yandex.app.service;

import java.nio.file.Path;

public final class Managers {
    private Managers() {
    }

    public static FileBackedTasksManager getDefault() {
        return new FileBackedTasksManager();
    }

    public static FileBackedTasksManager loadFromFile(Path path) {
        return new FileBackedTasksManager(path);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
