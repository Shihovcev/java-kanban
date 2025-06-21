package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import manager.TaskManager;

public class HistoryHandler extends CrudHandler {

    public HistoryHandler(TaskManager manager, Gson gson) {
        super(manager, gson, "history");
    }

    @Override
    protected void getAll(HttpExchange exchange) {
        try {
            String response = gson.toJson(manager.getHistory());
            sendText(exchange, response);
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    @Override
    protected void getById(HttpExchange exchange, int id) {
        sendBadRequestDirectly(exchange);
    }

    @Override
    protected void create(HttpExchange exchange) {
        sendBadRequestDirectly(exchange);
    }

    @Override
    protected void update(HttpExchange exchange, int id) {
        sendBadRequestDirectly(exchange);
    }

    @Override
    protected void delete(HttpExchange exchange, int id) {
        sendBadRequestDirectly(exchange);
    }

    @Override
    protected void getAllLinked(HttpExchange exchange, int id, String linkedType) {
        sendBadRequestDirectly(exchange);
    }
}
