package tasks;

import java.util.ArrayList;

public class Epic extends Task{
    private final ArrayList<Integer> subtasks;

    public Epic(String title, String description) {
        super(title, description);
        subtasks = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic.getTitle(), epic.getDescription(), epic.getStatus(), epic.getId());
        subtasks = epic.getSubtaskList();
    }

    public ArrayList<Integer> getSubtaskList (){
        return subtasks;
    }

    public void addSubtask(Integer Id){
        if (Id == null) {
            throw new IllegalArgumentException("ID не может быть равно нулю.");
        }
        if (Id.equals(this.getId())) {
            throw new IllegalArgumentException("Эпик не может быть своей же подзадачей.");
        }
        if (subtasks.contains(Id)) {
            return;
        }
       
        subtasks.add(Id);
    }

    public void removeSubtask(Integer Id){
        subtasks.remove(Id);
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
