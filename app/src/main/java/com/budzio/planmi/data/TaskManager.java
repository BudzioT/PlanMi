package com.budzio.planmi.data;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskManager {
    private static TaskManager instance;
    private final List<Task> tasks;
    private Context context;

    private static final String PREF_NAME = "PlanMiPrefs";
    private static final String TASKS_KEY = "tasks";
    private static final String NOTES_KEY = "notes";


    private Map<LocalDate, Note> notesMap = new HashMap<>();

    private TaskManager() {

        tasks = new ArrayList<>();
    }

    public void initialize(Context context) {
        this.context = context.getApplicationContext();
        loadTasks();
        loadNotes();
    }

    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }

        return instance;
    }

    public Note getNoteForDate(LocalDate date) {
        if (!notesMap.containsKey(date)) notesMap.put(date, new Note());
        return notesMap.get(date);
    }

    public void saveNoteForDate(LocalDate date, Note note) {
        notesMap.put(date, note);
        saveNotes();
    }

    private void saveNotes() {
        if (context == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        JSONObject notesJson = new JSONObject();
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;

        for (Map.Entry<LocalDate, Note> entry : notesMap.entrySet()) {
            try {
                notesJson.put(entry.getKey().format(fmt), entry.getValue().toJSON());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        editor.putString(NOTES_KEY, notesJson.toString());
        editor.apply();
    }

    private void loadNotes() {
        if (context == null) return;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String notesStr = prefs.getString(NOTES_KEY, "{}");
        notesMap.clear();

        try {
            JSONObject notesJson = new JSONObject(notesStr);

            Iterator<String> keys = notesJson.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject noteJson = notesJson.getJSONObject(key);
                Note note = Note.fromJSON(noteJson);
                notesMap.put(LocalDate.parse(key), note);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void addTask(Task task) {
        tasks.add(task);
        saveTasks();
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        saveTasks();
    }

    public void saveTasks() {
        if (context == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray jsonArray = new JSONArray();
        for (Task task : tasks) {
            try {
                jsonArray.put(task.toJSON());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        editor.putString(TASKS_KEY, jsonArray.toString());
        editor.apply();
    }

    private void loadTasks() {
        if (context == null) return;

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String tasksJson = prefs.getString(TASKS_KEY, "[]");

        tasks.clear();
        try {
            JSONArray jsonArray = new JSONArray(tasksJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject taskJson = jsonArray.getJSONObject(i);
                Task task = Task.fromJSON(taskJson);
                tasks.add(task);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateTask(Task task) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == task.getId()) {
                tasks.set(i, task);
                break;
            }
        }

        saveTasks();
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
