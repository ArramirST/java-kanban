package com.yandex.app.model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId = new ArrayList<>();
    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasksId;
    }

    public void addSubtasksId(Integer identifier) {
        this.subtasksId.add(identifier);
    }
    public void removeSubtasksId() {
        this.subtasksId.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return name.equals(epic.name) && description.equals(epic.description)
                && status.equals(epic.status)&& subtasksId.equals(epic.subtasksId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, subtasksId);
    }

    @Override
    public String toString() {
        return "EPIC," + name + "," + status + "," + description;
    }

}
