package tasks;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;


public class SubtaskTest {
    private static Subtask subA;
    private static Subtask subB;
    private static Epic epicA;

    @BeforeAll
    static void beforeAllTests() {
        subA = new Subtask("Подзадача 1", "Описание подзадачи 1");
        subB = new Subtask("Подзадача 2", "Описание подзадачи 2");
        epicA = new Epic("Эпик 1", "Описание эпика 1");
    }

    @Test
    void subtaskCannotBeItsOwnEpic() {
        assertThrows(IllegalArgumentException.class, () -> subA.setEpicTask(subA.getId()));
    }

    @Test
    void epicParentAssignmentToSubtask_preservesIdAndReference() {
        subA.setEpicTask(epicA.getId());
        assertNotNull(subA.getEpicTask());
        assertEquals(subA.getEpicTask(), epicA.getId());
    }

    @Test
    void subtasksWithSameId_mustBeEqual() {
        subA.setId(1);
        subB.setId(1);
        assertEquals(subA, subB);
    }
}
