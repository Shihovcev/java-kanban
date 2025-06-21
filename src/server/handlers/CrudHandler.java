package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import server.Endpoints;
import manager.TaskManager;

public abstract class CrudHandler extends BaseHttpHandler {

    String entityPath;

    public CrudHandler(TaskManager manager, Gson gson, String entityPath) {
        super(manager, gson);
        this.entityPath = entityPath;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            //private final Pattern pattern;
            String[] request = exchange.getRequestURI().getPath().split("/", -1);
            Endpoints method = getEndpointMethod(exchange.getRequestMethod());

            if (request.length == 0 ||
                    request.length > 4 ||
                    !request[1].equals(entityPath) ||
                    method == Endpoints.UNKNOWN) {
                sendBadRequest(exchange);
                return;
            }

            int id = 0;
            String idString = request.length >= 3 ? request[2] : "";
            String allLinksString = request.length == 4 ? request[3] : "";

            if (!idString.isBlank()) {
                try {
                    id = Integer.parseInt(idString);
                } catch (NumberFormatException e) {
                    sendBadRequest(exchange);
                    return;
                }
            }

            switch (method) {
                case GET -> {
                    if (!idString.isBlank() && !allLinksString.isBlank()) {
                        getAllLinked(exchange, id, allLinksString);
                    } else if (!idString.isBlank()) {
                        getById(exchange, id);
                    } else {
                        getAll(exchange);
                    }
                }
                case POST -> {
                    if (!idString.isBlank()) {
                        update(exchange, id);
                    } else {
                        create(exchange);
                    }
                }
                case DELETE -> {
                    if (!idString.isBlank()) {
                        delete(exchange, id);
                    } else {
                        sendBadRequest(exchange);
                    }
                }
                default -> sendBadRequest(exchange);
            }

        } catch (Exception e) {
            sendServerError(exchange);
        }
    }

    protected String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        return new String(is.readAllBytes(), DEFAULT_CHARSET);
    }

    protected abstract void getById(HttpExchange exchange, int id);

    protected abstract void getAll(HttpExchange exchange);

    protected abstract void getAllLinked(HttpExchange exchange, int id, String linkedType);

    protected abstract void create(HttpExchange exchange);

    protected abstract void update(HttpExchange exchange, int id);

    protected abstract void delete(HttpExchange exchange, int id);
}
