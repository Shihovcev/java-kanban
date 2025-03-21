import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;
import manager.Manager;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        manager.addTask(new Task("Задача 1", "Описание 1"));
        manager.addTask(new Task("Задача 2", "Описание 2"));

        Epic epic1 = new Epic("Задача эпика 1", "Описание эпика 1");
        manager.addEpic(epic1);
        manager.addSubtask(new Subtask("Подзадача 1","Описание подзадачи 1"), epic1);
        manager.addSubtask(new Subtask("Подзадача 2","Описание подзадачи 2"), epic1);

        Epic epic2 = new Epic("Задача эпика 2", "Описание эпика 2");
        manager.addEpic(epic2);
        manager.addSubtask(new Subtask("Подзадача 3","Описание подзадачи 3"), epic2);

        System.out.println(manager.getAllTask());
        System.out.println(manager.getAllSubtask());
        System.out.println(manager.getAllEpic());

        /*manager.deleteTask(2);
        manager.deleteEpic(3);*/

        manager.getSubtaskById(5).setStatus(Status.DONE);
        manager.updateEpic(epic1);

        System.out.println("-----------------------------------------------\n");

        System.out.println(manager.getAllTask());
        System.out.println(manager.getAllSubtask());
        System.out.println(manager.getAllEpic());
    }
}




