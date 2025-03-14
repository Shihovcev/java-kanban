import Tasks.Task;
import Enum.Status;
import java.util.ArrayList;
import java.util.HashMap;
public class Manadger {
    static int id = 1;
    HashMap<Integer,Task> tasks = new HashMap<>();
    ArrayList<Task> standartTask = new ArrayList<>();


    Task task = new Task(1,"Сделать задание", "Выполнить задание 4 спринта", Status.NEW);


    public static Task createTask(String title, String description, Status status){
        Task task = new Task(id, title, description, status);
        Manadger.id++;
        return task;
    }
}
