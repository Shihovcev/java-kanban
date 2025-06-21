package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.NoSuchElementException;
import manager.ManagerSaveException;
import manager.TaskManager;
import manager.TimeOverlapException;
import tasks.Subtask;

public class SubtaskHandler extends CrudHandler {

    public SubtaskHandler(TaskManager manager, Gson gson) {
        super(manager, gson, "subtasks");
    }

    @Override
    protected void getById(HttpExchange exchange, int id) {
        try {
            Subtask subtask = manager.getSubtaskById(id);
            String response = gson.toJson(subtask);
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
            String response = gson.toJson(manager.getAllSubtask());
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
            Subtask sub = gson.fromJson(body, Subtask.class);
            manager.addSubtask(sub);
            String response = "Сабтаска с ID:" + sub.getId() + " успешно создана.";
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
    protected void update(HttpExchange exchange, int id) {
        try {
            String body = readRequestBody(exchange);
            Subtask sub = gson.fromJson(body, Subtask.class);
            if (sub.getId() != id) {
                throw new NoSuchElementException("Запрашиваемый ID ("
                        + id + ") не соответствует ID сабтаски (" + sub.getId() + ").");
            }
            manager.updateSubtask(sub);
            String response = "Сабтаска с ID:" + sub.getId() + " успешно обновлена.";
            sendModified(exchange, response);
        } catch (IOException | ManagerSaveException e) {
            sendServerError(exchange);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            sendNotFound(exchange, e.getMessage());
        } catch (TimeOverlapException e) {
            sendHasTimeOverlapping(exchange, e.getMessage());
        } catch (IllegalStateException e) {
            sendConflict(exchange, e.getMessage());
        }
    }

    @Override
    protected void delete(HttpExchange exchange, int id) {
        try {
            manager.deleteSubtaskById(id);
            String response = "Сабтаска с ID:" + id + " удалена.";
            sendText(exchange, response);
        } catch (IOException | ManagerSaveException e) {
            sendServerError(exchange);
        } catch (NoSuchElementException | IllegalArgumentException e) {
            sendNotFound(exchange, e.getMessage());
        }
    }
}
