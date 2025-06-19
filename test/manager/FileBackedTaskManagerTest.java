package manager;

import static tasks.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    File tempTestFile;

    Task task1;
    Task task2;
    Epic epic1;
    Epic epic2;
    Subtask subtask1;
    Subtask subtask2;
    Subtask subtask3;

    LocalDateTime start;
    Duration duration;

    @BeforeEach
    void createFileAndInitializeManager() {
        start = LocalDateTime.of(2025, 1, 28, 10, 0);
        duration = Duration.ofMinutes(20);

        task1 = new Task(0, "TASK1", "TASK DESCRIPTION 1", NEW,
                start, duration);
        task2 = new Task(0, "TASK2", "TASK DESCRIPTION 2", NEW,
                start.plusMinutes(30), duration);

        epic1 = new Epic("EPIC1", "EPIC DESCRIPTION 1");
        epic2 = new Epic("EPIC2", "EPIC DESCRIPTION 2");
        subtask1 = new Subtask(0, "Subtask1", "Subtask description 1", NEW, 0,
                start.plusMinutes(180), duration);
        subtask2 = new Subtask(0, "Subtask2", "Subtask description 2", NEW, 0,
                start.plusMinutes(210), duration);
        subtask3 = new Subtask(0, "Subtask3", "Subtask description 3", NEW, 0,
                start.plusMinutes(240), duration);

        try {
            tempTestFile = File.createTempFile("tempTestFile", ".cvs");
            taskManager = new FileBackedTaskManager(tempTestFile.toPath().toFile());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка создания временного файла.");
        }

    }

    @Test
    void loadFromAndSaveEmptyFile() {
        FileBackedTaskManager managerLoadedFromFile = FileBackedTaskManager.loadFromFile(tempTestFile);
        assertNotNull(managerLoadedFromFile);
        assertTrue(managerLoadedFromFile.getHistory().isEmpty(), "История должна быть пустой");
    }

    @Test
    void writeInEmptyFile() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        subtask1.setEpicTask(epic1.getId());
        subtask2.setEpicTask(epic1.getId());
        subtask3.setEpicTask(epic2.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        assertTrue(taskManager.getHistory().isEmpty(), "История должна быть пустой");
        assertEquals(task1, taskManager.getTaskById(task1.getId()), "Таски не равны");
        assertEquals(task2, taskManager.getTaskById(task2.getId()), "Таски не равны");
        assertEquals(epic1, taskManager.getEpicById(epic1.getId()), "Эпики не равны");
        assertEquals(subtask1, taskManager.getSubtaskById(subtask1.getId()), "Сабтаски не равны");
        assertEquals(subtask2, taskManager.getSubtaskById(subtask2.getId()), "Сабтаски не равны");
        assertEquals(subtask3, taskManager.getSubtaskById(subtask3.getId()), "Сабтаски не равны");
        assertEquals(6, taskManager.getHistory().size(), "История просмотров тасков сохранена некорректно");
    }

    @Test
    void testTwoDifferentManagersFromSameFile() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        subtask1.setEpicTask(epic1.getId());
        subtask2.setEpicTask(epic1.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.save();

        assertTrue(taskManager.getPrioritizedTasks()
                .stream()
                .map(Task::getId)
                .allMatch(List.of(epic1.getId(), subtask2.getId(), subtask1.getId(),
                        task2.getId(), task1.getId())::contains), "Неккоректная запись списка задач в порядке" +
                " приоритета.");

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(tempTestFile);

        assertEquals(taskManager.getTaskById(task1.getId()), newManager.getTaskById(task1.getId()),
                "Некорректное сохранение тасков");
        assertEquals(taskManager.getTaskById(task2.getId()), newManager.getTaskById(task2.getId()),
                "Некорректное сохранение тасков");
        assertEquals(taskManager.getEpicById(epic1.getId()), newManager.getEpicById(epic1.getId()),
                "Некорректное сохранение эпиков");
        assertEquals(taskManager.getSubtaskById(subtask1.getId()), newManager.getSubtaskById(subtask1.getId()),
                "Некорректное сохранение сабтасков");
        assertEquals(taskManager.getSubtaskById(subtask2.getId()), newManager.getSubtaskById(subtask2.getId()),
                "Некорректное сохранение сабтасков");
        assertEquals(taskManager.getHistory().size(), newManager.getHistory().size(),
                "Некорректное сохранение истории просмотров тасков");
        assertEquals(taskManager.getPrioritizedTasks(), newManager.getPrioritizedTasks());
    }

    @Test
    void testThrowingIOException() {
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(new File("ERROR")));
        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(tempTestFile));
    }

    @AfterEach
    void deleteAllTempFiles() {
        tempTestFile.deleteOnExit();
    }
}