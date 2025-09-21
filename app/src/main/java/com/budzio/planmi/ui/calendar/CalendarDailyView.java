package com.budzio.planmi.ui.calendar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.budzio.planmi.R;
import com.budzio.planmi.data.AddTaskDialog;
import com.budzio.planmi.data.Note;
import com.budzio.planmi.data.NoteImageAdapter;
import com.budzio.planmi.data.Task;
import com.budzio.planmi.data.TaskAdapter;
import com.budzio.planmi.data.TaskManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

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

    // Note stuff
    private TextInputEditText noteText;
    private Button addNoteImageBtn, saveNoteBtn;
    private RecyclerView noteImagesRecycler;
    private NoteImageAdapter imagesAdapter;
    private Note currentNote;
    private LocalDate selectedDate;

    private LinearLayout noteImageContainer;

    private static final int PICK_IMAGE_REQUEST = 1001;


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

        // Note stuff again
        noteText = findViewById(R.id.note_text);
        addNoteImageBtn = findViewById(R.id.add_note_image_btn);
        saveNoteBtn = findViewById(R.id.save_note);
        noteImagesRecycler = findViewById(R.id.note_images_recycler);

        selectedDate = chosenDate;
        loadNote();

        addNoteImageBtn.setOnClickListener(v -> pickImage());
        saveNoteBtn.setOnClickListener(v -> saveNote());

        noteText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) saveNote();
        });

        Button nextDayBtn = findViewById(R.id.next_day_btn);
        Button prevDayBtn = findViewById(R.id.prev_day_btn);
        nextDayBtn.setOnClickListener(this::goToNextDay);
        prevDayBtn.setOnClickListener(this::goToPrevDay);


        // Back button to start screen connection
        ImageButton menuBackBtn = findViewById(R.id.calendar_back_btn);
        menuBackBtn.setOnClickListener(this::goBackToMenu);

        setupCalendarTypeButtons();
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
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

        clearTaskViewsFromGrid();
        addTasksToGrid(tasks);
    }

    private void clearTaskViewsFromGrid() {
        GridLayout grid = findViewById(R.id.daily_task_grid);
        if (grid == null) return;

        List<View> viewsToRemove = new ArrayList<>();

        for (int i = 0; i < grid.getChildCount(); i++) {
            View child = grid.getChildAt(i);
            if (child.getTag() != null && child.getTag().equals("task")) {
                viewsToRemove.add(child);
            }
        }

        for (View view : viewsToRemove) {
            grid.removeView(view);
        }
    }

    private void addTasksToGrid(List<Task> tasks) {
        GridLayout grid = findViewById(R.id.daily_task_grid);
        if (grid == null) return;

        for (Task task : tasks) {
            if (task.getStartTime() != null) {
                addTaskToGrid(task, grid);
            }
        }
    }

    private void addTaskToGrid(Task task, GridLayout grid) {
        if (task.getStartTime() == null || grid == null) {
            return;
        }

        View taskView = LayoutInflater.from(this).inflate(R.layout.cell_weekly_activity, grid, false);

        TextView titleView = taskView.findViewById(R.id.weekly_task_title);
        TextView timeView = taskView.findViewById(R.id.weekly_task_time);

        if (titleView != null) {
            titleView.setText(task.getTitle());
        }
        taskView.setTag("task");

        String timeText = "";
        if (task.getStartTime() != null) {
            timeText = task.getStartTime().toString();
            if (task.getEndTime() != null) {
                timeText += " - " + task.getEndTime().toString();
            }
        }
        if (timeView != null) {
            timeView.setText(timeText);
        }

        int startHour = task.getStartTime().getHour();
        int columnSpan = 1;
        if (task.getEndTime() != null) {
            int duration = task.getEndTime().getHour() - startHour + 1;
            columnSpan = Math.max(1, duration);
        }

        int maxColumns = 25;
        if (startHour + columnSpan > maxColumns) {
            columnSpan = maxColumns - startHour;
        }

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(1);
        params.columnSpec = GridLayout.spec(startHour, columnSpan);
        params.width = (int) (70 * columnSpan * getResources().getDisplayMetrics().density);
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.setMargins(4, 4, 4, 4);

        taskView.setLayoutParams(params);
        grid.addView(taskView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                currentNote.getImageUris().add(imageUri.toString());
                imagesAdapter.notifyDataSetChanged();
                saveNote();
            }
        }
    }

    private void loadNote() {
        currentNote = TaskManager.getInstance().getNoteForDate(selectedDate);
        noteText.setText(currentNote.getText());

        imagesAdapter = new NoteImageAdapter(currentNote.getImageUris());
        noteImagesRecycler.setAdapter(imagesAdapter);
        noteImagesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void saveNote() {
        if (currentNote != null) {
            currentNote.setText(noteText.getText().toString());
            TaskManager.getInstance().saveNoteForDate(selectedDate, currentNote);
        }
    }

    private void goBackToMenu(View view) {
        Intent intent = new Intent(CalendarDailyView.this, CalendarActivity.class);
        startActivity(intent);

        overridePendingTransition(R.anim.slide_from_right, 0);
    }

    private void goToNextDay(View view) {
        saveNote();
        chosenDate = chosenDate.plusDays(1);
        selectedDate = chosenDate;
        updateDayView();
        loadTasks();
        loadNote();
    }

    private void goToPrevDay(View view) {
        saveNote();
        chosenDate = chosenDate.plusDays(-1);
        selectedDate = chosenDate;
        updateDayView();
        loadTasks();
        loadNote();
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
