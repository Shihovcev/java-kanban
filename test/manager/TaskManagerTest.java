package manager;

import static tasks.TaskStatus.DONE;
import static tasks.TaskStatus.IN_PROGRESS;
import static tasks.TaskStatus.NEW;

import org.junit.jupiter.api.BeforeEach;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    Task task1;
    Task task2;
    Task task3;
    Task newTask1;
    Epic epic1;
    Epic epic2;
    Epic newEpic1;
    Subtask subtask1;
    Subtask subtask2;
    Subtask subtask3;
    Subtask subtask4;

    @BeforeEach
    void beforeEachTest() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 28, 10, 0);
        Duration duration = Duration.ofMinutes(30);

        task1 = new Task(0, "TASK1", "TASK DESCRIPTION 1", NEW,
                start, duration);
        task2 = new Task(0, "TASK2", "TASK DESCRIPTION 2", NEW,
                start.plusMinutes(30), duration);
        task3 = new Task(0, "TASK3", "TASK DESCRIPTION 3", NEW,
                start.plusMinutes(30), duration);
        newTask1 = new Task(1, "NEW TASK1", "TASK DESCRIPTION 1", DONE,
                start.plusMinutes(30), duration);

        epic1 = new Epic("EPIC1", "EPIC DESCRIPTION 1");
        epic2 = new Epic("EPIC2", "EPIC DESCRIPTION 2");
        newEpic1 = new Epic(1, "NEW EPIC 1", "NEW EPIC DESCRIPTION 1");

        subtask1 = new Subtask(0, "Subtask1", "Subtask description 1", NEW, 0,
                start, duration);
        subtask2 = new Subtask(0, "Subtask2", "Subtask description 2", NEW, 0,
                start.plusMinutes(30), duration);
        subtask3 = new Subtask(0, "Subtask3", "Subtask description 3", NEW, 0,
                start.plusMinutes(60), duration);
        subtask4 = new Subtask(0, "Subtask4", "Subtask description 4", NEW, 0,
                start.plusMinutes(90), duration);

    }

    @Test
    void getHistory() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        assertEquals(2, taskManager.getHistory().size(), "Некорректное сохранение в истории");
    }

    @Test
    void getTaskById() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        Task returnTask = taskManager.getTaskById(task1.getId());
        assertNotEquals(returnTask, task2, "Возвращение неверной задачи.");
    }

    @Test
    void addTask() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        assertThrows(TimeOverlapException.class, () -> taskManager.addTask(task3),
                "Попытка добавить задачу пересекающуюся с другими " +
                        "по времени исполнения вызывает исключение TimeOverlapExceptions.");
        assertEquals(2, taskManager.getAllTask().size(), "Ошибка создания задач.");
    }

    @Test
    void updateTask() {
        taskManager.addTask(task1);
        taskManager.addTask(newTask1);
        taskManager.updateTask(newTask1);
        assertEquals(newTask1, taskManager.getTaskById(task1.getId()), "Задача не обновилась.");
    }

    @Test
    void deleteTask() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.deleteTaskById(task1.getId());
        assertEquals(1, taskManager.getAllTask().size());
        assertThrows(NoSuchElementException.class, () ->
                        taskManager.deleteTaskById(task1.getId()),
                "Попытка удалить задачу которой нет в менеджере " +
                        "вызывает исключение NoSuchElementException.");
        taskManager.deleteTaskById(task2.getId());
        assertTrue(taskManager.getAllTask().isEmpty());
    }

    @Test
    void deleteAllTasks() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.deleteAllTask();
        assertTrue(taskManager.getAllTask().isEmpty());
    }

    void getAllTasks() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        assertEquals(2, taskManager.getAllTask().size());
        taskManager.deleteAllTask();
        assertTrue(taskManager.getAllTask().isEmpty());
    }

    @Test
    void addEpic() {
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        assertNotNull(epic1);
        assertNotEquals(epic1, epic2);
        assertEquals(2, taskManager.getAllEpic().size());
    }

    @Test
    void updateEpic() {
        taskManager.addEpic(epic1);
        taskManager.updateEpic(newEpic1);
        assertEquals("NEW EPIC 1", taskManager.getEpicById(epic1.getId()).getTitle());
        assertEquals("NEW EPIC DESCRIPTION 1", taskManager.getEpicById(epic1.getId()).getDescription());
    }

    @Test
    void getEpicById() {
        taskManager.addEpic(epic1);
        assertEquals(taskManager.getEpicById(epic1.getId()), epic1);
        assertThrows(NoSuchElementException.class, () -> taskManager.getEpicById(24),
                "Попытка получить эпик которой нет " +
                        "в менеджере вызывает исключение NoSuchElementException.");
    }

    @Test
    void getAllEpics() {
        taskManager.addEpic(epic1);
        assertEquals(1, taskManager.getAllEpic().size());
    }

    @Test
    void deleteEpic() {
        taskManager.addEpic(epic1);
        subtask1.setEpicTask(epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.deleteEpicById(epic1.getId());
        assertEquals(0, taskManager.getAllEpic().size());
        assertEquals(0, taskManager.getAllSubtask().size());
    }

    @Test
    void deleteAllEpic() {
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.deleteAllEpic();
        assertEquals(0, taskManager.getAllEpic().size());
    }

    @Test
    void getSubtaskList() {
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        subtask1.setEpicTask(epic1.getId());
        subtask2.setEpicTask(epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        assertEquals(2, taskManager.getEpicById(epic1.getId()).getSubtaskList().size());
        assertEquals(0, taskManager.getEpicById(epic2.getId()).getSubtaskList().size());
    }

    // Beginning of module.Subtask methods:
    @Test
    void addSubtask() {
        taskManager.addEpic(epic1);
        subtask1.setEpicTask(1);
        subtask2.setEpicTask(1);
        subtask3.setEpicTask(3);
        subtask4.setEpicTask(12);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        assertThrows(IllegalArgumentException.class, () -> taskManager.addSubtask(subtask3),
                "Попытка добавить подзадачу с несуществующим эпиком " +
                        "вызывает исключение IllegalArgumentException.");
        assertThrows(IllegalArgumentException.class, () -> taskManager.addSubtask(subtask4),
                "Попытка добавить подзадачу с несуществующим эпиком " +
                        "вызывает исключение IllegalArgumentException.");
        assertEquals(2, taskManager.getAllSubtask().size());
    }

    @Test
    void updateSubtask() {
        taskManager.addEpic(epic1);
        subtask1.setEpicTask(1);
        subtask2.setEpicTask(1);
        subtask3.setEpicTask(1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        assertEquals(3, taskManager.getAllSubtask().size());

        subtask3.setStatus(IN_PROGRESS);
        taskManager.updateSubtask(subtask3);
        assertEquals(3, taskManager.getAllSubtask().size());
        assertEquals(IN_PROGRESS, taskManager.getEpicById(epic1.getId()).getStatus());
    }

    @Test
    void getSubtaskById() {
        taskManager.addEpic(epic1);
        subtask1.setEpicTask(1);
        subtask2.setEpicTask(1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        assertEquals(subtask1, taskManager.getSubtaskById(subtask1.getId()));
        assertNotEquals(subtask2, taskManager.getSubtaskById(subtask1.getId()));
        assertThrows(NoSuchElementException.class, () -> taskManager.getSubtaskById(123),
                "Попытка получить подзадачу которой нет " +
                        "в менеджере вызывает исключение NoSuchElementException.");
    }

    @Test
    void getAllSubtasks() {
        taskManager.addEpic(epic1);
        subtask1.setEpicTask(1);
        subtask2.setEpicTask(1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        assertEquals(2, taskManager.getAllSubtask().size());
    }

    @Test
    void deleteSubtask() {
        taskManager.addEpic(epic1);
        subtask1.setEpicTask(epic1.getId());
        subtask2.setEpicTask(epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.deleteSubtaskById(subtask1.getId());
        assertEquals(1, taskManager.getAllSubtask().size());
        assertThrows(NoSuchElementException.class, () -> taskManager.deleteSubtaskById(subtask1.getId()),
                "Попытка удалить подзадачу которой нет в менеджере " +
                        "вызывает исключение NoSuchElementException.");
        taskManager.deleteSubtaskById(subtask2.getId());
        assertEquals(0, taskManager.getAllSubtask().size());
    }

    @Test
    void deleteAllSubtasks() {
        taskManager.addEpic(epic1);
        subtask1.setEpicTask(1);
        subtask2.setEpicTask(1);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.deleteAllSubtask();
        assertEquals(0, taskManager.getEpicById(epic1.getId()).getSubtaskList().size());
    }
}