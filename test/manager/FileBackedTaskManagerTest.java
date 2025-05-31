package manager;

import org.junit.jupiter.api.*;
import tasks.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager taskManager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        taskManager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() {
        tempFile.delete();
    }

    @Test
    @DisplayName("Сохранение и загрузка пустого файла")
    void shouldSaveAndLoadEmptyFile() {
        // Сохраняем пустой менеджер
        //taskManager.save();

        // Загружаем из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getAllTask().isEmpty(), "Список задач должен быть пустым");
        assertTrue(loadedManager.getAllEpic().isEmpty(), "Список эпиков должен быть пустым");
        assertTrue(loadedManager.getAllSubtask().isEmpty(), "Список подзадач должен быть пустым");
    }

    @Test
    @DisplayName("Сохранение и загрузка нескольких задач")
    void shouldSaveAndLoadMultipleTasks() {
        // Создаем тестовые задачи
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        Epic epic1 = new Epic("Epic 1", "Epic Description 1");
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask Description 1");


        // Добавляем задачи в менеджер
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        taskManager.addSubtask(subtask1);

        epic1 = taskManager.getEpicById(epic1.getId());
        subtask1 = taskManager.getSubtaskById(subtask1.getId());
        epic1.addSubtask(subtask1.getId());
        subtask1.setEpicTask(epic1.getId());
        taskManager.updateEpic(epic1);
        taskManager.updateSubtask(subtask1);


        // Сохраняем
        taskManager.save();

        // Загружаем из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем загруженные задачи
        List<Task> loadedTasks = loadedManager.getAllTask();
        List<Epic> loadedEpics = loadedManager.getAllEpic();
        List<Subtask> loadedSubtasks = loadedManager.getAllSubtask();

        assertEquals(2, loadedTasks.size(), "Неверное количество задач");
        assertEquals(1, loadedEpics.size(), "Неверное количество эпиков");
        assertEquals(1, loadedSubtasks.size(), "Неверное количество подзадач");
        assertEquals("Task 1", loadedTasks.getFirst().getTitle(), "Название задачи не совпадает");
        assertEquals(epic1.getId(), loadedSubtasks.getFirst().getEpicTask(),
                        "Подзадача должна быть связана с эпиком");
    }


}