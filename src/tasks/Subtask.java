package tasks;


import static tasks.TypeTask.SUBTASK;

public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String title, String description) {
        super(title, description);
        this.epicId = 0;
    }

    public Subtask(String title, String description, Status status, Integer id, Integer epicId) {
        super(title, description, status, id);
        this.epicId = epicId;
    }

    public Subtask(Subtask subtask) {
        super(subtask.getTitle(), subtask.getDescription(), subtask.getStatus(), subtask.getId());
        epicId = subtask.getEpicTask();
    }

    public Integer getEpicTask() {
        return epicId;
    }

    public void setEpicTask(Integer id) {
        if (id.equals(this.getId())) {
            throw new IllegalArgumentException("Подзадача не может быть своим же эпиком.");
        }
        if (id.equals(this.getEpicTask())) {
            return;
        }
        this.epicId = id;
    }

    @Override
    public TypeTask getType() {
        return SUBTASK;
    }


    @Override
    public String toString() {
        return "SubTask{" +
                "id='" + id +
                ", title='" + this.getTitle() + '\'' +
                ", status=" + this.getStatus() +
                ", Epicid=" + epicId +
                '}';
    }
}
