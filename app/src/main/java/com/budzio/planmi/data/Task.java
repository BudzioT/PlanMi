package com.budzio.planmi.data;

import android.annotation.SuppressLint;

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
    private int id;


    public Task(String title, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isCompleted = false;
        this.id = (int)System.currentTimeMillis(); // Grab that ID by miliseconds cause why not
        this.isScheduled = (startTime != null && endTime != null);
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

    public int getId() {
        return id;
    }
    public void setId(int id) {
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
