package com.budzio.planmi.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.budzio.planmi.R;
import com.budzio.planmi.data.Task;
import com.budzio.planmi.data.TaskManager;
import com.budzio.planmi.ui.calendar.CalendarActivity;

import java.time.LocalDate;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LinearLayout taskContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        taskContainer = findViewById(R.id.main_task_container);
        loadCloseTasks();

        Button calendarBtn = findViewById(R.id.calendar_btn);
        calendarBtn.setOnClickListener(this::openCalendarActivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCloseTasks();
    }

    private void loadCloseTasks() {
        taskContainer.removeAllViews();

        List<Task> todayTasks = TaskManager.getInstance().getTasksForDate(LocalDate.now());
        int tasksToShow = Math.min(3, todayTasks.size());

        if (todayTasks.size() >= 3) {
            for (int i = 0; i < 3; i++) {
                addTaskView(todayTasks.get(i));
            }
        }
        else {
            for (Task task : todayTasks) {
                addTaskView(task);
            }
        }

        List<Task> closeTasks = TaskManager.getInstance().getCloseTasks(3 - todayTasks.size());
        for (Task task : closeTasks) {
            if (!todayTasks.contains(task)) {
                addTaskView(task);
            }
        }
    }

    private void addTaskView(Task task) {
        View taskView = getLayoutInflater().inflate(R.layout.task_main, taskContainer, false);

        CheckBox checkBox = taskView.findViewById(R.id.task_main_check);
        TextView titleView = taskView.findViewById(R.id.task_main_title);

        checkBox.setChecked(task.isCompleted());
        titleView.setText(task.getTitle());

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            TaskManager.getInstance().updateTask(task);
        });

        taskContainer.addView(taskView);
    }

    public void openCalendarActivity(View view) {
        Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
        startActivity(intent);

        overridePendingTransition(R.anim.slide_from_left, 0);
    }
}