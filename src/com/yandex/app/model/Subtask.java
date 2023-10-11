package com.yandex.app.model;

import java.util.Objects;

public class Subtask extends Task {
    private int epicId;
    public Subtask(String name, String description) {
        super(name, description);
    }

    public int getAttachment() {
        return epicId;
    }

    public void setAttachment(int attachment) {
        this.epicId = attachment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtask subtask = (Subtask) o;
        return name.equals(subtask.name) && description.equals(subtask.description)
                && status.equals(subtask.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status);
    }

    @Override
    public String toString() {
        return "SUBTASK','" + name + "','" + status + "','" + description + "','" + startTime + "','" + duration + "','" + epicId;
    }
}
