import manager.*;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Manager.getDefault();

        Task task1 = new Task("Задача 1", "Описание 1");
        Task task2 = new Task("Задача 2", "Описание 2");
        Epic epic1 = new Epic("Задача эпика 1", "Описание эпика 1");
        Epic epic2 = new Epic("Задача эпика 2", "Описание эпика 2");
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1");
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2");
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3");

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        manager.getEpicById(epic1.getId()).addSubtask(subtask1.getId());
        manager.getEpicById(epic1.getId()).addSubtask(subtask2.getId());
        manager.getEpicById(epic2.getId()).addSubtask(subtask3.getId());

        manager.getSubtaskById(subtask1.getId()).setEpicTask(epic1.getId());
        manager.getSubtaskById(subtask2.getId()).setEpicTask(epic1.getId());
        manager.getSubtaskById(subtask3.getId()).setEpicTask(epic2.getId());


        System.out.println("Все созданные задачи");
        System.out.println(manager.getAllTask());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtask());
        System.out.println("-----------------------------");

        manager.getTaskById(task1.getId()).setStatus(Status.IN_PROGRESS);
        manager.getTaskById(task2.getId()).setStatus(Status.IN_PROGRESS);
        manager.getSubtaskById(subtask1.getId()).setStatus(Status.IN_PROGRESS);
        manager.updateEpic(manager.getEpicById(manager.getSubtaskById(subtask1.getId()).getEpicTask()));

        manager.getSubtaskById(subtask3.getId()).setStatus(Status.DONE);
        /*
        Integer subTaskID = subtask3.getId();
        Subtask subTask = manager.getSubtaskById(subTaskID);
        Integer epicTaskID = subTask.getEpicTask();
        Epic epicTask = manager.getEpicById(epicTaskID);
        manager.updateEpic(epicTask);
        */
        manager.updateEpic(manager.getEpicById(manager.getSubtaskById(subtask3.getId()).getEpicTask()));
        ;

        System.out.println("Все вызванные задачи");
        System.out.println(manager.getAllTask());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtask());
        System.out.println("-----------------------------");

        manager.deleteTaskById(task2.getId());
        manager.deleteSubtaskById(subtask1.getId());
        manager.deleteEpicById(epic2.getId());

        System.out.println("Все удаленные задачи");
        System.out.println(manager.getAllTask());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubtask());
        System.out.println("-----------------------------");

        System.out.println("История задач");
        System.out.println(manager.getHistory());
    }
}




