import Tasks.Task;
import Enum.Status;
public class Main {

    public static void main(String[] args) {

        Task task = Manadger.createTask("Обученик", "ХУепинание", Status.NEW);
        Task task2 = Manadger.createTask("Обученик", "ХУепинание", Status.NEW);
        System.out.println(task);
        System.out.println(task2);
    }




}
