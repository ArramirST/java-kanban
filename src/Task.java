import java.util.Objects;

public class Task {
    private String type = "Task";
    protected String name;
    protected String description;
    protected String status = "NEW";

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return type.equals(task.type) && name.equals(task.name) && description.equals(task.description) && status.equals(task.status);
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
