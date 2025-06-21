package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import manager.ManagerSaveException;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;

public class EpicTaskHandler extends CrudHandler {

    public EpicTaskHandler(TaskManager manager, Gson gson) {
        super(manager, gson, "epics");
    }

    @Override
    protected void getById(HttpExchange exchange, int id) {
        try {
            Epic epic = manager.getEpicById(id);
            String response = gson.toJson(epic);
            sendText(exchange, response);
        } catch (IOException | ManagerSaveException e) {
            sendServerError(exchange);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    @Override
    protected void getAll(HttpExchange exchange) {
        try {
            String response = gson.toJson(manager.getAllEpic());
            sendText(exchange, response);
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    @Override
    protected void getAllLinked(HttpExchange exchange, int id, String linkedType) {
        if (linkedType.equals("subtasks")) {
            try {
                Epic epic = manager.getEpicById(id);
                List<Subtask> subtasks = new ArrayList<>();
                for (Integer subTaskId : epic.getSubtaskList()) {
                    Subtask subTask = manager.getSubtaskById(subTaskId);
                    subtasks.add(subTask);
                }
                String response = gson.toJson(subtasks);
                sendText(exchange, response);
            } catch (NoSuchElementException e) {
                sendNotFound(exchange, e.getMessage());
            } catch (IOException e) {
                sendServerError(exchange);
            }
        } else {
            try {
                sendBadRequest(exchange);
            } catch (IOException e) {
                sendServerError(exchange);
            }
        }
    }

    @Override
    protected void create(HttpExchange exchange) {
        try {
            String body = readRequestBody(exchange);
            Epic epic = gson.fromJson(body, Epic.class);
            manager.addEpic(epic);
            String response = "Эпик с ID: " + epic.getId() + " успешно создан.";
            sendModified(exchange, response);
        } catch (IOException | ManagerSaveException e) {
            sendServerError(exchange);
        } catch (IllegalArgumentException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }

    @Override
    protected void update(HttpExchange exchange, int id) {
        try {
            String body = readRequestBody(exchange);
            Epic epic = gson.fromJson(body, Epic.class);
            if (epic.getId() != id) {
                throw new NoSuchElementException("Запрашиваемый ID ("
                        + id + ") не соответствует ID эпика (" + epic.getId() + ").");
            }
            manager.updateEpic(epic);
            String response = "Эпик с ID:" + epic.getId() + " успешно обновлен.";
            sendModified(exchange, response);
        } catch (IOException | ManagerSaveException e) {
            sendServerError(exchange);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (IllegalStateException e) {
            sendConflict(exchange, e.getMessage());
        }
    }

    @Override
    protected void delete(HttpExchange exchange, int id) {
        try {
            manager.deleteEpicById(id);
            String response = "Эпик с ID:" + id + " удален.";
            sendText(exchange, response);
        } catch (IOException | ManagerSaveException e) {
            sendServerError(exchange);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }
}
