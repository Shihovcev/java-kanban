package tasks;

import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class EpicTest {
    private Epic epicA;
    private Epic epicB;
    private Subtask subA;
    private Subtask subB;

    @BeforeEach
    void beforeEachTest() {
        epicA = new Epic("Epic title A", "Epic description A");
        epicB = new Epic("Epic title B", "Epic description B");
        subA = new Subtask("Sub title A", "Sub description A");
        subB = new Subtask("Sub title B", "Sub description B");
        epicA.setId(1);
        epicB.setId(2);
        subA.setId(3);
        subB.setId(4);
        epicA.addSubtask(subA.getId());
        epicA.addSubtask(subB.getId());
        epicB.addSubtask(subA.getId());
        epicB.addSubtask(subB.getId());
        subA.setStatus(Status.NEW);
        subB.setStatus(Status.NEW);
    }

    @Test
    void givenEpicIsSubtaskToItself_thenExceptionIsThrown() {
        assertThrows(IllegalArgumentException.class, () -> epicA.addSubtask(epicA.getId()));
    }


    @Test
    void givenEpicTask_whenSubTaskAdded_thenEpicSubTasksExist() {
        assertEquals(2, epicA.getSubtaskList().size());
    }

    @Test
    void epicAndSubTask_sharedIdValidation() {
        ArrayList<Integer> subTasksList = new ArrayList<>(epicA.getSubtaskList());
        assertEquals(subA.getId(), subTasksList.get(0));
        assertEquals(subB.getId(), subTasksList.get(1));
    }

    @Test
    void epicsWithSameId_mustBeEqual() {
        epicA.setId(1);
        epicB.setId(1);
        assertEquals(epicA, epicB);
    }
}
