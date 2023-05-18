import java.util.HashMap;
import java.util.Objects;

public class TaskManager {
    private int identifier;
    private int identifier() {
        return identifier++;
    }
    HashMap<Integer, Object>  tasks = new HashMap<>();

    public void addTask(Object object) {
        tasks.put(identifier(), object);
    }
}
