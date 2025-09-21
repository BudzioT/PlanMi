package com.budzio.planmi.data;

import android.annotation.SuppressLint;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalTime;

public class Task {
    private String title;

    private LocalDate startDate;
    private LocalDate endDate;

    private LocalTime startTime;
    private LocalTime endTime;

    private boolean isScheduled;

    private boolean isCompleted;
    private String id;


    public Task(String title, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isCompleted = false;
        this.id = java.util.UUID.randomUUID().toString();
        this.isScheduled = (startTime != null && endTime != null);
    }

    // JSON deserialization shit
    public Task(String id, String title, LocalDate startDate, LocalDate endDate, LocalTime startTime,
                LocalTime endTime, boolean isCompleted, boolean isScheduled) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isCompleted = isCompleted;
        this.isScheduled = isScheduled;
    }

    // Turn da file into JSON
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("title", title);
        json.put("startDate", startDate != null ? startDate.toString() : null);
        json.put("endDate", endDate != null ? endDate.toString() : null);
        json.put("startTime", startTime != null ? startTime.toString() : null);
        json.put("endTime", endTime != null ? endTime.toString() : null);
        json.put("isCompleted", isCompleted);
        json.put("isScheduled", isScheduled);

        return json;
    }

    // Parse da JSON
    public static Task fromJSON(JSONObject json) throws JSONException {
        String id = json.getString("id");
        String title = json.getString("title");

        LocalDate startDate = json.isNull("startDate") ? null : LocalDate.parse(json.getString("startDate"));
        LocalDate endDate = json.isNull("endDate") ? null : LocalDate.parse(json.getString("endDate"));
        LocalTime startTime = json.isNull("startTime") ? null : LocalTime.parse(json.getString("startTime"));
        LocalTime endTime = json.isNull("endTime") ? null : LocalTime.parse(json.getString("endTime"));

        boolean isCompleted = json.getBoolean("isCompleted");
        boolean isScheduled = json.getBoolean("isScheduled");

        return new Task(id, title, startDate, endDate, startTime, endTime, isCompleted, isScheduled);
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public boolean isScheduled() {
        return isScheduled;
    }
    public void setScheduled(boolean scheduled) {
        isScheduled = scheduled;
    }

    public boolean isToday() {
        return startDate.equals(LocalDate.now());
    }

    public boolean isOnDate(LocalDate date) {
        if (endDate == null) {
            return startDate.equals(date);
        }

        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    @SuppressLint("DefaultLocale")
    public String getTimeRange() {
        return String.format("%02d:%02d - %02d:%02d",
                startTime.getHour(), startTime.getMinute(),
                endTime.getHour(), endTime.getMinute());
    }
}
