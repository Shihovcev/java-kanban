package manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task taskA;
    private Task taskB;
    private Task taskC;
    private Task taskD;

    @BeforeEach
    void beforeEachTest() {
        historyManager = Manager.getDefaultHistory();
        taskA = new Task("Task A", "Description A");
        taskB = new Task("Task B", "Description B");
        taskC = new Task("Task C", "Description C");
        taskD = new Task("Task D", "Description D");
        taskA.setId(1);
        taskB.setId(2);
        taskC.setId(3);
        taskD.setId(4);
    }

    @Test
    void givenNewHistoryManager_whenInitialized_thenHistoryIsEmpty() {
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void givenTask_whenModifiedAfterAddingToHistory_thenStoredTaskIsUnaffected() {
        historyManager.add(taskA);
        taskA.setDescription("Description B modified");
        assertNotEquals(historyManager.getHistory().getFirst().getDescription(),
                taskA.getDescription());
    }

    @Test
    void givenTask_whenAddedToHistory_thenHistoryNotEmpty() {
        historyManager.add(taskA);
        historyManager.add(taskB);
        historyManager.add(taskC);
        assertFalse(historyManager.getHistory().isEmpty());
        assertEquals(3, historyManager.getHistory().size());
    }

    @Test
    void givenThreeTasks_whenAddingAgainFirstTask_thenItBecomesLastInHistory() {
        historyManager.add(taskA);
        historyManager.add(taskB);
        historyManager.add(taskC);
        historyManager.add(taskA);
        List<Task> expectedOrder = Arrays.asList(taskB, taskC, taskA);
        assertEquals(expectedOrder, historyManager.getHistory());
    }
}



