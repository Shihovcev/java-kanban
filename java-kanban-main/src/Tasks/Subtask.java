package Tasks;
import Enum.Status;
public class Subtask extends Task {
    private int subtaskID;

    public Subtask(int id, String title, String description, Status status, int subtaskID) {
        super(id, title, description, status);
        this.subtaskID = subtaskID;
    }

    public int getSubtaskID() {
        return subtaskID;
    }

    public void setSubtaskID(int subtaskID) {
        this.subtaskID = subtaskID;
    }
}
