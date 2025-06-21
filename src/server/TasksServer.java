package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import server.adapters.GsonFactory;
import server.handlers.EpicTaskHandler;
import server.handlers.HistoryHandler;
import server.handlers.PrioritizedHandler;
import server.handlers.SubtaskHandler;
import server.handlers.TaskHandler;
import manager.TaskManager;

public class TasksServer {

    private static final int PORT = 8080;
    private static HttpServer httpServer;
    private static final Gson gson = getGson();
    static TaskManager manager;
    private static final Logger logger = Logger.getLogger(TasksServer.class.getName());

    static {
        configureLogger();
    }

    public TasksServer(TaskManager manager) {
        TasksServer.manager = manager;
        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TaskHandler(manager, gson));
            httpServer.createContext("/subtasks", new SubtaskHandler(manager, gson));
            httpServer.createContext("/epics", new EpicTaskHandler(manager, gson));
            httpServer.createContext("/history", new HistoryHandler(manager, gson));
            httpServer.createContext("/prioritized", new PrioritizedHandler(manager, gson));
            logger.info("HTTP сервер успешно инициализирован на порту " + PORT);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка инициализации HTTP сервера на порту " + PORT, e);
        }
    }

    public static void start() {
        if (httpServer != null) {
            httpServer.start();
            logger.info("HTTP сервер запущен. Прослушиваю порт " + PORT);
        } else {
            logger.warning("Попытка запуска HTTP сервера не увенчалась успехом.");
        }
    }

    public static void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
            logger.info("HTTP сервер остановлен. Порт " + PORT + " освобожден.");
        } else {
            logger.warning("Попытка остановки HTTP сервера не увенчалась успехом.");
        }
    }

    public static Gson getGson() {
        return GsonFactory.createGson();
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
