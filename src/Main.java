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
        Subtask subtask1 = new Subtask("Подзадача 1","Описание подзадачи 1");
        Subtask subtask2 = new Subtask("Подзадача 2","Описание подзадачи 2");
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.getEpicById(epic1.getId()).addSubtask(subtask1);
        manager.getEpicById(epic1.getId()).addSubtask(subtask2);

        manager.getSubtaskById(subtask1.getId()).setEpicTask(epic1);
        manager.getSubtaskById(subtask2.getId()).setEpicTask(epic1);


        Epic epic2 = new Epic("Задача эпика 2", "Описание эпика 2");
        Subtask subtask3 = new Subtask("Подзадача 3","Описание подзадачи 3");
        manager.addEpic(epic2);
        manager.addSubtask(subtask3);
        manager.getEpicById(epic2.getId()).addSubtask(subtask3);
        manager.getSubtaskById(subtask3.getId()).setEpicTask(epic2);

        System.out.println(manager.getAllTask());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtask());

        manager.getTaskById(2).setStatus(Status.IN_PROGRESS);
        manager.getSubtaskById(4).setStatus(Status.IN_PROGRESS);
        manager.getSubtaskById(4).getEpicTask().updateStatus();

        manager.getSubtaskById(7).setStatus(Status.DONE);
        manager.getSubtaskById(7).getEpicTask().updateStatus();

        System.out.println(manager.getAllTask());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtask());

        manager.deleteTaskById(2);
        manager.deleteSubtaskById(4);
        manager.deleteEpicById(6);

        System.out.println(manager.getAllTask());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtask());
    }
}




