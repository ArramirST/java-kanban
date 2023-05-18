public class Epic extends Task {
    private String type = "Epic";
    protected String name;
    protected String description;
    @Override
    public Epic(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
