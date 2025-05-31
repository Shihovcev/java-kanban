package tasks;

import java.util.ArrayList;
import java.util.List;

import static tasks.TypeTask.EPIC;

public class Epic extends Task {
    private List<Integer> subtasks;

    public Epic(String title, String description) {
        super(title, description);
        subtasks = new ArrayList<>();
    }

    public Epic(String title, String description, Status status, Integer id, List<Integer> subtask) {
        super(title, description, status, id);
        this.subtasks = subtask;
    }

    public Epic(Epic epic) {
        super(epic.getTitle(), epic.getDescription(), epic.getStatus(), epic.getId());
        subtasks = epic.getSubtaskList();
    }

    public List<Integer> getSubtaskList() {
        return subtasks;
    }

    public void addSubtask(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("id не может быть равно нулю.");
        }
        if (id.equals(this.getId())) {
            throw new IllegalArgumentException("Эпик не может быть своей же подзадачей.");
        }
        if (subtasks.contains(id)) {
            return;
        }

        subtasks.add(id);
    }

    public void removeSubtask(Integer id) {
        subtasks.remove(id);
    }

    @Override
    public TypeTask getType() {
        return EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id='" + this.getId() +
                ", title='" + this.getTitle() + '\'' +
                ", status=" + this.getStatus() +
                ", SubTask=" + subtasks +
                '}';
    }
}
