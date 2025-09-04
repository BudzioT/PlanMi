package com.budzio.planmi.ui.calendar;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewDebug;
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

import java.io.Console;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class CalendarActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {
    // Grid for days
    private RecyclerView calendarRecView;
    // Label for date
    private TextView dateText;
    // Current date
    private LocalDate chosenDate;
    private int selectedDayNum;
    private String calendar_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar);

        chosenDate = LocalDate.now();
        createDateStuff();
        setMonthView();

        // Calendar type stuff
        changeCalendarType("Monthly");

        // Calendar type buttons
        TextView dailyTypeBtn = findViewById(R.id.daily_calendar_btn);
        TextView weeklyTypeBtn = findViewById(R.id.weekly_calendar_btn);
        TextView monthlyTypeBtn = findViewById(R.id.monthly_calendar_btn);
        dailyTypeBtn.setOnClickListener(this::changeToDailyType);
        weeklyTypeBtn.setOnClickListener(this::changeToWeeklyType);
        monthlyTypeBtn.setOnClickListener(this::changeToMonthlyType);


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
        calendar_type = "Monthly";
    }

    // Just update the month/year with chosen date
    private void setMonthView() {
        dateText.setText(formatDateMonthly(chosenDate));

        // Generate days in current month
        ArrayList<CalendarDay> daysInMonth = daysInMonth(chosenDate);

        // Create grid with days based on month, make it have 7 rows and just slap it as active
        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecView.setLayoutManager(layoutManager);
        calendarRecView.setAdapter(calendarAdapter);
    }

    // Return how days in certain month should be shown
    private ArrayList<CalendarDay> daysInMonth(LocalDate date) {
        ArrayList<CalendarDay> days = new ArrayList<>();

        // Grab num of days in current month
        YearMonth yearMonth = YearMonth.from(date);
        int daysNum = yearMonth.lengthOfMonth();

        // Check which day of week is the first month
        LocalDate firstDay = chosenDate.withDayOfMonth(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue() - 1;  // Had to add -1 for it to work?? Ig 0 = Mon, 6 = Sun

        YearMonth prevMonth = yearMonth.minusMonths(1);
        int prevMonthLength = prevMonth.lengthOfMonth();

        // Iterate through number of days and add proper look for this month
        for (int i = 1; i <= 42; i++) {  // 42 - cause calendar has dimension 7x6 = 42 cells
            // Padding days, so previous/next month

            // Add previous month days
            if (i <= dayOfWeek) {
                int day = prevMonthLength - (dayOfWeek - i);
                days.add(new CalendarDay(String.valueOf(day), false, false));
            }
            // Add next month days
            else if (i > daysNum + dayOfWeek) {
                int day = i - (daysNum + dayOfWeek);
                days.add(new CalendarDay(String.valueOf(day), false, false));
            }
            // Otherwise add a normal day
            else {
                // Check if this day is today
                Calendar today = Calendar.getInstance();
                boolean isToday = (i - dayOfWeek) == chosenDate.getDayOfMonth() &&
                        chosenDate.getMonthValue() - 1 == (int)today.get(Calendar.MONTH) &&
                        chosenDate.getYear() == today.get(Calendar.YEAR);

                days.add(new CalendarDay(String.valueOf(i - dayOfWeek), true, isToday));
            }
        }

        return days;
    }

    // Format the date to fancy style
    private String formatDateMonthly(LocalDate date) {
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

    // Handle clicking day
    @Override
    public void onItemClick(int position, String dayNumber) {
        if (dayNumber.isEmpty()) {
            return;
        }

        String alert = "Selected  " + dayNumber + "日 " + formatDateMonthly(chosenDate);


        Toast.makeText(this, alert, Toast.LENGTH_SHORT).show();
    }

    // Change the layout and everything needed to see calendar in a different format
    private void changeCalendarType(String type) {
        this.calendar_type = type;

        TextView dailyType = findViewById(R.id.daily_calendar_btn);
        TextView weeklyType = findViewById(R.id.weekly_calendar_btn);
        TextView monthlyType = findViewById(R.id.monthly_calendar_btn);

        RecyclerView monthlyDayView = findViewById(R.id.calendar_day_view);

        // Indicate which category is active by using different colors of font
        switch (calendar_type) {
            case "Daily":
                dailyType.setTextColor(getColor(R.color.active_day));
                weeklyType.setTextColor(getColor(R.color.inactive_day));
                monthlyType.setTextColor(getColor(R.color.inactive_day));

                goToDailyView();
                break;

            case "Weekly":
                weeklyType.setTextColor(getColor(R.color.active_day));
                dailyType.setTextColor(getColor(R.color.inactive_day));
                monthlyType.setTextColor(getColor(R.color.inactive_day));

                monthlyDayView.setVisibility(GONE);
                break;

            case "Monthly":
                monthlyType.setTextColor(getColor(R.color.active_day));
                dailyType.setTextColor(getColor(R.color.inactive_day));
                weeklyType.setTextColor(getColor(R.color.inactive_day));

                monthlyDayView.setVisibility(VISIBLE);
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

    // Change view to the daily one, with all the tasks etc.
    private void goToDailyView() {

    }
}
