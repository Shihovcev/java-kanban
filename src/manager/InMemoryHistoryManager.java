package manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import tasks.Task;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyMap;
    private Node head;
    private Node tail;

    public InMemoryHistoryManager() {
        historyMap = new HashMap<>();
        head = null;
        tail = null;
    }

    @Override
    public ArrayList<Task> getHistory() {
        ArrayList<Task> historyArray = new ArrayList<>();

        Node currentNode = head;
        while (currentNode != null) {
            historyArray.add(currentNode.getTask());
            currentNode = currentNode.getNext();
        }

        return historyArray;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task must not be null.");
        }

        Node newNode = new Node(null, new Task(task), null);

        if (historyMap.containsKey(task.getId())) {
            remove(task.getId());
        }

        if (historyMap.isEmpty()) {
            head = newNode;
        }

        if (tail != null) {
            tail.setNext(newNode);
        }

        newNode.setPrev(tail);
        tail = newNode;
        historyMap.put(task.getId(), newNode);
    }

    @Override
    public void remove(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null.");
        }

        if (!historyMap.containsKey(id)) {
            return;
        }

        Node nodeToRemove = historyMap.get(id);
        Node prev = nodeToRemove.getPrev();
        Node next = nodeToRemove.getNext();

        // Обновляем firstNode, если удаляем первый элемент
        if (nodeToRemove == head) {
            head = next;
        }

        // Обновляем lastNode, если удаляем последний элемент
        if (nodeToRemove == tail) {
            tail = prev;
        }

        // Связываем предыдущий и следующий узлы
        if (prev != null) {
            prev.setNext(next);
        }

        if (next != null) {
            next.setPrev(prev);
        }

        historyMap.remove(id);
    }
}
