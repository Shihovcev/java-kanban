package manager;

import tasks.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileBackedTaskManager  extends InMemoryTaskManager implements TaskManager {

    File file;

    FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {

        String csvStringHeader = "id,type,name,status,description,epic";
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        if (!file.exists() || !file.isFile()) {
            return taskManager;
        }

        // Загружаем текстовый файл
        String loadedRawData;
        try {
            loadedRawData = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new ManagerSaveException("Cannot read taskFile. Error: " + e.getMessage());
        }

        if (loadedRawData.isBlank()) {
            return taskManager;
        }

        // Обрабатываем текстовый файл
        boolean isParse = false;
        int loadedLastId = 0;
        for (String processString : loadedRawData.split("\n")) {
            if (processString.equals(csvStringHeader)) {
                isParse = true;

            } else if (isParse && !processString.isBlank()) {
                Optional<Task> genericTask = taskManager.fromString(processString);

                if (genericTask.isPresent()) {
                    Task task = genericTask.get();
                    if (task instanceof Epic epic) {
                        loadedLastId = Integer.max(epic.getId(), loadedLastId);
                        taskManager.addEpic(epic);
                    } else if (task instanceof Subtask sub) {
                        loadedLastId = Integer.max(sub.getId(), loadedLastId);
                        taskManager.addSubtask(sub);
                    } else {
                        loadedLastId = Integer.max(task.getId(), loadedLastId);
                        taskManager.addTask(task);
                    }
                }
            }
        }

        if (isParse) {
            taskManager.setGlobalTaskId(loadedLastId);

            for (Subtask subTask : taskManager.getAllSubtask()) {
                if (subTask.getEpicTask() == 0) {
                    continue;
                }

                Integer epicId = subTask.getEpicTask();
                Integer subTaskId = subTask.getId();
                taskManager.getEpicById(epicId).addSubtask(subTaskId);
            }

            for (Epic epic : taskManager.getAllEpic()) {
                taskManager.updateEpic(epic);
            }
        }
        return taskManager;
    }


    public void save() { //сохранение задачи в файл

        StringBuilder stringToFile = new StringBuilder("id,type,name,status,description,epic\n");

        for (Task task : getAllTask()) {
            stringToFile.append(toString(task)).append("\n");
        }

        for (Task task : getAllEpic()) {
            stringToFile.append(toString(task)).append("\n");
        }

        for (Task task : getAllSubtask()) {
            stringToFile.append(toString(task)).append("\n");
        }

        try {
            Files.writeString(file.toPath(), stringToFile);
        } catch (IOException e) {
            throw new ManagerSaveException("Cannot save taskFile. Error: " + e.getMessage());
        }
    }


    public String toString(Task task) { // перевод задачи в строку

        //id,type,name,status,description,epic
        Integer id = task.getId();
        String type = task.getType().toString();
        String name = task.getTitle();
        String status = task.getStatus().toString();
        String description = task.getDescription();
        String epic = task instanceof Subtask sub ? sub.getEpicTask().toString() : "";

        return String.format("%d,%s,%s,%s,%s,%s", id, type, name, status, description, epic);
    }

    private Optional<Task> fromString(String processString) { // перевод строки в задачу

        List<String> parsedString = List.of(processString.split(",", -1));

        Integer taskId = 0;
        TypeTask taskType = TypeTask.TASK;
        Status taskStatus = Status.NEW;
        Integer epicId = 0;
        String taskDescription = "";
        String taskName = "";

        try {
            taskId = Integer.parseInt(parsedString.get(0));
            taskType = TypeTask.valueOf(parsedString.get(1));
            taskStatus = Status.valueOf(parsedString.get(3));
            epicId = parsedString.get(5).isBlank() ? 0 : Integer.parseInt(parsedString.get(5));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }

        if (!parsedString.get(2).isEmpty()) {
            taskName = parsedString.get(2);
        } else {
            return Optional.empty();
        }
        taskDescription = parsedString.get(4);

        switch (taskType) {
            case TASK -> {
                return Optional.of(new Task(taskName, taskDescription, taskStatus, taskId));
            }
            case SUBTASK -> {
                return Optional.of(new Subtask(taskName, taskDescription, taskStatus, taskId, epicId));
            }
            case EPIC -> {
                return Optional.of(new Epic(taskName, taskDescription, taskStatus, taskId, new ArrayList<>()));
            }
            default -> {
                return Optional.empty();
            }
        }
    }

    /*
    id,type,name,status,description,epic
    0,TASK,Task 1,NEW,Description task 1,
    1,TASK,Task 1,NEW,Description task 1,
    2,EPIC,Epic 2,NEW,Description task 2,
    3,SUBTASK,Sub 2,NEW,Description subtask 3,2
     */

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void addEpic(Epic epic) { // создание нового эпика
        super.addEpic(epic);
        save();
    }

    @Override
    public void addTask(Task task) { // создание новой задачи
        super.addTask(task);
        save();
    }

    @Override
    public void updateTask(Task taskUpdate) { // Обновление задачи
        super.updateTask(taskUpdate);
        save();
    }

    @Override
    public void updateEpic(Epic epicUpdate) {   // обновлние эпика
        super.updateEpic(epicUpdate);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtaskUpdate) { // обновление подзадачи
        super.updateSubtask(subtaskUpdate);
        save();
    }

    @Override
    public void deleteTaskById(Integer id) { //удаление задачи по ID
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(Integer id) { //удаление подзадачи по ID
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(Integer id) { // удаление эпика по ID
        super.deleteEpicById(id);
        save();
    }
}
