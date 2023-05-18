import java.util.Objects;

public class Subtask extends Task {
    private String type = "Subtask";
    private int attachment;
    public Subtask(String name, String description) {
        super(name, description);
    }

    public int getAttachment() {
        return attachment;
    }

    public void setAttachment(int attachment) {
        this.attachment = attachment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtask subtask = (Subtask) o;
        return type.equals(subtask.type) && name.equals(subtask.name) && description.equals(subtask.description)
                && status.equals(subtask.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, description, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", description.length='" + description.length() + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
