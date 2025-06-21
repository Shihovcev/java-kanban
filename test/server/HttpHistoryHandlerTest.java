package server;

import static tasks.TaskStatus.DONE;
import static tasks.TaskStatus.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
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

public class HttpHistoryHandlerTest extends HttpBaseTest {

    private final Type taskListType = new TypeToken<List<Task>>() {}.getType();

    @Test
    public void shouldReturnEmptyHistoryInitially() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, response.statusCode());

        List<Task> history = gson.fromJson(response.body(), taskListType);
        assertEquals(0, history.size());
    }

    @Test
    public void shouldReturnCorrectHistoryAfterAccessingTasks()
            throws IOException, InterruptedException {
        LocalDateTime start = LocalDateTime.of(2025, 2, 7, 10, 0);
        Duration duration = Duration.ofMinutes(30);

        Task taskA = new Task(0, "Task A", "Task A description", DONE,
                start, duration);
        Task taskB = new Task(0, "Task B", "Task B description", DONE,
                start.plusMinutes(35), duration);
        taskManager.addTask(taskA);
        taskManager.addTask(taskB);

        Epic epicA = new Epic("Epic A", "Epic A description");
        taskManager.addEpic(epicA);
        Subtask subA = new Subtask(0, "Sub A", "Sub A description", IN_PROGRESS, epicA.getId(),
                start.plusMinutes(65), duration);
        Subtask subB = new Subtask(0, "Sub B", "Sub B description", IN_PROGRESS, epicA.getId(),
                start.plusMinutes(95), duration);
        taskManager.addSubtask(subA);
        taskManager.addSubtask(subB);

        taskManager.getTaskById(taskA.getId());
        taskManager.getTaskById(taskB.getId());
        taskManager.getSubtaskById(subA.getId());
        taskManager.getSubtaskById(subB.getId());
        taskManager.getEpicById(epicA.getId());

        URI uri = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, response.statusCode());

        List<Task> history = gson.fromJson(response.body(), taskListType);
        assertEquals(5, history.size());
        assertEquals(taskA.getId(), history.getFirst().getId());
    }

    @Test
    public void shouldReturn400OnInvalidPathOrMethod() throws IOException, InterruptedException {
        URI invalidPath = URI.create("http://localhost:8080/history/extra");
        HttpRequest requestA = HttpRequest.newBuilder()
                .uri(invalidPath)
                .GET()
                .build();

        HttpResponse<String> responseA = taskClient.send(
                requestA,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(400, responseA.statusCode());

        URI deleteMethod = URI.create("http://localhost:8080/history");
        HttpRequest requestB = HttpRequest.newBuilder()
                .uri(deleteMethod)
                .DELETE()
                .build();
        HttpResponse<String> responseB = taskClient.send(
                requestB,
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(400, responseB.statusCode());
    }
}
