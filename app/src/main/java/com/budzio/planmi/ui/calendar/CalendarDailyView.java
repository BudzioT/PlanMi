package com.budzio.planmi.ui.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.budzio.planmi.R;
import com.budzio.planmi.data.AddTaskDialog;
import com.budzio.planmi.data.Task;
import com.budzio.planmi.data.TaskAdapter;
import com.budzio.planmi.data.TaskManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CalendarDailyView extends AppCompatActivity {
    // Current date
    private LocalDate chosenDate;
    private TextView dayTextView;

    private FloatingActionButton addTask;
    private RecyclerView dailyTaskList;
    private TaskAdapter taskAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daily_view);

        if (getIntent().hasExtra("selectedDate")) {
            String dateString = getIntent().getStringExtra("selectedDate");
            chosenDate = LocalDate.parse(dateString);
        }
        else {
            chosenDate = LocalDate.now();
        }

        dayTextView = findViewById(R.id.dayTextView);
        updateDayView();

        RecyclerView taskList = findViewById(R.id.daily_tasklist);
        taskList.setLayoutManager(new LinearLayoutManager(this));

        addTask = findViewById(R.id.add_task);
        addTask.setOnClickListener(v -> showAddTaskDialog());

        dailyTaskList = findViewById(R.id.daily_tasklist);
        setupTaskRecyclerView();
        loadTasks();

        Button nextDayBtn = findViewById(R.id.next_day_btn);
        Button prevDayBtn = findViewById(R.id.prev_day_btn);
        nextDayBtn.setOnClickListener(this::goToNextDay);
        prevDayBtn.setOnClickListener(this::goToPrevDay);


        // Back button to start screen connection
        ImageButton menuBackBtn = findViewById(R.id.calendar_back_btn);
        menuBackBtn.setOnClickListener(this::goBackToMenu);

        setupCalendarTypeButtons();
    }

    private void setupCalendarTypeButtons() {
        TextView weeklyTypeBtn = findViewById(R.id.weekly_calendar_btn);
        TextView monthlyTypeBtn = findViewById(R.id.monthly_calendar_btn);

        if (weeklyTypeBtn != null) {
            weeklyTypeBtn.setOnClickListener(this::goToWeeklyView);
        }
        if (monthlyTypeBtn != null) {
            monthlyTypeBtn.setOnClickListener(this::goToMonthlyView);
        }
    }

    private void showAddTaskDialog() {
        AddTaskDialog dialog = new AddTaskDialog(this, task -> {
            loadTasks();
            Toast.makeText(this, "You've added a task yayy", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void setupTaskRecyclerView() {
        taskAdapter = new TaskAdapter(new ArrayList<>());
        dailyTaskList.setLayoutManager(new LinearLayoutManager(this));
        dailyTaskList.setAdapter(taskAdapter);
    }

    private void loadTasks() {
        List<Task> tasks = TaskManager.getInstance().getTasksForDate(chosenDate);
        taskAdapter.updateTasks(tasks);
    }

    private void goBackToMenu(View view) {
        Intent intent = new Intent(CalendarDailyView.this, CalendarActivity.class);
        startActivity(intent);

        overridePendingTransition(R.anim.slide_from_right, 0);
    }

    private void goToNextDay(View view) {
        chosenDate = chosenDate.plusDays(1);
        updateDayView();
    }

    private void goToPrevDay(View view) {
        chosenDate = chosenDate.plusDays(-1);
        updateDayView();
    }

    private void updateDayView() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d日 MM月 yyyy年");
        dayTextView.setText(chosenDate.format(formatter));
    }

    public void goToMonthlyView(View view) {
        Intent intent = new Intent(CalendarDailyView.this, CalendarActivity.class);
        intent.putExtra("selectedDate", chosenDate.toString());
        startActivity(intent);
    }

    public void goToWeeklyView(View view) {
        Intent intent = new Intent(CalendarDailyView.this, CalendarWeeklyActivity.class);
        intent.putExtra("selectedDate", chosenDate.toString());
        startActivity(intent);
    }
}
