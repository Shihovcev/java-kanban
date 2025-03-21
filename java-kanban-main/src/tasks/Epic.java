package tasks;

import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Subtask> subtasks;

    public Epic(String title, String description) {
        super(title, description);
        subtasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtaskList (){
        return subtasks;
    }

    public void addSubtask(Subtask subtask){
        subtasks.add(subtask);
        updateStatus();
    }

    public void removeSubtask(Subtask subtask){
        subtasks.remove(subtask);
        updateStatus();
    }

    public void updateStatus(){ //ЗАмена статуса у эпика
        int allInProgress = 0;
        int allDone = 0;

        for (Subtask subtask : subtasks) {
            if(subtask.getStatus() == Status.DONE){
                allDone++;
            }else if(subtask.getStatus() == Status.IN_PROGRESS){
                allInProgress++;
            }
        }
        for (Subtask subtask : subtasks) {
            if(allDone>0 && allDone == subtasks.size()){
                setStatus(Status.DONE);
            }else if(allDone>0 || allInProgress>0){
                setStatus(Status.IN_PROGRESS);
            }
        }

    }

}
