package com.budzio.planmi.ui.calendar;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.budzio.planmi.R;
import com.budzio.planmi.data.AddTaskDialog;
import com.budzio.planmi.data.Task;
import com.budzio.planmi.data.TaskManager;
import com.budzio.planmi.ui.main.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CalendarWeeklyActivity extends AppCompatActivity {
    // Current date
    private LocalDate chosenDate;
    private TextView dateText;

    private GridLayout weeklyGrid;

    private FloatingActionButton addTaskBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weekly_calendar);

        // All date related shenanigans
        chosenDate = LocalDate.now();
        dateText = findViewById(R.id.dateTextView);

        if (getIntent().hasExtra("selectedDate")) {
            String dateString = getIntent().getStringExtra("selectedDate");
            chosenDate = LocalDate.parse(dateString);
        } else {
            chosenDate = LocalDate.now();
        }

        initViews();
        setupClickListeners();
        updateWeeklyView();
        loadWeeklyTasks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWeeklyTasks();
    }

    private void initViews() {
        dateText = findViewById(R.id.dateTextView);
        addTaskBtn = findViewById(R.id.add_task);
        weeklyGrid = findViewById(R.id.weekly_grid);
    }

    private void showAddTaskDialog() {
        AddTaskDialog dialog = new AddTaskDialog(this, task -> {
            updateWeekView();
            loadWeeklyTasks();
            Toast.makeText(this, "Task added successfully!", Toast.LENGTH_SHORT).show();
        });
        dialog.show();
    }

    private void updateWeekView() {
        // Update the week date display
        LocalDate startOfWeek = chosenDate.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("W週 MM月 yyyy年");

        int weekOfYear = startOfWeek.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        String weekText = weekOfYear + "週 " + startOfWeek.format(DateTimeFormatter.ofPattern("MM月 yyyy年"));
        dateText.setText(weekText);

        //updateDayNumbers();
        // TODO: Load and display tasks for the week
    }

    private void updateDayNumbers(LocalDate weekStart) {
        TextView mondayNum = findViewById(R.id.monday_number);
        TextView tuesdayNum = findViewById(R.id.tuesday_number);
        TextView wednesdayNum = findViewById(R.id.wednesday_number);
        TextView thursdayNum = findViewById(R.id.thursday_number);
        TextView fridayNum = findViewById(R.id.friday_number);
        TextView saturdayNum = findViewById(R.id.saturday_number);
        TextView sundayNum = findViewById(R.id.sunday_number);

        mondayNum.setText(String.valueOf(weekStart.getDayOfMonth()));
        tuesdayNum.setText(String.valueOf(weekStart.plusDays(1).getDayOfMonth()));
        wednesdayNum.setText(String.valueOf(weekStart.plusDays(2).getDayOfMonth()));
        thursdayNum.setText(String.valueOf(weekStart.plusDays(3).getDayOfMonth()));
        fridayNum.setText(String.valueOf(weekStart.plusDays(4).getDayOfMonth()));
        saturdayNum.setText(String.valueOf(weekStart.plusDays(5).getDayOfMonth()));
        sundayNum.setText(String.valueOf(weekStart.plusDays(6).getDayOfMonth()));
    }

    /*private void loadTasksOnGrid() {
        GridLayout gridLayout = findViewById(R.id.weekly_grid);
        if (gridLayout == null) return;

        for (int i = gridLayout.getChildCount() - 1; i >= 0; i--) {
            View child = gridLayout.getChildAt(i);
            if (child.getTag() != null && child.getTag().equals("task")) {
                gridLayout.removeView(child);
            }
        }

        LocalDate startOfWeek = getStartOfWeek(chosenDate);
        List<Task> weekTasks = TaskManager.getInstance().getTasksForWeek(startOfWeek);

        for (Task task : weekTasks) {
            if (task.isScheduled()) {
                addTaskToGrid(task, startOfWeek, gridLayout);
            }
        }
    }*/

    private void setupClickListeners() {
        Button nextWeekBtn = findViewById(R.id.next_week_btn);
        Button prevWeekBtn = findViewById(R.id.prev_week_btn);
        nextWeekBtn.setOnClickListener(this::nextWeek);
        prevWeekBtn.setOnClickListener(this::previousWeek);

        ImageButton backBtn = findViewById(R.id.calendar_back_btn);
        backBtn.setOnClickListener(this::goBackToMenu);

        setupCalendarTypeButtons();

        addTaskBtn.setOnClickListener(v -> showAddTaskDialog());
    }

    private void setupCalendarTypeButtons() {
        TextView dailyTypeBtn = findViewById(R.id.daily_calendar_btn);
        TextView monthlyTypeBtn = findViewById(R.id.monthly_calendar_btn);

        if (dailyTypeBtn != null) {
            dailyTypeBtn.setOnClickListener(this::goToDailyView);
        }
        if (monthlyTypeBtn != null) {
            monthlyTypeBtn.setOnClickListener(this::goToMonthlyView);
        }
    }

    private void loadWeeklyTasks() {
        clearTaskViews();

        LocalDate weekStart = chosenDate.minusDays(chosenDate.getDayOfWeek().getValue() - 1);

        for (int dayOffset = 0; dayOffset < 7; dayOffset++) {
            LocalDate currentDay = weekStart.plusDays(dayOffset);
            List<Task> dayTasks = TaskManager.getInstance().getTasksForDate(currentDay);

            for (Task task : dayTasks) {
                if (task.getStartTime() != null) {
                    addTaskToGrid(task, dayOffset + 1);
                }
            }
        }
    }

    private void clearTaskViews() {
        GridLayout grid = findViewById(R.id.weekly_grid);

        List<View> viewsToRemove = new ArrayList<>();

        for (int i = 0; i < grid.getChildCount(); i++) {
            View child = grid.getChildAt(i);
            GridLayout.LayoutParams params = (GridLayout.LayoutParams) child.getLayoutParams();


            if (child.getTag() != null && child.getTag().equals("activity")) {
                viewsToRemove.add(child);
            }
        }

        for (View view : viewsToRemove) {
            grid.removeView(view);
        }
    }

    private void addTaskToGrid(Task task, int row) {
        View taskView = LayoutInflater.from(this).inflate(R.layout.cell_weekly_activity, weeklyGrid, false);

        TextView titleView = taskView.findViewById(R.id.weekly_task_title);
        TextView timeView = taskView.findViewById(R.id.weekly_task_time);

        titleView.setText(task.getTitle());
        taskView.setTag("activity");

        String timeText = "";
        if (task.getStartTime() != null) {
            timeText = task.getStartTime().toString();
            if (task.getEndTime() != null) {
                timeText += " - " + task.getEndTime().toString();
            }
        }
        timeView.setText(timeText);

        int startHour = task.getStartTime().getHour();
        int columnSpan = 1;
        if (task.getEndTime() != null) {
            int duration = task.getEndTime().getHour() - startHour + 1;
            columnSpan = Math.max(1, duration);
        }

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(row);
        params.columnSpec = GridLayout.spec(startHour, columnSpan);
        params.width = (int) (70 * columnSpan * getResources().getDisplayMetrics().density);
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.setMargins(4, 4, 4, 4);

        taskView.setLayoutParams(params);
        weeklyGrid.addView(taskView);
    }

    private LocalDate getStartOfWeek(LocalDate date) {
        return date.minusDays(date.getDayOfWeek().getValue() - 1);
    }

    private void setWeekView() {
        dateText.setText(formatDateWeekly(chosenDate));

        ArrayList<String> days = daysInWeek(chosenDate);

        TextView monday = findViewById(R.id.monday_number);
        TextView tuesday = findViewById(R.id.tuesday_number);
        TextView wednesday = findViewById(R.id.wednesday_number);
        TextView thursday = findViewById(R.id.thursday_number);
        TextView friday = findViewById(R.id.friday_number);
        TextView saturday = findViewById(R.id.saturday_number);
        TextView sunday = findViewById(R.id.sunday_number);

        monday.setText(days.get(0));
        tuesday.setText(days.get(1));
        wednesday.setText(days.get(2));
        thursday.setText(days.get(3));
        friday.setText(days.get(4));
        saturday.setText(days.get(5));
        sunday.setText(days.get(6));

        //loadTasksOnGrid();
    }

    private ArrayList<String> daysInWeek(LocalDate date) {
        ArrayList<String> days = new ArrayList<>();

        int dayOfWeek = date.getDayOfWeek().getValue();
        LocalDate firstDayOfWeek = date.minusDays(dayOfWeek - 1);

        for (int i = 0; i < 7; i++) {
            LocalDate current = firstDayOfWeek.plusDays(i);
            days.add(String.valueOf(current.getDayOfMonth()));
        }

        return days;
    }

    private String formatDateWeekly(LocalDate date) {
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);

        int firstWeekDay = firstDayOfMonth.getDayOfWeek().getValue() % 7; // % 7 cause Sunday should be 0
        int dayOfMonth = date.getDayOfMonth();

        int weekOfMonth = ((dayOfMonth + firstWeekDay - 1) / 7) + 1;

        if (date.getDayOfWeek().getValue() == 7) {
            weekOfMonth = ((dayOfMonth + firstWeekDay - 1) / 7);
            if (weekOfMonth == 0) {
                weekOfMonth = 1;
            }
        }

        return weekOfMonth + "週" + date.format(DateTimeFormatter.ofPattern("MM月 yyyy年"));
    }

    private void updateWeeklyView() {
        LocalDate weekStart = chosenDate.minusDays(chosenDate.getDayOfWeek().getValue() - 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("週 MM月 yyyy年");
        dateText.setText(weekStart.format(formatter));

        updateDayNumbers(weekStart);
    }

    private void nextWeek(View view) {
        chosenDate = chosenDate.plusWeeks(1);
        updateWeekView();
        loadWeeklyTasks();
    }

    private void previousWeek(View view) {
        chosenDate = chosenDate.minusWeeks(1);
        updateWeekView();
        loadWeeklyTasks();
    }

    @SuppressLint("ResourceType")
    private void changeCalendarType(String type) {
        TextView dailyType = findViewById(R.id.daily_calendar_btn);
        TextView weeklyType = findViewById(R.id.weekly_calendar_btn);
        TextView monthlyType = findViewById(R.id.monthly_calendar_btn);

        // Indicate which category is active by using different colors of font
        switch (type) {
            case "Daily":
                dailyType.setTextColor(getColor(R.color.active_day));
                weeklyType.setTextColor(getColor(R.color.inactive_day));
                monthlyType.setTextColor(getColor(R.color.inactive_day));

                goToDailyView(findViewById(R.layout.activity_weekly_calendar));
                break;

            case "Weekly":
                weeklyType.setTextColor(getColor(R.color.active_day));
                dailyType.setTextColor(getColor(R.color.inactive_day));
                monthlyType.setTextColor(getColor(R.color.inactive_day));

                break;

            case "Monthly":
                monthlyType.setTextColor(getColor(R.color.active_day));
                dailyType.setTextColor(getColor(R.color.inactive_day));
                weeklyType.setTextColor(getColor(R.color.inactive_day));

                goToMonthlyView(findViewById(R.layout.activity_weekly_calendar));
                break;
        }
    }

    private void changeToDailyType(View view) {
        changeCalendarType("Daily");
    }

    private void changeToWeeklyType(View view) {
        changeCalendarType("Weekly");
    }

    private void changeToMonthlyType(View view) {
        changeCalendarType("Monthly");
    }

    private void goToDailyView(View view) {
        Intent intent = new Intent(CalendarWeeklyActivity.this, CalendarDailyView.class);
        intent.putExtra("selectedDate", chosenDate.toString());
        startActivity(intent);
    }

    private void goToMonthlyView(View view) {
        Intent intent = new Intent(CalendarWeeklyActivity.this, CalendarActivity.class);
        intent.putExtra("selectedDate", chosenDate.toString());
        startActivity(intent);
    }

    private void goBackToMenu(View view) {
        Intent intent = new Intent(CalendarWeeklyActivity.this, MainActivity.class);
        intent.putExtra("selectedDate", chosenDate.toString());
        startActivity(intent);

        overridePendingTransition(R.anim.slide_from_right, 0);
    }
}
