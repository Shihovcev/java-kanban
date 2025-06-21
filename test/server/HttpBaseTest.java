package server;

import com.google.gson.Gson;
import java.net.http.HttpClient;
import manager.Manager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class HttpBaseTest {

    protected TaskManager taskManager;
    protected Gson gson;
    protected TasksServer taskServer;
    protected HttpClient taskClient;

    @BeforeEach
    public void setUpBase() {
        taskManager = Manager.getDefault();
        gson = TasksServer.getGson();
        taskServer = new TasksServer(taskManager);
        taskClient = HttpClient.newHttpClient();
        taskManager.deleteAllTask();
        taskManager.deleteAllEpic();
        TasksServer.start();
    }

    @AfterEach
    public void tearDownBase() {
        TasksServer.stop();
        taskClient = null;
        taskServer = null;
        taskManager = null;
    }
}
