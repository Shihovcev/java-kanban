package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.LinkedList;

public interface TaskManager {

     ArrayList<Task> getAllTask();// Показать все задачи

     ArrayList<Task> getAllSubtask();// Показать все подзадачи

     ArrayList<Task> getAllEpic();// Показать все эпики

     void deleteAllTask(); // удаление всех задач

     void deleteAllSubtask(); // удаление всех подзадач

     void deleteAllEpic();// удаление всех эпиков

     Task getTaskById(Integer taskId); // Возвращаем по ID задачу

     Subtask getSubtaskById(Integer taskId); // Возвращаем по ID подзадачи

     Epic getEpicById(Integer taskId); // Возвращаем по ID Эпики

     void addTask(Task task);// создание новой задачи

     void addEpic(Epic epic);// создание нового эпика

     void addSubtask(Subtask subtask); // создание новой подзадачи

     void updateTask(Task task);// Обновление задачи

     void updateEpic(Epic epic);  // обновлние эпика

     void updateSubtask(Subtask subtask); // обновление подзадачи

     void deleteTaskById(Integer id); //удаление задачи по ID

     void deleteSubtaskById(Integer id); //удаление подзадачи по ID

     void deleteEpicById(Integer id);// удаление эпика по ID

     ArrayList<Task> getHistory();
}
