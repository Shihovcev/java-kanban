package tasks;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest {
    private static Task TaskA;
    private static Task TaskB;

    @BeforeAll
    static void beforeAllTest() {
        TaskA = new Task("Задача 1", "Описание задачи 1");
        TaskB = new Task("Задача 2", "Описание задачи 2");
    }

    @Test
    void tasksWithSameId_shouldBeEqual() {
        TaskA.setId(1);
        TaskB.setId(1);
        assertEquals(TaskA, TaskB);
    }
}
