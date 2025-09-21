package com.budzio.planmi.data;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.budzio.planmi.R;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AddTaskDialog extends Dialog {
    private TextInputEditText titleInput;
    private Button startDateBtn, endDateBtn, startTimeBtn, endTimeBtn;
    private Button saveBtn, cancelBtn;
    private CheckBox isScheduledCheckbox;

    private LinearLayout dateContainer;

    private LocalDate startDate, endDate;
    private LocalTime startTime, endTime;

    private final OnTaskAddedListener listener;

    public interface OnTaskAddedListener {
        void onTaskAdded(Task task);
    }

    public AddTaskDialog(@NonNull Context context, OnTaskAddedListener listener) {
        super(context);
        this.listener = listener;

        this.startDate = LocalDate.now();
        this.endDate = LocalDate.now();

        this.startTime = LocalTime.of(9, 0);
        this.endTime = LocalTime.of(10, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_task);

        initViews();
        setupClicks();
        updateButtons();
    }

    private void initViews() {
        titleInput = findViewById(R.id.task_input_title);
        isScheduledCheckbox = findViewById(R.id.is_scheduled_checkbox);

        dateContainer = findViewById(R.id.date_time_container);

        startDateBtn = findViewById(R.id.start_date_btn);
        endDateBtn = findViewById(R.id.end_date_btn);

        startTimeBtn = findViewById(R.id.start_time_btn);
        endTimeBtn = findViewById(R.id.end_time_btn);

        saveBtn = findViewById(R.id.save_btn);
        cancelBtn = findViewById(R.id.cancel_btn);

        isScheduledCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dateContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
    }

    private void saveTasks() {
        String title = titleInput.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Enter a task title", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isScheduled = isScheduledCheckbox.isChecked();

        if (isScheduled && endDate.isBefore(startDate)) {
            Toast.makeText(getContext(), "End date can't be before Start date",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Task task;
        if (isScheduled) {
            task = new Task(title, startDate, endDate, startTime, endTime);
        }
        else {
            task = new Task(title, LocalDate.now(), null, null, null);
        }

        TaskManager.getInstance().addTask(task);

        if (listener != null) {
            listener.onTaskAdded(task);
        }

        dismiss();
    }

    private void showDatePicker(boolean isStartDate) {
        LocalDate currentDate = isStartDate ? startDate : endDate;

        DatePickerDialog picker = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);

                    if (isStartDate) {
                        startDate = selectedDate;

                        if (endDate.isBefore(startDate)) {
                            endDate = startDate;
                        }
                    }
                    else {
                        endDate = selectedDate;
                    }

                    updateButtons();
                },
                currentDate.getYear(),
                currentDate.getMonthValue() - 1,
                currentDate.getDayOfMonth()
        );

        picker.show();
    }

    private void showTimePicker(boolean isStartTime) {
        LocalTime currentTime = isStartTime ? startTime : endTime;

        TimePickerDialog picker = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minute) -> {
                    LocalTime selectedTime = LocalTime.of(hourOfDay, minute);

                    if (isStartTime) {
                        startTime = selectedTime;
                    }
                    else {
                        endTime = selectedTime;
                    }

                    updateButtons();
                },
                currentTime.getHour(),
                currentTime.getMinute(),
                true
        );

        picker.show();
    }

    private void setupClicks() {
        startDateBtn.setOnClickListener(v -> showDatePicker(true));
        endDateBtn.setOnClickListener(v -> showDatePicker(false));

        startTimeBtn.setOnClickListener(v -> showTimePicker(true));
        endTimeBtn.setOnClickListener(v -> showTimePicker(false));

        saveBtn.setOnClickListener(v -> saveTasks());
        cancelBtn.setOnClickListener(v -> dismiss());
    }

    private void updateButtons() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        startDateBtn.setText(startDate.format(dateFormatter));
        endDateBtn.setText(endDate.format(dateFormatter));

        startTimeBtn.setText(startTime.format(timeFormatter));
        endTimeBtn.setText(endTime.format(timeFormatter));
    }
}
