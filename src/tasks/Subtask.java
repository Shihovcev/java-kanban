package tasks;


public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String title, String description) {
        super(title, description);
        this.epicId = 0;
    }

    public Subtask(Subtask subtask) {
        super(subtask.getTitle(), subtask.getDescription(), subtask.getStatus(), subtask.getId());
        epicId = subtask.getEpicTask();
    }

    public Integer getEpicTask() {
        return epicId;
    }

    public void setEpicTask(Integer Id) {
        if (Id.equals(this.getId())) {
            throw new IllegalArgumentException("Подзадача не может быть своим же эпиком.");
        }
        if (Id.equals(this.getEpicTask())) {
            return;
        }
        this.epicId = Id;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id='" + id +
                ", title='" + this.getTitle() + '\'' +
                ", status=" + this.getStatus() +
                ", EpicId=" + epicId +
                '}';
    }
}
