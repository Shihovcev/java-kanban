package manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tasks.TaskStatus.DONE;
import static tasks.TaskStatus.NEW;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryHistoryManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    HistoryManager historyManager;
    Task taskA;
    Task taskB;
    Task taskC;
    Task taskD;

    Task task1;
    Task task2;
    Task task3;
    Task task4;
    Task task5;
    Task newTask1;
    Epic epic1;
    Epic epic2;
    Epic epic3;
    Epic epic4;
    Epic newEpic1;
    Subtask subtask1;
    Subtask subtask2;
    Subtask subtask3;
    Subtask subtask4;
    Subtask subtask5;
    Subtask subtask6;
    Subtask subtask7;
    Subtask subtask8;
    Subtask subtaskA;

    LocalDateTime start;
    Duration duration;

    @BeforeEach
    void beforeEachHistoryManagerTest() {
        taskA = new Task("Task A", "Description A");
        taskB = new Task("Task B", "Description B");
        taskC = new Task("Task C", "Description C");
        taskD = new Task("Task D", "Description D");
        taskA.setId(1);
        taskB.setId(2);
        taskC.setId(3);
        taskD.setId(4);


        start = LocalDateTime.of(2025, 1, 28, 10, 0);
        duration = Duration.ofMinutes(20);

        task1 = new Task(0, "TASK1", "TASK DESCRIPTION 1", NEW,
                start, duration);
        task2 = new Task(0, "TASK2", "TASK DESCRIPTION 2", NEW,
                start.plusMinutes(30), duration);
        task3 = new Task(0, "TASK3", "TASK DESCRIPTION 3", NEW,
                start.plusMinutes(60), duration);
        task4 = new Task(0, "TASK4", "TASK DESCRIPTION 4", NEW,
                start.plusMinutes(90), duration);
        task5 = new Task(0, "TASK5", "TASK DESCRIPTION 5", NEW,
                start.plusMinutes(120), duration);
        newTask1 = new Task(1, "NEW TASK1", "TASK DESCRIPTION 1", DONE,
                start.plusMinutes(150), duration);

        epic1 = new Epic("EPIC1", "EPIC DESCRIPTION 1");
        epic2 = new Epic("EPIC2", "EPIC DESCRIPTION 2");
        epic3 = new Epic("EPIC3", "EPIC DESCRIPTION 3");
        epic4 = new Epic("EPIC4", "EPIC DESCRIPTION 4");

        newEpic1 = new Epic(1, "NEW EPIC 1", "NEW EPIC DESCRIPTION 1");

        subtask1 = new Subtask(0, "Subtask1", "Subtask description 1", NEW, 0,
                start.plusMinutes(180), duration);
        subtask2 = new Subtask(0, "Subtask2", "Subtask description 2", NEW, 0,
                start.plusMinutes(210), duration);
        subtask3 = new Subtask(0, "Subtask3", "Subtask description 3", NEW, 0,
                start.plusMinutes(240), duration);
        subtask4 = new Subtask(0, "Subtask4", "Subtask description 4", NEW, 0,
                start.plusMinutes(270), duration);
        subtask5 = new Subtask(0, "Subtask5", "Subtask description 5", NEW, 0,
                start.plusMinutes(300), duration);
        subtask6 = new Subtask(0, "Subtask6", "Subtask description 6", NEW, 0,
                start.plusMinutes(330), duration);
        subtask7 = new Subtask(0, "Subtask7", "Subtask description 7", NEW, 0,
                start.plusMinutes(360), duration);
        subtask8 = new Subtask(0, "Subtask8", "Subtask description 8", NEW, 0,
                start.plusMinutes(390), duration);

        subtaskA = new Subtask(45, "SubtaskA", "Subtask description A", NEW, 0,
                start.plusMinutes(420), duration);

        taskManager = new InMemoryTaskManager();
        historyManager = Manager.getDefaultHistory();
    }

    @Test
    void givenNewHistoryManager_whenInitialized_thenHistoryIsEmpty() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void givenTask_whenModifiedAfterAddingToHistory_thenStoredTaskIsUnaffected() {
        historyManager.add(taskA);
        taskA.setDescription("Description B modified");
        assertNotEquals(historyManager.getHistory().getFirst().getDescription(),
                taskA.getDescription());
    }

    @Test
    void givenTask_whenAddedToHistory_thenHistoryNotEmpty() {
        historyManager.add(taskA);
        historyManager.add(taskB);
        historyManager.add(taskC);
        assertFalse(historyManager.getHistory().isEmpty());
        assertEquals(3, historyManager.getHistory().size());
    }

    @Test
    void givenThreeTasks_whenAddingAgainFirstTask_thenItBecomesLastInHistory() {
        historyManager.add(taskA);
        historyManager.add(taskB);
        historyManager.add(taskC);
        historyManager.add(taskA);
        List<Task> expectedOrder = Arrays.asList(taskB, taskC, taskA);
        assertEquals(expectedOrder, historyManager.getHistory());
    }

    @Test
    void testHistoryManager() {
        assertTrue(taskManager.getHistory().isEmpty(), "История тасков должна быть пуста");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        taskManager.addTask(task4);
        taskManager.addTask(task5);

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        subtask1.setEpicTask(epic1.getId());
        subtask2.setEpicTask(epic1.getId());
        subtask3.setEpicTask(epic1.getId());
        subtask4.setEpicTask(epic2.getId());
        subtask5.setEpicTask(epic2.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        taskManager.addSubtask(subtask4);
        taskManager.addSubtask(subtask5);

        taskManager.getTaskById(task1.getId());
        assertEquals(1, taskManager.getHistory().size(), "В истории должен быть 1 таск");

        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());
        taskManager.getTaskById(task4.getId());
        taskManager.getTaskById(task5.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getSubtaskById(subtask3.getId());
        assertEquals(10, taskManager.getHistory().size(), "Некорректное количество тасков в истории");

        taskManager.getSubtaskById(subtask4.getId());
        taskManager.getSubtaskById(subtask5.getId());
        assertEquals(12, taskManager.getHistory().size(), "В истории не 12 последних тасков");

        taskManager.getTaskById(task1.getId());
        assertEquals(task2, taskManager.getHistory().getFirst(), "Объект после повторного обращения не" +
                "переместился в конец истории");

        taskManager.getTaskById(task2.getId());
        assertEquals(12, taskManager.getHistory().size(), "История обновляется некорректно");
        assertEquals(task2, taskManager.getHistory().getLast(), "Объект после повторного обращения не" +
                "переместился в конец истории");

        taskManager.deleteAllEpic();
        assertEquals(5, taskManager.getHistory().size(), "Не удалились Эпики и их Сабтаски из истории");

        taskManager.addEpic(epic3);
        subtask6.setEpicTask(epic3.getId());
        taskManager.addSubtask(subtask6);

        taskManager.getEpicById(epic3.getId());
        taskManager.getSubtaskById(subtask6.getId());
        taskManager.deleteAllSubtask();
        assertEquals(6, taskManager.getHistory().size(), "Удаленные Сабтаски не удалились из истории");

        taskManager.deleteAllTask();
        assertEquals(1, taskManager.getHistory().size(), "Удаленные Таски не удалились из истории");

        taskManager.deleteEpicById(epic3.getId());
        assertEquals(0, taskManager.getHistory().size(), "Удаленный Эпик по id не удалился из истории");

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic4);
        subtask7.setEpicTask(epic4.getId());
        subtask8.setEpicTask(epic4.getId());
        taskManager.addSubtask(subtask7);
        taskManager.addSubtask(subtask8);

        taskManager.getEpicById(epic4.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getSubtaskById(subtask7.getId());
        taskManager.getSubtaskById(subtask8.getId());
        taskManager.getSubtaskById(subtask7.getId());
        taskManager.deleteSubtaskById(subtask7.getId());

        assertEquals(4, taskManager.getHistory().size(), "Некорректно удаляется Сабтаск из истории");

        taskManager.deleteTaskById(task2.getId());
        assertEquals(3, taskManager.getHistory().size(), "Некорректно удаляется Таск из истории");

        taskManager.deleteEpicById(epic4.getId());
        assertEquals(1, taskManager.getHistory().size(), "Удаленный Эпик по id не удалился из истории");
    }

}



