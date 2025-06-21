package tasks;

import static tasks.TaskType.EPIC;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> epicSubtasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
        endTime = LocalDateTime.MIN;
    }

    public Epic(Integer id, String title, String description) {
        super(id, title, description);
        endTime = LocalDateTime.MIN;
    }

    public Epic(Integer id, String title, String description, TaskStatus taskStatus,
                LocalDateTime startTime, Duration duration) {
        super(id, title, description, taskStatus, startTime, duration);
        endTime = LocalDateTime.MIN;
    }

    public Epic(Epic epic) {
        super(epic.getId(), epic.getTitle(), epic.getDescription(), epic.getStatus());
        endTime = epic.getEndTime();
        epicSubtasks = epic.getSubtaskList();
    }

    public List<Integer> getSubtaskList() {
        return epicSubtasks;
    }

    public void addSubtask(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("id не может быть равно нулю.");
        }
        if (id.equals(this.getId())) {
            throw new IllegalArgumentException("Эпик не может быть своей же подзадачей.");
        }
        epicSubtasks.add(id);
    }

    public void removeSubtask(Integer id) {
        epicSubtasks.remove(id);
    }

    @Override
    public TaskType getType() {
        return EPIC;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        if (endTime != null) {
            this.endTime = endTime;
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "Name:" + getTitle() + " \\ " +
                this.getDescription() +
                "|ID:" + this.getId() +
                "|Status:" + this.getStatus() +
                "|SubTask:" + epicSubtasks +
                "|StartTime:" + this.getStartTime() +
                "|Duration:" + this.getDuration() +
                "|EndTime:" + endTime +
                '}';
    }
}
