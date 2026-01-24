package LLD.tech_impl.TaskSchedular;


import lombok.Getter;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

interface Executable extends Comparable<Executable>{
    void execute();
}

@Getter
class Task implements Executable{
    final int priority;
    final String name;
    Task(int priority, String name){
        if (priority < 1) {
            throw new IllegalArgumentException("Priority must be a positive integer (lower = higher priority)");
        }
        this.priority = priority;
        this.name = name;
    }

    @Override
    public int compareTo(Executable o) { // natural ordering, lowest first
        return Integer.compare(this.priority, ((Task)o).priority);
//        return 0;
    }

    @Override
    public void execute() {
        System.out.println("Executing task: " + name);
    }
}

// strategy pattern
interface SchedulingStrategy{
    Comparator<Executable> getComparator();
}

class FIFOSchedulingStrategy implements SchedulingStrategy{
    @Override
    public Comparator<Executable> getComparator() {
        return (o1, o2) -> o1.compareTo(o2);
    }
}

class PrioritySchedulingStrategy implements SchedulingStrategy{
    @Override
    public Comparator<Executable> getComparator() {
        return (o1, o2) -> Integer.compare(((Task)o1).getPriority(), ((Task)o2).getPriority());
    }
}

interface TaskScheduler{
    void scheduleTask(Executable task);
    void executeTasks();
    void cancelTask(Executable task);
}

class TaskSchedulerImpl implements TaskScheduler{
    private final SchedulingStrategy schedulingStrategy;
    private final Queue<Executable> queue;
    private final ScheduledExecutorService executorService;


    public TaskSchedulerImpl(SchedulingStrategy schedulingStrategy){
        this.schedulingStrategy = schedulingStrategy;
        this.queue = new PriorityQueue<>(this.schedulingStrategy.getComparator());
        this.executorService = Executors.newScheduledThreadPool(10);
        executorService.scheduleAtFixedRate(this::executeTasks, 0, 1, TimeUnit.SECONDS);
    }


    @Override
    public void scheduleTask(Executable task) {
        queue.offer(task);
    }

//    @Override
//    public void executeTasks() {
//        while (!queue.isEmpty()) {
//            queue.poll().execute();
//            ThreadUtils.sleepSeconds(1);
//        }
//    }

    @Override
    public void executeTasks() {
        while (!queue.isEmpty()) {
            Runnable taskRunner = () -> {
                Executable task = queue.poll();
                if (task != null) {
                    task.execute();
                }
            };
            executorService.submit(taskRunner);
        }
    }

    @Override
    public void cancelTask(Executable task) {
        queue.remove(task);
    }
}


class Runner{
    public static void main(String[] args) {
        TaskSchedulerImpl taskScheduler = new TaskSchedulerImpl(new PrioritySchedulingStrategy());
        taskScheduler.scheduleTask(new Task(4, "Task-4"));
        taskScheduler.scheduleTask(new Task(2, "Task-2"));
        taskScheduler.scheduleTask(new Task(1, "Task-1"));
        taskScheduler.scheduleTask(new Task(3, "Task-3"));
        taskScheduler.executeTasks();
    }
}
