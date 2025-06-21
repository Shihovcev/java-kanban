import server.TasksServer;
import manager.Manager;

public class MainHttpServer {
    public static void main(String[] args) {
        TasksServer httpTaskServer = new TasksServer(Manager.getDefault());
        TasksServer.start();
    }
}
