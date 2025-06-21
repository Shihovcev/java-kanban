package tasks;

import static tasks.TaskStatus.NEW;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static tasks.TaskType.TASK;

public class Task implements Comparable<Task> {

    protected Integer id;
    private String title;
    private String description;
    private TaskStatus taskStatus;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String title, String description) {
        id = 0;
        this.title = title;
        this.description = description;
        taskStatus = NEW;
        duration = Duration.ZERO;
        startTime = LocalDateTime.MIN;
    }

    public Task(Integer id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        taskStatus = NEW;
        duration = Duration.ZERO;
        startTime = LocalDateTime.MIN;
    }

    public Task(Integer id, String title, String description, TaskStatus taskStatus) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.taskStatus = taskStatus;
        startTime = LocalDateTime.MIN;
        duration = Duration.ZERO;
    }

    public Task(Integer id, String title, String description, TaskStatus taskStatus,
                LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.taskStatus = taskStatus;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(Task task) {
        this.id = task.id;
        this.title = task.title;
        this.description = task.description;
        this.taskStatus = task.taskStatus;
        this.duration = task.duration;
        this.startTime = task.startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return taskStatus;
    }

    public void setStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public TaskType getType() {
        return TASK;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Task other) {
        return this.startTime.compareTo(other.startTime);
    }

    @Override
    public String toString() {
        return "Task{" +
                "Name:" + title + " \\ " +
                description +
                "|id:" + id +
                "|Status:" + taskStatus +
                "|StartTime:" + startTime +
                "|Duration:" + duration.toMinutes() +
                "|EndTime:" + getEndTime() +
                "}";
    }
}
