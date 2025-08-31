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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.budzio.planmi.R;
import com.budzio.planmi.ui.main.MainActivity;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CalendarActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {
    // Grid for days
    private RecyclerView calendarRecView;
    // Label for date
    private TextView dateText;
    // Current date
    private LocalDate chosenDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar);

        chosenDate = LocalDate.now();
        createDateStuff();

        // Buttons to control months
        Button nextMonthBtn = findViewById(R.id.next_month_btn);
        Button prevMonthBtn = findViewById(R.id.prev_month_btn);
        nextMonthBtn.setOnClickListener(this::nextMonth);
        prevMonthBtn.setOnClickListener(this::prevMonth);

        // Back button to start screen connection
        ImageButton menuBackBtn = findViewById(R.id.calendar_back_btn);
        menuBackBtn.setOnClickListener(this::goBackToMenu);
    }

    // Initialize some date stuff
    private void createDateStuff() {
        // Just find grid and date label by IDs
        calendarRecView = findViewById(R.id.calendar_day_view);
        dateText = findViewById(R.id.dateTextView);
    }

    // Just update the month/year with chosen date
    private void setMonthView() {
        dateText.setText(formatDate(chosenDate));

        // Generate days in current month
        ArrayList<String> daysInMonth = daysInMonth(chosenDate);

        // Create grid with days based on month, make it have 7 rows and just slap it as active
        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecView.setLayoutManager(layoutManager);
        calendarRecView.setAdapter(calendarAdapter);
    }

    // Return how days in certain month should be shown
    private ArrayList<String> daysInMonth(LocalDate date) {
        ArrayList<String> days = new ArrayList<>();

        // Grab num of days in current month
        YearMonth yearMonth = YearMonth.from(date);
        int daysNum = yearMonth.lengthOfMonth();

        // Check which day of week is the first month
        LocalDate firstDay = chosenDate.withDayOfMonth(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue();

        // Iterate through number of days and add proper look for this month
        for (int i = 1; i <= 42; i++) {  // 42 - cause calendar has dimension 7x6 = 42 cells
            // Padding days, so previous/next month
            if (i <= dayOfWeek || i > daysNum + dayOfWeek) {
                days.add("");
                continue;
            }

            // Otherwise add a normal day
            days.add(String.valueOf(i - dayOfWeek));
        }

        return days;
    }

    // Format the date to fancy style
    private String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM月 yyyy年");
        return date.format(formatter);
    }

    // Make transition to Menu screen (starting one)
    private void goBackToMenu(View view) {
        Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
        startActivity(intent);

        overridePendingTransition(R.anim.slide_from_right, 0);
    }

    // Update the calendar to the next month
    private void nextMonth(View view) {
        chosenDate = chosenDate.plusMonths(1);
        setMonthView();
    }

    // Update the calendar to the previous month
    private void prevMonth(View view) {
        chosenDate = chosenDate.minusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, String dayNumber) {
        if (!dayNumber.isEmpty()) {
            return;
        }

        String alert = "Selected " + dateText + " " + formatDate(chosenDate);
        Toast.makeText(this, alert, Toast.LENGTH_LONG).show();
    }
}
