package com.budzio.planmi.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskManager {
    private static TaskManager instance;
    private final List<Task> tasks;

    private TaskManager() {
        tasks = new ArrayList<>();
    }

    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }

        return instance;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }

    public void updateTask(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == task.getId()) {
                tasks.set(i, task);
                break;
            }
        }
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public List<Task> getTasksForDate(LocalDate date) {
        return tasks.stream()
                .filter(task -> task.isOnDate(date))
                .collect(Collectors.toList());
    }

    public List<Task> getScheduledTasksForDate(LocalDate date) {
        return tasks.stream()
                .filter(task -> task.isOnDate(date) && task.isScheduled())
                .collect(Collectors.toList());
    }

    public List<Task> getCloseTasks(int count) {
        LocalDate today = LocalDate.now();

        return tasks.stream()
                .filter(task -> !task.getStartDate().isBefore(today))
                .sorted((lr, tr) -> lr.getStartDate().compareTo(tr.getStartDate()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Task> getTasksForWeek(LocalDate startOfWeek) {
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        return tasks.stream()
                .filter(task -> {
                    LocalDate taskStart = task.getStartDate();
                    LocalDate taskEnd = task.getEndDate() != null ? task.getEndDate() : taskStart;

                    return !(taskEnd.isBefore(startOfWeek) || taskStart.isAfter(endOfWeek));
                })
                .collect(Collectors.toList());
    }
}
