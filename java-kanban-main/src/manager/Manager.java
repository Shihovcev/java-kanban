package manager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtask = new HashMap<>();
    private HashMap<Integer, Epic> epic = new HashMap<>();

    public ArrayList<Task> getAllTask(){ // Показать все задачи
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getAllSubtask(){ // Показать все подзадачи
        return new ArrayList<>(subtask.values());
    }

    public ArrayList<Epic> getAllEpic(){ // Показать все эпики
        return new ArrayList<>(epic.values());
    }

    public void deleteAllTask(){ // удаление всех задач
        tasks.clear();
    }

    public void deleteAllSubtask(){ // удаление всех подзадач
        for (Subtask subtask : subtask.values()) {
            subtask.getEpicTask().removeSubtask(subtask);
        }
        subtask.clear();
    }

    public void deleteAllEpic(){ // удаление всех эпиков
        epic.clear();
        deleteAllSubtask();
    }

    public Task getTaskById(Integer taskId){ // Возвращаем по ID задачу
        return tasks.get(taskId);
    }

    public Task getSubtaskById(Integer taskId){ // Возвращаем по ID подзадачи
        return subtask.get(taskId);
    }

    public Task getEpicById(Integer taskId){ // Возвращаем по ID Эпики
        return epic.get(taskId);
    }

    public void addTask(Task task){ // создание новой задачи
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic){ // создание нового эпика
        this.epic.put(epic.getId(), epic);
    }

    public void addSubtask(Subtask subtask, Epic epic){ // создание новой подзадачи
        epic.addSubtask(subtask);
        subtask.setEpicTask(epic);
        this.subtask.put(subtask.getId(), subtask);
    }

    public void updateTask (Task task){ // Обновление задачи
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic){         // обновлние эпика
        epic.updateStatus();
        this.epic.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask){ // обновление подзадачи
        subtask.getEpicTask().updateStatus();
        this.subtask.put(subtask.getId(), subtask);
    }

    public void deleteTask (Integer id){ //удаление задачи по ID
        tasks.remove(id);
    }

    public void deleteSubtask (Integer id){ //удаление подзадачи
        subtask.get(id).getEpicTask().removeSubtask(subtask.get(id));
        this.subtask.remove(id);
    }

    public void deleteEpic(Integer id){ // удаление эпика
        for (Subtask subtask : epic.get(id).getSubtaskList()) {
            this.subtask.remove(subtask.getId());
        }
        this.epic.remove(id);
    }




}
