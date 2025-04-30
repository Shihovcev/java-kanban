package manager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Subtask> subtask;
    private HashMap<Integer, Epic> epic;
    private HistoryManager history;
    private Integer taskId;

    public InMemoryTaskManager() {
        taskId = 1;
        tasks = new HashMap<>();
        subtask = new HashMap<>();
        epic = new HashMap<>();
        history = Manager.getDefaultHistory();
    }

    @Override
    public ArrayList<Task> getAllTask() { // Показать все задачи
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Task> getAllSubtask() { // Показать все подзадачи
        return new ArrayList<>(subtask.values());
    }

    @Override
    public ArrayList<Task> getAllEpic() { // Показать все эпики
        return new ArrayList<>(epic.values());
    }

    // Final
    @Override
    public void deleteAllTask() { // удаление всех задач
        tasks.clear();
    }


    @Override
    public void deleteAllSubtask() { // удаление всех подзадач
        for (Subtask subTask : subtask.values()) {
            epic.get(subTask.getEpicTask()).removeSubtask(subTask.getId());
        }
        for (Epic epicTask : epic.values()) {
            updateStatus(epicTask.getId());
        }
        subtask.clear();
    }

    // Final
    @Override
    public void deleteAllEpic() { // удаление всех эпиков
        epic.clear();
        subtask.clear();
    }

    // Final
    @Override
    public Task getTaskById(Integer taskId) { // Возвращаем по ID задачу
        history.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    // Final
    @Override
    public Subtask getSubtaskById(Integer taskId) { // Возвращаем по ID подзадачи
        history.add(subtask.get(taskId));
        return subtask.get(taskId);
    }

    // Final
    @Override
    public Epic getEpicById(Integer taskId) { // Возвращаем по ID Эпики
        history.add(epic.get(taskId));
        return epic.get(taskId);
    }

    // Final
    @Override
    public void addTask(Task task) { // создание новой задачи
        task.setId(generateNewId());
        tasks.put(task.getId(), task);
    }

    // Final
    @Override
    public void addEpic(Epic epic) { // создание нового эпика
        epic.setId(generateNewId());
        this.epic.put(epic.getId(), epic);
    }

    // Final
    @Override
    public void addSubtask(Subtask subtask) { // создание новой подзадачи
        subtask.setId(generateNewId());
        this.subtask.put(subtask.getId(), subtask);
    }

    // Final
    @Override
    public void updateTask(Task taskUpdate) { // Обновление задачи
        tasks.put(taskUpdate.getId(), taskUpdate);
    }

    // Final
    @Override
    public void updateEpic(Epic epicUpdate) {   // обновлние эпика
        epic.put(epicUpdate.getId(), epicUpdate);
        updateStatus(epicUpdate.getId());
    }

    // Final
    @Override
    public void updateSubtask(Subtask subtaskUpdate) { // обновление подзадачи
        subtask.put(subtaskUpdate.getId(), subtaskUpdate);
        updateStatus(subtaskUpdate.getEpicTask());
    }

    // Final
    @Override
    public void deleteTaskById(Integer id) { //удаление задачи по ID
        tasks.remove(id);
    }

    // Final
    @Override
    public void deleteSubtaskById(Integer id) { //удаление подзадачи по ID
        Integer epicId = subtask.get(id).getEpicTask();
        Epic epicTask = epic.get(epicId);
        epicTask.removeSubtask(id);
        //epic.get(subtask.get(id).getEpicTask()).removeSubtask(id);
        this.subtask.remove(id);
        updateStatus(epicId);
    }

    // Final
    @Override
    public void deleteEpicById(Integer id) { // удаление эпика по ID
        ArrayList<Integer> subTaskIdList = epic.get(id).getSubtaskList();
        for (Integer subTaskId : subTaskIdList) {
            subtask.remove(subTaskId);
        }
        this.epic.remove(id);
    }

    // Final
    @Override
    public ArrayList<Task> getHistory() {
        return history.getHistory();
    }

    // Final
    private Integer generateNewId() {
        return taskId++;
    }

    // Ready
    private void updateStatus(Integer epicId) { //ЗАмена статуса у эпика
        int allInProgress = 0;
        int allDone = 0;

        if (epicId == null) {
            return;
        }

        if (!epic.containsKey(epicId)) {
            return;
        }

        if (epic.get(epicId).getSubtaskList().isEmpty()) {
            epic.get(epicId).setStatus(Status.NEW);
        }

        for (Integer subTaskId : epic.get(epicId).getSubtaskList()) {
            if (subtask.get(subTaskId).getStatus() == Status.IN_PROGRESS) {
                allInProgress++;
            } else if (subtask.get(subTaskId).getStatus() == Status.DONE) {
                allDone++;
            }
        }
        if (allDone > 0 && allDone == epic.get(epicId).getSubtaskList().size()) {
            epic.get(epicId).setStatus(Status.DONE);
        } else if (allInProgress > 0 || allDone > 0) {
            epic.get(epicId).setStatus(Status.IN_PROGRESS);
        } else {
            epic.get(epicId).setStatus(Status.NEW);
        }
    }
}
