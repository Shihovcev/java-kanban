package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.NoSuchElementException;
import manager.ManagerSaveException;
import manager.TaskManager;
import manager.TimeOverlapException;
import tasks.Task;

public class TaskHandler extends CrudHandler {

    public TaskHandler(TaskManager manager, Gson gson) {
        super(manager, gson, "tasks");
    }

    @Override
    protected void getById(HttpExchange exchange, int id) {
        try {
            Task task = manager.getTaskById(id);
            String response = gson.toJson(task);
            sendText(exchange, response);
        } catch (NoSuchElementException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (IOException | ManagerSaveException e) {
            sendServerError(exchange);
        }
    }

    @Override
    protected void getAll(HttpExchange exchange) {
        try {
            String response = gson.toJson(manager.getAllTask());
            sendText(exchange, response);
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    @Override
    protected void getAllLinked(HttpExchange exchange, int id, String linkedType) {
        try {
            sendBadRequest(exchange);
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    @Override
    protected void create(HttpExchange exchange) {
        try {
            String body = readRequestBody(exchange);
            Task task = gson.fromJson(body, Task.class);
            manager.addTask(task);
            String response = "Таска с ID:" + task.getId() + " успешно создана.";
            sendModified(exchange, response);
        } catch (IOException | ManagerSaveException e) {
            sendServerError(exchange);
        } catch (IllegalArgumentException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (TimeOverlapException e) {
            sendHasTimeOverlapping(exchange, e.getMessage());
        }
    }

    @Override
    protected void update(HttpExchange exchange, int id) {
        try {
            String body = readRequestBody(exchange);
            Task task = gson.fromJson(body, Task.class);
            manager.updateTask(task);
            String response = "Таска с ID:" + id + " успешно обновлена.";
            sendModified(exchange, response);
        } catch (IOException | ManagerSaveException e) {
            sendServerError(exchange);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (TimeOverlapException e) {
            sendHasTimeOverlapping(exchange, e.getMessage());
        }
    }

    @Override
    protected void delete(HttpExchange exchange, int id) {
        try {
            manager.deleteTaskById(id);
            String response = "Таска с ID: " + id + " удалена.";
            sendText(exchange, response);
        } catch (IOException | ManagerSaveException e) {
            sendServerError(exchange);
        } catch (NoSuchElementException | IllegalArgumentException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }
}
