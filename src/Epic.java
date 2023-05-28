import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks = new ArrayList<>();
    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtasks(Subtask subtask) {
        this.subtasks.add(subtask);
    }
    public void removeSubtasks() {
        this.subtasks.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return name.equals(epic.name) && description.equals(epic.description)
                && status.equals(epic.status)&& subtasks.equals(epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, subtasks);
    }

    @Override
    public String toString() {
        return "Epic{" +
                ", name='" + name + '\'' +
                ", description.length='" + description.length() + '\'' +
                ", status='" + status + '\'' +
                ", subtasks='" + subtasks + '\'' +
                '}';
    }

}
