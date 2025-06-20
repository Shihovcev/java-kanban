package manager;

import static tasks.TaskStatus.NEW;
import static tasks.TaskStatus.IN_PROGRESS;
import static tasks.TaskStatus.DONE;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;


public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Subtask> subtasks;
    protected final Map<Integer, Epic> epics;
    protected final HistoryManager history;
    protected Integer taskId;
    protected final Set<Task> taskPriorityList;

    public InMemoryTaskManager() {
        taskId = 1;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        history = Manager.getDefaultHistory();
        taskPriorityList = new TreeSet<>(Comparator.naturalOrder());
    }

    public void setGlobalTaskId(Integer id) {
        taskId = id;
    }

    @Override
    public ArrayList<Task> getAllTask() { // Показать все задачи
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() { // Показать все подзадачи
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpic() { // Показать все эпики
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllTask() { // удаление всех задач
        if (tasks.isEmpty()) {
            return;
        }
        tasks.keySet()
                .forEach(id -> {
                    history.remove(id);
                    taskPriorityList.remove(tasks.get(id));
                });
        tasks.clear();
    }


    @Override
    public void deleteAllSubtask() { // удаление всех подзадач
        if (subtasks.isEmpty()) {
            return;
        }
        List<Integer> subtasksToRemove = new ArrayList<>(subtasks.keySet());
        subtasksToRemove.forEach(id -> taskPriorityList.remove(getSubtaskById(id)));
        subtasksToRemove.forEach(this::deleteSubtaskById);
    }

    // Final
    @Override
    public void deleteAllEpic() { // удаление всех эпиков
        if (epics.isEmpty()) {
            return;
        }
        List<Integer> epicsToRemove = new ArrayList<>(epics.keySet());
        epicsToRemove.forEach(this::deleteEpicById);
    }

    @Override
    public Task getTaskById(Integer id) { // Возвращаем по ID задачу
        if (id == null) {
            throw new IllegalArgumentException("ID задачи не должно быть null.");
        }
        Task task = tasks.get(id);
        if (task == null) {
            throw new NoSuchElementException("Task с id: "
                    + id + " не найден в менеджере.");
        }
        history.add(tasks.get(id));
        return new Task(task);
    }

    // Final
    @Override
    public Subtask getSubtaskById(Integer id) { // Возвращаем по ID подзадачи
        if (id == null) {
            throw new IllegalArgumentException("ID подзадачи не должно быть null.");
        }
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            throw new NoSuchElementException("Subtask с id: "
                    + id + " не найден в менеджере.");
        }
        history.add(subtasks.get(id));
        return new Subtask(subtask);
    }

    // Final
    @Override
    public Epic getEpicById(Integer id) { // Возвращаем по ID Эпики
        if (id == null) {
            throw new IllegalArgumentException("ID эпика не должно быть null.");
        }
        Epic epic = epics.get(id);
        if (epic == null) {
            throw new NoSuchElementException("Epic с id: "
                    + id + " не найден в менеджере.");
        }
        history.add(epics.get(id));
        return new Epic(epic);
    }

    // Final
    @Override
    public void addTask(Task task) { // создание новой задачи
        if (task == null) {
            throw new IllegalArgumentException("Task не должна быть null.");
        }
        if (task.getId() == 0) {
            task.setId(generateNewId());
        }
        if (task.getStatus() == null) {
            task.setStatus(NEW);
        }
        if (!checkTimeOverlap(task)) {
            throw new TimeOverlapException("Задача с id: "
                    + task.getId() + " пересекается по времени выполнения с другими задачами.");
        }
        tasks.put(task.getId(), new Task(task));
        taskPriorityList.add(task);
    }

    @Override
    public void addEpic(Epic epic) { // создание нового эпика
        if (epic == null) {
            throw new IllegalArgumentException("Epic не должен быть null.");
        }
        if (epic.getId() == 0) {
            epic.setId(generateNewId());
        }

        epics.put(epic.getId(), new Epic(epic));

        if (!epic.getSubtaskList().isEmpty()) {
            epic = updateEpicStatus(epic).orElseThrow(() ->
                    new IllegalArgumentException("Обновление статуса Epic невозможно: " +
                            "Epic не добавлен в менеджер.")
            );
            epics.put(epic.getId(), new Epic(epic));
        }
    }

    @Override
    public void addSubtask(Subtask subtask) { // создание новой подзадачи
        if (subtask == null) {
            throw new IllegalArgumentException("Subtask не должен быть null.");
        }

        int epicId = subtask.getEpicTask();

        if (!epics.containsKey(epicId)) {
            throw new IllegalArgumentException("Указанный в Subtask Epic не найден в менеджере.");
        }
        if (!checkTimeOverlap(subtask)) {
            throw new TimeOverlapException("Подзадача с id: "
                    + subtask.getId() + " пересекается по времени выполнения с другими задачами.");
        }
        if (subtask.getId() == 0) {
            subtask.setId(generateNewId());
        }
        if (subtask.getStatus() == null) {
            subtask.setStatus(NEW);
        }
        taskPriorityList.add(subtask);

        Epic epic = new Epic(epics.get(epicId));
        epic.addSubtask(subtask.getId());
        subtasks.put(subtask.getId(), new Subtask(subtask));
        epic = updateEpicTime(epic);
        epic = updateEpicStatus(epic).orElseThrow(() ->
                new IllegalArgumentException("Обновление статуса Epic невозможно: " +
                        "Epic не добавлен в менеджер.")
        );
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task не должен быть null.");
        }
        if (!tasks.containsKey(task.getId())) {
            throw new NoSuchElementException("Обновляемый Task с id: "
                    + task.getId() + " не найден в менеджере.");
        }
        if (!checkTimeOverlap(task)) {
            throw new TimeOverlapException("Обновляемая задача с id: "
                    + task.getId() + " пересекается по времени выполнения с другими задачами.");
        }
        Task previousTask = tasks.get(task.getId());
        taskPriorityList.remove(previousTask);
        taskPriorityList.add(task);
        tasks.put(task.getId(), task);
    }

    // Final
    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            throw new IllegalArgumentException("Epic не должен быть null.");
        }
        if (!epics.containsKey(epic.getId())) {
            throw new NoSuchElementException("Обновляемый Epic с id: "
                    + epic.getId() + " не найден в менеджере.");
        }
        epic = updateEpicTime(epic);
        epic = updateEpicStatus(epic).orElseThrow(() ->
                new IllegalArgumentException("Обновление статуса Epic невозможно: " +
                        "Epic не добавлен в менеджер.")
        );
        epics.put(epic.getId(), new Epic(epic));
    }

    // Final
    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new IllegalArgumentException("Subtask не должен быть null.");
        }
        if (!subtasks.containsKey(subtask.getId())) {
            throw new NoSuchElementException("Обновляемый Subtask с id: "
                    + subtask.getId() + " не найден в менеджере.");
        }
        if (!checkTimeOverlap(subtask)) {
            throw new TimeOverlapException("Обновляемый подзадача с id: "
                    + subtask.getId() + " пересекается по времени выполнения с другими задачами.");
        }

        int subtaskId = subtask.getId();
        Subtask previousSubtask = subtasks.get(subtaskId);
        taskPriorityList.remove(previousSubtask);
        taskPriorityList.add(subtask);
        subtasks.put(subtaskId, new Subtask(subtask));

        if (!epics.containsKey(subtask.getEpicTask())) {
            throw new IllegalStateException("Нарушена целостность данных: "
                    + "Subtask с id: " + subtask.getId()
                    + " содержит ссылку на epic: " + subtask.getEpicTask()
                    + ", но данный данный epic не найден в менеджере.");
        }

        Epic epic = epics.get(subtask.getEpicTask());

        if (!epic.getSubtaskList().contains(subtask.getId())) {
            throw new IllegalStateException("Нарушена целостность данных: "
                    + "Subtask c id: " + subtask.getId()
                    + " не зарегистрирована в epic с id:" + epic.getId()
                    + ", как его подзадача.");
        }

        epic = updateEpicTime(epic);
        epic = updateEpicStatus(epic).orElseThrow(() ->
                new IllegalArgumentException("Обновление статуса Epic невозможно: " +
                        "Epic не добавлен в менеджер.")
        );
        epics.put(epic.getId(), epic);
    }

    // Final
    @Override
    public void deleteTaskById(Integer id) { //удаление задачи по ID
        if (id == null) {
            throw new IllegalArgumentException("ID удаляемой задачи не должно быть null.");
        }

        Task task = tasks.get(id);

        if (task == null) {
            throw new NoSuchElementException("Task с id: " + id + " не найдена.");
        }
        taskPriorityList.remove(task);
        history.remove(id);
        tasks.remove(id);
    }

    // Final
    @Override
    public void deleteSubtaskById(Integer id) { //удаление подзадачи по ID
        if (id == null) {
            throw new IllegalArgumentException("ID удаляемой подзадачи не должно быть null.");
        }

        Subtask sub = subtasks.get(id);

        if (sub == null) {
            throw new NoSuchElementException("Subtask с id: " + id + " не найдена.");
        }
        taskPriorityList.remove(sub);
        history.remove(id);
        subtasks.remove(id);

        Epic epic = epics.get(sub.getEpicTask());
        if (epic != null) {
            if (epic.getSubtaskList().contains(sub.getId())) {
                epic.removeSubtask(id);
                Epic updateEpic = updateEpicStatus(epic).orElseThrow(() ->
                        new IllegalArgumentException("Обновление статуса Epic невозможно: " +
                                "Epic не добавлен в менеджер.")
                );
                updateEpic = updateEpicTime(updateEpic);
                epics.put(updateEpic.getId(), updateEpic);
            } else {
                throw new IllegalStateException("Нарушена целостность данных: "
                        + "Subtask c id: " + id
                        + " не зарегистрирована в epic с id:" + epic.getId()
                        + ", как его подзадача.");
            }
        }
    }

    // Final
    @Override
    public void deleteEpicById(Integer id) { // удаление эпика по ID
        if (id == null) {
            throw new IllegalArgumentException("ID удаляемого эпика не должно быть null.");
        }

        Epic epic = epics.get(id);

        if (epic == null) {
            throw new NoSuchElementException("Epic с id: " + id + " не найден.");
        }

        List<Integer> subTaskIdList = epics.get(id).getSubtaskList();
        if (!subTaskIdList.isEmpty()) {
            for (Integer subTaskId : subTaskIdList) {
                taskPriorityList.remove(subtasks.get(subTaskId));
                subtasks.remove(subTaskId);
                history.remove(subTaskId);
            }
        }
        epics.remove(id);
        history.remove(id);
    }

    // Final
    @Override
    public ArrayList<Task> getHistory() {
        return history.getHistory();
    }

    public List<Task> getPrioritizedTasks() {
        return taskPriorityList.stream().toList();
    }

    protected boolean checkTimeOverlap(Task task) {

        if (taskPriorityList.isEmpty())
            return true;

        if (task.getStartTime() == null)
            return true;

        return taskPriorityList
                .stream()
                .filter(priorityTask -> !Objects.equals(priorityTask.getId(), task.getId()))
                .filter(priorityTask -> priorityTask.getStartTime() != null)
                .filter(priorityTask -> priorityTask.getStartTime().isEqual(task.getStartTime())
                        || (priorityTask.getStartTime().isBefore(task.getStartTime())
                        && priorityTask.getEndTime().isAfter(task.getStartTime()))
                        || (priorityTask.getStartTime().isAfter(task.getStartTime())
                        && priorityTask.getStartTime().isBefore(task.getEndTime())))
                .findFirst()
                .isEmpty();
    }

    // Final
    private Integer generateNewId() {
        return taskId++;
    }

    protected Epic updateEpicTime(Epic epic) {

        Epic updateTimeEpic = new Epic(epic);

        updateTimeEpic.setDuration(Duration.ZERO);
        updateTimeEpic.setStartTime(LocalDateTime.MIN);
        updateTimeEpic.setEndTime(LocalDateTime.MIN);

        if (updateTimeEpic.getSubtaskList().isEmpty()) {
            return updateTimeEpic;
        }

        LocalDateTime subtaskStartTime;
        LocalDateTime subtaskEndTime;
        Duration subtaskDuration;

        for (Integer id : updateTimeEpic.getSubtaskList()) {
            if (subtasks.get(id).getStartTime() != LocalDateTime.MIN) {
                subtaskStartTime = subtasks.get(id).getStartTime();
                subtaskDuration = subtasks.get(id).getDuration();
                subtaskEndTime = subtasks.get(id).getEndTime();

                if (updateTimeEpic.getSubtaskList().size() == 1) {
                    updateTimeEpic.setStartTime(subtaskStartTime);
                    updateTimeEpic.setDuration(subtaskDuration);
                    updateTimeEpic.setEndTime(subtaskEndTime);
                    return updateTimeEpic;
                }
                if (updateTimeEpic.getStartTime() == LocalDateTime.MIN) {
                    updateTimeEpic.setStartTime(subtaskStartTime);
                    updateTimeEpic.setEndTime(subtaskEndTime);
                }
                updateTimeEpic.setDuration(updateTimeEpic.getDuration().plus(subtaskDuration));
                if (subtaskStartTime.isBefore(updateTimeEpic.getStartTime())) {
                    updateTimeEpic.setStartTime(subtaskStartTime);
                }
                if (subtaskEndTime.isAfter(updateTimeEpic.getEndTime())) {
                    updateTimeEpic.setEndTime(subtaskEndTime);
                }
            }
        }
        return updateTimeEpic;
    }

    // Ready
    private Optional<Epic> updateEpicStatus(Epic epic) { //Обновление статуса у эпика

        int allInProgress = 0;
        int allDone = 0;

        if (epic == null) {
            return Optional.empty();
        }

        if (!epics.containsKey(epic.getId())) {
            return Optional.empty();
        }

        if (epic.getSubtaskList().isEmpty()) {
            epic.setStatus(NEW);
            return Optional.of(epic);
        }

        for (Integer subTaskId : epic.getSubtaskList()) {
            if (subtasks.get(subTaskId).getStatus() == IN_PROGRESS) {
                allInProgress++;
                break;
            } else if (subtasks.get(subTaskId).getStatus() == DONE) {
                allDone++;
            }
        }
        if (allDone > 0 && allDone == epic.getSubtaskList().size()) {
            epic.setStatus(DONE);
        } else if (allInProgress > 0 || allDone > 0) {
            epic.setStatus(IN_PROGRESS);
        } else {
            epic.setStatus(NEW);
        }
        return Optional.of(epic);
    }
}