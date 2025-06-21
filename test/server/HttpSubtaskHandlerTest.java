package server;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static tasks.TaskStatus.DONE;
import static tasks.TaskStatus.IN_PROGRESS;
import static tasks.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import tasks.Epic;
import tasks.Subtask;
import org.junit.jupiter.api.Test;

public class HttpSubtaskHandlerTest extends HttpBaseTest {

    @Test
    public void shouldCreateSubtaskAndHandleConflicts() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic A", "Epic A description");
        taskManager.addEpic(epic);

        Subtask subtask = new Subtask(0, "Sub A", "Sub A description", NEW,
                epic.getId(), LocalDateTime.now(), Duration.ofMinutes(5));
        String subtaskJson = gson.toJson(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(201, response.statusCode());

        List<Subtask> subtasks = taskManager.getAllSubtask();
        assertNotNull(subtasks);
        assertEquals(1, subtasks.size());
        assertEquals("Sub A", subtasks.getFirst().getTitle());

        Subtask conflictSubtask = new Subtask(0, "Sub B", "Sub B description", DONE,
                epic.getId(), subtask.getStartTime(), Duration.ofMinutes(10));
        HttpRequest conflictRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(conflictSubtask)))
                .build();

        HttpResponse<String> conflictResponse = taskClient.send(
                conflictRequest,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(400, conflictResponse.statusCode());
    }

    @Test
    public void shouldReturn404ForInvalidEpicId() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(0, "Sub A", "Sub A description", NEW,
                999, LocalDateTime.now(), Duration.ofMinutes(5));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask)))
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldUpdateSubtaskCorrectly() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic A", "Epic A description");
        taskManager.addEpic(epic);

        Subtask sub = new Subtask(0, "Sub A", "Sub A description", NEW,
                epic.getId(), LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.addSubtask(sub);

        Subtask update = new Subtask(sub.getId(), "Sub A update", "Sub A description update",
                IN_PROGRESS, epic.getId(),
                sub.getStartTime().plusHours(1), Duration.ofMinutes(30));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + sub.getId()))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(update)))
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(201, response.statusCode());

        Subtask actual = taskManager.getSubtaskById(sub.getId());
        assertEquals("Sub A update", actual.getTitle());
        assertEquals(IN_PROGRESS, actual.getStatus());
    }

    @Test
    public void shouldDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic A", "Epic A description");
        taskManager.addEpic(epic);

        Subtask sub = new Subtask(0, "Sub A", "Sub A description", NEW,
                epic.getId(), LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.addSubtask(sub);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + sub.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, response.statusCode());
        assertThrows(NoSuchElementException.class, () ->
                taskManager.getSubtaskById(sub.getId()), "Попытка получить задачу которой нет в " +
                "менеджере вызывает исключение NoSuchElementException.");
    }

    @Test
    public void shouldReturnSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic A", "Epic A description");
        taskManager.addEpic(epic);

        Subtask sub = new Subtask(0, "Sub A", "Sub A description", NEW,
                epic.getId(), LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.addSubtask(sub);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + sub.getId()))
                .GET()
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, response.statusCode());

        Subtask fetched = gson.fromJson(response.body(), Subtask.class);
        assertEquals(sub.getTitle(), fetched.getTitle());
        assertEquals(sub.getId(), fetched.getId());
    }

    @Test
    public void shouldReturn400OnInvalidId() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks/invalid-id");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(400, response.statusCode());
        assertEquals("Bad Request", response.body());
    }

    @Test
    public void shouldReturn404ForNonexistentSubtask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks/999");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, response.statusCode());
    }
}
