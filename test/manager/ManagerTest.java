package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ManagerTest {
    @Test
    void managerInitialization_returnsValidInstance() {
        TaskManager manager = Manager.getDefault();
        assertNotNull(manager);
        assertTrue(manager.getAllTask().isEmpty());
    }

    @Test
    void historyManagerInitialization_producesValidInstance() {
        HistoryManager manager = Manager.getDefaultHistory();
        assertNotNull(manager);
        assertTrue(manager.getHistory().isEmpty());
    }
}
