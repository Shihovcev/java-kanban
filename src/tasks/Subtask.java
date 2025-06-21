package tasks;

import static tasks.TaskType.SUBTASK;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String title, String description) {
        super(title, description);
        this.epicId = 0;
    }

    public Subtask(Integer id, String title, String description, TaskStatus taskStatus, Integer epicId) {
        super(id, title, description, taskStatus);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String title, String description, TaskStatus taskStatus, Integer epicId,
                   LocalDateTime startTime, Duration duration) {
        super(id, title, description, taskStatus, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(Subtask subtask) {
        super(subtask.getId(),
                subtask.getTitle(),
                subtask.getDescription(),
                subtask.getStatus(),
                subtask.getStartTime(),
                subtask.getDuration());
        epicId = subtask.getEpicTask();
    }

    public Integer getEpicTask() {
        return epicId;
    }

    public void setEpicTask(Integer id) {
        if (id.equals(this.getId())) {
            throw new IllegalArgumentException("Подзадача не может быть своим же эпиком.");
        }
        epicId = id;
    }

    @Override
    public TaskType getType() {
        return SUBTASK;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "Name:" + getTitle() + " \\ " +
                this.getDescription() +
                "|ID:" + this.getId() +
                "|Status:" + this.getStatus() +
                "|EpicID:" + epicId +
                "|StartTime:" + this.getStartTime() +
                "|Duration:" + this.getDuration() +
                "|EndTime:" + this.getEndTime() +
                '}';
    }
}
