package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTaskManagerTest {
    private TaskManager manager;
    private Task taskA;
    private Task taskB;
    private Epic epicA;
    private Subtask subA;
    private Subtask subB;

    @BeforeEach
    void beforeEachTest() {
        manager = Manager.getDefault();
        taskA = new Task("Title task A", "Task description A");
        taskB = new Task("Title task B", "Task description B");
        epicA = new Epic("Title epic A", "Epic description A");
        subA = new Subtask("Title sub A", "Sub description A");
        subB = new Subtask("Title sub B", "Sub description B");
    }

    @Test
    void taskManagerStoresAndFindsAllTaskTypes() {
        manager.addTask(taskA);
        manager.addEpic(epicA);
        manager.addSubtask(subA);
        assertEquals(taskA, manager.getTaskById(taskA.getId()));
        assertEquals(epicA, manager.getEpicById(epicA.getId()));
        assertEquals(subA, manager.getSubtaskById(subA.getId()));
    }

    @Test
    void taskManagerGeneratesUniqueIds() {
        manager.addTask(taskA);
        manager.addTask(taskB);
        assertEquals(manager.getTaskById(taskB.getId()), manager.getTaskById(taskA.getId() + 1));
    }

    @Test
    void taskManagerPreservesAddedTaskState() {
        manager.addTask(taskA);
        assertEquals(taskA.getTitle(), manager.getTaskById(taskA.getId()).getTitle());
        assertEquals(taskA.getDescription(), manager.getTaskById(taskA.getId()).getDescription());
        assertEquals(taskA.getStatus(), manager.getTaskById(taskA.getId()).getStatus());
        assertEquals(taskA.getId(), manager.getTaskById(taskA.getId()).getId());
    }

    @Test
    void historyManagerCapturesTaskStateChanges() {
        manager.addTask(taskA);
        manager.getTaskById(taskA.getId()).setStatus(Status.IN_PROGRESS);
        manager.getTaskById(taskA.getId()).setStatus(Status.DONE);
        ArrayList<Task> history = manager.getHistory();
        assertEquals(Status.IN_PROGRESS, history.get(0).getStatus());
    }

    @Test
    void epicStatusUpdatesWhenSubtaskInProgress() {
        manager.addEpic(epicA);
        manager.addSubtask(subA);
        manager.addSubtask(subB);
        manager.getEpicById(epicA.getId()).addSubtask(subA.getId());
        manager.getEpicById(epicA.getId()).addSubtask(subB.getId());
        manager.getSubtaskById(subA.getId()).setEpicTask(epicA.getId());
        manager.getSubtaskById(subB.getId()).setEpicTask(epicA.getId());
        manager.updateEpic(manager.getEpicById(epicA.getId()));
        manager.getSubtaskById(subB.getId()).setStatus(Status.IN_PROGRESS);
        manager.updateEpic(manager.getEpicById(epicA.getId()));
        assertEquals(Status.IN_PROGRESS, manager.getEpicById(epicA.getId()).getStatus());
    }

    @Test
    void epicStatusDoneWhenAllSubtasksDone() {
        manager.addEpic(epicA);
        manager.addSubtask(subA);
        manager.addSubtask(subB);
        manager.getEpicById(epicA.getId()).addSubtask(subA.getId());
        manager.getEpicById(epicA.getId()).addSubtask(subB.getId());
        manager.getSubtaskById(subA.getId()).setEpicTask(epicA.getId());
        manager.getSubtaskById(subB.getId()).setEpicTask(epicA.getId());
        manager.updateEpic(manager.getEpicById(epicA.getId()));
        manager.getSubtaskById(subA.getId()).setStatus(Status.DONE);
        manager.getSubtaskById(subB.getId()).setStatus(Status.DONE);
        manager.updateEpic(manager.getEpicById(epicA.getId()));
        assertEquals(Status.DONE, manager.getEpicById(epicA.getId()).getStatus());
    }
}
