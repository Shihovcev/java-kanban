package server;

import static tasks.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.junit.jupiter.api.Test;

public class HttpEpicHandlerTest extends HttpBaseTest {

    private final Type subTaskListType = new TypeToken<List<Subtask>>() {}.getType();

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic A", "Epic A description");
        String epicJson = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> epics = taskManager.getAllEpic();
        assertEquals(1, epics.size());
        assertEquals("Epic A", epics.getFirst().getTitle());
        assertEquals(0, epics.getFirst().getSubtaskList().size());
    }

    @Test
    public void testAddNullEpic() throws IOException, InterruptedException {
        String nullEncoded = gson.toJson(null);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(nullEncoded))
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        LocalDateTime start = LocalDateTime.of(2025, 2, 7, 10, 0);
        Duration duration = Duration.ofMinutes(30);

        Epic epicA = new Epic("Epic A", "Epic A description");
        taskManager.addEpic(epicA);
        Subtask subA = new Subtask(0, "Sub A", "Sub A description", NEW,
                epicA.getId(), start, duration);
        taskManager.addSubtask(subA);

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicA.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = taskClient.send(
                deleteRequest,
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode());
        assertTrue(taskManager.getAllEpic().isEmpty());
        assertTrue(taskManager.getAllSubtask().isEmpty());

        HttpRequest notFoundRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/999"))
                .DELETE()
                .build();

        HttpResponse<String> response404 = taskClient.send(
                notFoundRequest,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, response404.statusCode());
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epicA = new Epic("Epic A", "Epic A description");
        taskManager.addEpic(epicA);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicA.getId()))
                .GET()
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode());
        Epic recievedEpic = gson.fromJson(response.body(), Epic.class);
        assertEquals(epicA, recievedEpic);

        HttpRequest request404 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/999"))
                .GET()
                .build();

        HttpResponse<String> response404 = taskClient.send(
                request404,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, response404.statusCode());
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        LocalDateTime start = LocalDateTime.of(2025, 2, 10, 10, 0);
        Duration duration = Duration.ofMinutes(30);
        Epic epicA = new Epic("Epic A", "Epic A description");
        taskManager.addEpic(epicA);

        Subtask subA = new Subtask(0, "Sub A", "Sub A description", NEW,
                epicA.getId(), start, duration);
        taskManager.addSubtask(subA);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicA.getId() + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        List<Subtask> subTasks = gson.fromJson(response.body(), subTaskListType);

        assertEquals(200, response.statusCode());
        assertEquals(taskManager.getEpicById(epicA.getId()).getSubtaskList().size(), subTasks.size());
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic epicA = new Epic("Epic A", "Epic A description");
        taskManager.addEpic(epicA);

        Epic updateEpicA = new Epic(epicA.getId(),
                "Epic A update", "Epic A description update");
        String updatedJson = gson.toJson(updateEpicA);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicA.getId()))
                .POST(HttpRequest.BodyPublishers.ofString(updatedJson))
                .build();

        HttpResponse<String> response = taskClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(201, response.statusCode());
        Epic fetchedEpic = taskManager.getEpicById(epicA.getId());
        assertEquals("Epic A update", fetchedEpic.getTitle());
    }

    @Test
    public void testInvalidRequests() throws IOException, InterruptedException {
        HttpRequest badIdRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/id"))
                .GET()
                .build();

        HttpResponse<String> badIdResponse = taskClient.send(
                badIdRequest,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(400, badIdResponse.statusCode());
    }

    @Test
    public void testInvalidEndpoint() throws IOException, InterruptedException {
        HttpRequest badIdRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/abracadabra/12/subtasks"))
                .GET()
                .build();

        HttpResponse<String> badIdResponse = taskClient.send(
                badIdRequest,
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(404, badIdResponse.statusCode());
    }

}
