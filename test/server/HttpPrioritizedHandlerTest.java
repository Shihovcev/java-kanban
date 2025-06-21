package server;

import static tasks.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import org.junit.jupiter.api.Test;

public class HttpPrioritizedHandlerTest extends HttpBaseTest {

    @Test
    public void shouldReturnPrioritizedTasksInCorrectOrder()
            throws IOException, InterruptedException {

        LocalDateTime start = LocalDateTime.of(2025, 2, 7, 10, 0);
        Duration duration = Duration.ofMinutes(30);

        Task taskA = new Task(0, "Task A", "Task A description", NEW,
                start, duration);
        Task taskB = new Task(0, "Task B", "Task B description", NEW,
                start.plusMinutes(30), duration);
        Task taskC = new Task(0, "Task C", "Task C description", NEW,
                start.plusMinutes(60), duration);
        Task taskD = new Task("Task D", "Task D description");
        taskManager.addTask(taskA);
        taskManager.addTask(taskB);
        taskManager.addTask(taskC);
        taskManager.addTask(taskD);

        Epic epicA = new Epic("Epic A", "Epic A description");
        Epic epicB = new Epic("Epic B", "Epic B description");
        taskManager.addEpic(epicA);
        taskManager.addEpic(epicB);

        Subtask subA = new Subtask(0, "Sub A", "Sub A description", NEW,
                epicA.getId(), start.plusMinutes(90), duration);
        Subtask subB = new Subtask(0, "Sub B", "Sub B description", NEW,
                epicA.getId(), start.plusMinutes(120), duration);
        Subtask subC = new Subtask(0, "Sub C", "Sub C description", NEW,
                epicB.getId(), start.plusMinutes(150), duration);
        taskManager.addSubtask(subA);
        taskManager.addSubtask(subB);
        taskManager.addSubtask(subC);

        List<Task> expected = taskManager.getPrioritizedTasks();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, response.statusCode());

        Task[] returned = gson.fromJson(response.body(), Task[].class);
        assertEquals(expected.size(), returned.length);
        assertEquals(expected.getFirst(), returned[0]);
    }

    @Test
    public void shouldReturnEmptyListWhenNoTasksExist() throws IOException, InterruptedException {

        taskManager.deleteAllTask();
        taskManager.deleteAllEpic();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, response.statusCode());

        Task[] returned = gson.fromJson(response.body(), Task[].class);
        assertEquals(0, returned.length);
    }

    @Test
    public void shouldReturn400OnInvalidPathOrMethod() throws IOException, InterruptedException {
        URI badPath = URI.create("http://localhost:8080/prioritized/invalid");
        HttpRequest requestA = HttpRequest.newBuilder()
                .uri(badPath)
                .GET()
                .build();
        HttpResponse<String> responseA = taskClient.send(
                requestA,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(400, responseA.statusCode());

        // Invalid method
        URI correctPath = URI.create("http://localhost:8080/prioritized");
        HttpRequest requestB = HttpRequest.newBuilder()
                .uri(correctPath)
                .DELETE()
                .build();
        HttpResponse<String> responseB = taskClient.send(
                requestB,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(400, responseB.statusCode());
    }
}
