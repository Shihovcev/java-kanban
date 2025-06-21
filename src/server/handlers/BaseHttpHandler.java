package server.handlers;

import static server.Endpoints.DELETE;
import static server.Endpoints.GET;
import static server.Endpoints.POST;
import static server.Endpoints.UNKNOWN;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import server.Endpoints;
import manager.TaskManager;

public class BaseHttpHandler implements HttpHandler {

    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected final TaskManager manager;
    protected final Gson gson;
    protected static final Logger logger = Logger.getLogger(BaseHttpHandler.class.getName());

    static {
        configureLogger();
    }

    public BaseHttpHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) {
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        try (OutputStream os = h.getResponseBody()) {
            h.sendResponseHeaders(200, response.length);
            os.write(response);
        }
    }

    protected void sendBadRequest(HttpExchange h) throws IOException {
        byte[] response = "Bad Request".getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        try (OutputStream os = h.getResponseBody()) {
            h.sendResponseHeaders(400, response.length);
            os.write(response);
        }
    }

    protected void sendModified(HttpExchange h, String text) throws IOException {
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(201, response.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendNotFound(HttpExchange h, String text) {
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        try (OutputStream os = h.getResponseBody()) {
            h.sendResponseHeaders(404, response.length);
            os.write(response);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to send response: 404 Not Found", e);
        }
    }

    protected void sendHasTimeOverlapping(HttpExchange h, String text) {
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        try (OutputStream os = h.getResponseBody()) {
            h.sendResponseHeaders(400, response.length);
            os.write(response);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to send response: 400 Bad Request", e);
        }
    }

    protected void sendConflict(HttpExchange h, String message) {
        byte[] response = message.getBytes(DEFAULT_CHARSET);
        try (OutputStream os = h.getResponseBody()) {
            h.sendResponseHeaders(409, response.length);
            os.write(response);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to send response: 409 Conflict", e);
        }
    }

    protected void sendServerError(HttpExchange h) {
        byte[] response = "Server Error".getBytes(DEFAULT_CHARSET);
        try (OutputStream os = h.getResponseBody()) {
            h.sendResponseHeaders(500, response.length);
            os.write(response);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to send response: 500 Server Error", e);
        }
    }

    protected Endpoints getEndpointMethod(String requestMethod) {
        return switch (requestMethod) {
            case "GET" -> GET;
            case "POST" -> POST;
            case "DELETE" -> DELETE;
            default -> UNKNOWN;
        };
    }

    public void sendBadRequestDirectly(HttpExchange exchange) {
        try {
            sendBadRequest(exchange);
        } catch (IOException e) {
            sendServerError(exchange);
        }
    }

    private static void configureLogger() {
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        handler.setFormatter(new SimpleFormatter());
        logger.addHandler(handler);
    }
}
