package tasks;


public class Subtask extends Task {
    private Epic epicTask;

    public Subtask(String title, String description) {
        super(title, description);
        this.epicTask = null;
    }


    public Epic getEpicTask() {
        return epicTask;
    }

    public void setEpicTask(Epic epicTask) {
        this.epicTask = epicTask;
    }
}
