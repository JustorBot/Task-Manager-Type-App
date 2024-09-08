public class Task<T> {
    private int id;
    private String taskName;
    private String description;
    private String dueDate;
    private boolean completed;
    private String category;

    public Task(int id, String taskName, String description, String dueDate, boolean completed, String category) {
        this.id = id;
        this.taskName = taskName;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = completed;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return id + ":" + taskName + ":" + dueDate + ":" + category;
    }
}
