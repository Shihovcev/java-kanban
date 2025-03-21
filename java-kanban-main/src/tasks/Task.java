package tasks;

import java.util.Objects;

public class Task {
    protected static Integer id = 1;
    private final Integer taskId;
    private String title;
    private String description;
    private Status status;

    public Task(String title, String description) {
        taskId = id++;
        this.title = title;
        this.description = description;
        status = Status.NEW;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId() {
        return taskId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return  taskId.equals(task.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }

    @Override
    public String toString() {
        return "{" +
                "ID='" + taskId +
                ", title='" + title + '\'' +
                ", status=" + status +
                '}';
    }



}
