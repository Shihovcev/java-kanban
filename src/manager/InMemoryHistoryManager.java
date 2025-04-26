package manager;

import tasks.Task;


import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    ArrayList<Task> historyArray = new ArrayList<>();

    @Override
    public ArrayList<Task> getHistory(){ // Показывает историю
        return new ArrayList<>(historyArray);
    }

    @Override
    public void add(Task task) {
        if (task == null){
            return;
        }

        if (historyArray.size() >= 10) {
            historyArray.removeFirst();
        }
        historyArray.add(new Task(task));

    }
}
