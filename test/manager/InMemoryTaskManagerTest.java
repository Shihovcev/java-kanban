package manager;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static tasks.TaskStatus.DONE;
import static tasks.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

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
    void beforeEachTaskManagerTest() {
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
    }

    @Test
    void assertManagerIsWorkingCorrectly() {
        assertInstanceOf(InMemoryTaskManager.class, Manager.getDefault());
        assertInstanceOf(InMemoryHistoryManager.class, Manager.getDefaultHistory(), "getDefaultHistory" +
                " не создает экземпляр менеджера service.InMemoryHistoryManager");
        assertInstanceOf(TaskManager.class, taskManager, "service.Manager не создает проинициализированный экземпляр" +
                " менеджера service.TaskManager");
    }

    @Test
    void testTasks() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);
        int savedId = task1.getId();

        //проверка получения таска по ID + добавления в хранилище
        assertNotNull(taskManager.getTaskById(savedId), "Таск не был добавлен в хранилище");
        //проверка равенства тасков по ID
        assertEquals(task1, taskManager.getTaskById(savedId), "Таски с одинаковым id не равны друг другу" +
                " при добавлении объекта в хранилище значение полей неизменно");

        assertEquals(task1.toString(), taskManager.getTaskById(task1.getId()).toString());

        //проверка обновления таска
        taskManager.updateTask(newTask1);
        assertEquals(DONE, taskManager.getTaskById(1).getStatus(), "Таск c id=1 не обновился");

        assertNotNull(taskManager.getAllTask(), "Такси не возвращаются"); // проверка получения тасков

        taskManager.deleteTaskById(savedId); //проверка удаления таска по ID

        assertThrows(NoSuchElementException.class, () -> taskManager.getTaskById(savedId),
                "Попытка получить задачи которой нет " +
                        "в менеджере вызывает исключение NoSuchElementException.");

        taskManager.deleteAllTask(); // проверка удаления всех тасков

        assertEquals(0, taskManager.getAllTask().size(), "Таски не удалились");
    }

    @Test
    void testEpics() {
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        int savedId = epic1.getId();

        assertNotNull(taskManager.getEpicById(epic1.getId()), "Эпик не добавлен в хранилище");
        assertEquals(taskManager.getEpicById(savedId), epic1, "Эпики с одинаковым ID не равны друг другу" +
                " при добавлении объекта в хранилище значение полей неизменно");

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

        assertEquals(TaskStatus.NEW, epic2.getStatus(), "Статус эпика2 считается неверно (CORRECT-NEW)");
        assertNotNull(taskManager.getAllEpic(), "Не возвращает список эпиков");

        subtask4.setStatus(DONE);
        subtask5.setStatus(DONE);
        taskManager.updateSubtask(subtask4);
        taskManager.updateSubtask(subtask5);
        assertEquals(DONE, taskManager.getEpicById(epic2.getId()).getStatus(), "Статус эпика2 считается неверно (CORRECT-DONE)");

        subtask5.setStatus(TaskStatus.NEW);
        taskManager.updateSubtask(subtask5);
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(epic2.getId()).getStatus(), "Статус эпика2 считается неверно" +
                " (CORRECT-IN_PROGRESS)");

        taskManager.deleteSubtaskById(subtask5.getId());
        assertEquals(DONE, taskManager.getEpicById(epic2.getId()).getStatus(), "Статус эпика2 считается неверно" +
                " (CORRECT-IN_PROGRESS)");

        for (int i : taskManager.getEpicById(epic2.getId()).getSubtaskList()) {
            Subtask testSubtask = taskManager.getSubtaskById(i);
            assertNotEquals(testSubtask.getId(), taskManager.getSubtaskById(testSubtask.getId()).getEpicTask(),
                    "Эпик2 некорректно обновляет свои сабтаски");
        }

        epic2.setTitle("NEWEPIC2");
        epic2.setDescription("SOME BIG EPIC");
        taskManager.updateEpic(epic2);
        assertEquals(epic2, taskManager.getEpicById(epic2.getId()), "Некорректное обновление эпика");
        assertNotNull(taskManager.getEpicById(epic1.getId()), "Эпик не добавлен в хранилище эпиков");

        taskManager.deleteAllSubtask();
        assertTrue(taskManager.getEpicById(epic1.getId()).getSubtaskList().isEmpty(),
                "В эпиках не очистились сабтаски при их удалении");

        taskManager.deleteAllEpic();
        assertEquals(0, taskManager.getAllSubtask().size(),
                "При удалении всех эпиков не удалились сабтаски");
    }

    @Test
    void testSubtask() {

        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        subtask1.setEpicTask(epic1.getId());
        subtask2.setEpicTask(epic1.getId());
        subtask3.setEpicTask(epic1.getId());
        subtask4.setEpicTask(epic2.getId());
        subtask5.setEpicTask(epic2.getId());
        subtaskA.setEpicTask(epic2.getId());

        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);
        taskManager.addSubtask(subtask4);
        taskManager.addSubtask(subtask5);
        taskManager.addSubtask(subtaskA);

        assertEquals(subtask2.getEpicTask(), epic1.getId(), "Сабтаск не знает свой эпик");
        assertThrows(NoSuchElementException.class, () ->
                        taskManager.updateSubtask(
                                new Subtask(58, "WRONGSUBTASK", "FAKEEPICID", NEW, 7)
                        ),
                "Попытка обновить подзадачу которой нет в " +
                        "менеджере вызывает исключение NoSuchElementException.");

        assertThrows(NoSuchElementException.class, () -> taskManager.getSubtaskById(58),
                "Попытка получить подзадачи которой нет " +
                        "в менеджере вызывает исключение NoSuchElementException.");

        assertEquals(subtaskA, taskManager.getSubtaskById(45), "Сабтаски с одинаковым ID не равны");
        assertEquals(subtaskA.toString(), taskManager.getSubtaskById(45).toString());
    }

    @Test
    void testCorrectTimeValidation() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 30, 10, 0);
        duration = Duration.ofMinutes(30);

        task1.setStartTime(start);
        task2.setStartTime(start.plusMinutes(30));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        assertEquals(2, taskManager.getAllTask().size(), "Некорректная проверка временных интервалов," +
                " временные интервалы не пересекаются");
    }

    @Test
    void testTimeOverlap() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 30, 10, 0);
        duration = Duration.ofMinutes(30);

        task1.setStartTime(start);
        task2.setStartTime(start.plusMinutes(30));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        task3.setStartTime(start.plusMinutes(30));
        assertThrows(TimeOverlapException.class, () ->  taskManager.addTask(task3),
                "Попытка добавить задачу время исполнения которой пересекается с другими задачами " +
                        "в менеджере вызывает исключение TimeOverlapException.");
        assertEquals(2, taskManager.getAllTask().size(), "Некорректная проверка временных интервалов," +
                " временные интервалы пересекаются");

        task3.setStartTime(start.minusMinutes(30));
        task3.setDuration(duration.plusMinutes(30));
        assertThrows(TimeOverlapException.class, () ->  taskManager.addTask(task3),
                "Попытка добавить задачу время исполнения которой пересекается с другими задачами " +
                        "в менеджере вызывает исключение TimeOverlapException.");
        assertEquals(2, taskManager.getAllTask().size(), "Некорректная проверка временных интервалов," +
                " временные интервалы пересекаются");

        task4.setStartTime(start.minusMinutes(30));
        task4.setDuration(duration.plusMinutes(10));
        assertThrows(TimeOverlapException.class, () ->  taskManager.addTask(task4),
                "Попытка добавить задачу время исполнения которой пересекается с другими задачами " +
                        "в менеджере вызывает исключение TimeOverlapException.");

        assertEquals(2, taskManager.getAllTask().size(), "Некорректная проверка временных интервалов," +
                " временные интервалы пересекаются");

        task5.setStartTime(start.minusMinutes(30));
        task5.setDuration(duration);
        taskManager.addTask(task5);
        assertEquals(3, taskManager.getAllTask().size(), "Некорректная проверка временных интервалов," +
                " временные интервалы не пересекаются");
    }

    @Test
    void testPrioritizedTasks() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 30, 10, 0);
        Duration duration30Min = Duration.ofMinutes(30);

        task1.setStartTime(start);
        task1.setDuration(duration30Min);
        task2.setStartTime(start.plusMinutes(30));
        task2.setDuration(duration30Min);
        task3.setStartTime(start.minusMinutes(30));
        task3.setDuration(duration30Min.minusMinutes(10));
        taskManager.addTask(task2);
        taskManager.addTask(task1);
        taskManager.addTask(task3);

        assertEquals(task3, taskManager.getPrioritizedTasks().getFirst(), "1. Неверный порядок " +
                "в приоритетном списке тасков (correct: task3, task1, task2)");
        assertEquals(task2, taskManager.getPrioritizedTasks().getLast(), "2. Неверный порядок " +
                "в приоритетном списке тасков (correct: task3, task1, task2)");

        task2 = taskManager.getTaskById(task2.getId());
        task2.setStartTime(start.minusMinutes(10));
        task2.setDuration(duration30Min.minusMinutes(20));
        taskManager.updateTask(task2);

        assertEquals(task3, taskManager.getPrioritizedTasks().getFirst(), "3. Неверный порядок " +
                "в приоритетном списке тасков (correct: task3, task2, task1)");
        assertEquals(task1, taskManager.getPrioritizedTasks().getLast(), "4. Неверный порядок " +
                "в приоритетном списке тасков (correct: task3, task2, task1)");


        taskManager.addEpic(epic1);

        subtask1.setEpicTask(epic1.getId());
        subtask1.setStartTime(start.plusMinutes(60));
        subtask1.setDuration(duration30Min);
        subtask2.setEpicTask(epic1.getId());
        subtask2.setStatus(DONE);
        subtask2.setStartTime(start.plusMinutes(90));
        subtask2.setDuration(duration30Min);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(task3, taskManager.getPrioritizedTasks().getFirst(), "5. Неверный порядок " +
                "в приоритетном списке тасков после добавления epic1, subtask1, subtask2" +
                " (correct: task3, task2, task1, subtask1, subtask2)");
        assertEquals(subtask2, taskManager.getPrioritizedTasks().getLast(), "6. Неверный порядок " +
                "в приоритетном списке тасков после добавления epic1, subtask1, subtask2" +
                " (correct: task3, task2, task1, subtask1, subtask2)");
        assertEquals(5, taskManager.getPrioritizedTasks().size(), "7. Ошибка добавления тасков в" +
                " prioritizedTasks");

        subtask1 = taskManager.getSubtaskById(subtask1.getId());
        subtask1.setStartTime(start.minusMinutes(180));
        taskManager.updateSubtask(subtask1);
        assertEquals(subtask1, taskManager.getPrioritizedTasks().getFirst(), "8. Неверный порядок " +
                "в приоритетном списке Тасков после изменения subtask1 (correct:  subtask1, task3, task2," +
                " task1, subtask2)");
        assertEquals(subtask2, taskManager.getPrioritizedTasks().getLast(), "9. Неверный порядок " +
                "в приоритетном списке Тасков после изменения subtask1 (correct:  subtask1, task3, task2," +
                " task1, subtask2)");

        taskManager.deleteSubtaskById(subtask2.getId());
        assertEquals(subtask1, taskManager.getPrioritizedTasks().getFirst(), "10. Неверный порядок " +
                "в приоритетном списке Тасков после изменения subtask1 (correct: subtask1, task3, task2," +
                " task1)");
        assertEquals(task1, taskManager.getPrioritizedTasks().getLast(), "11. Неверный порядок " +
                "в приоритетном списке Тасков после изменения subtask1 (correct: subtask1, task3, task2," +
                " task1)");
        assertEquals(4, taskManager.getPrioritizedTasks().size(), "Ошибка удаления subtask2 из списка");

        taskManager.deleteAllEpic();
        assertEquals(task3, taskManager.getPrioritizedTasks().getFirst(), "12. Неверный порядок " +
                "в приоритетном списке тасков после удаления эпика (correct: task3, task2, task1)");
        assertEquals(task1, taskManager.getPrioritizedTasks().getLast(), "Неверный порядок " +
                "в приоритетном списке тасков после удаления эпика (correct: task3, task2, task1)");
        assertEquals(3, taskManager.getPrioritizedTasks().size(), "Ошибка удаления тасков в" +
                " prioritizedTasks");
    }
}