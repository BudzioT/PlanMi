package com.budzio.planmi.ui.calendar;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.budzio.planmi.R;
import com.budzio.planmi.ui.main.MainActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CalendarWeeklyActivity extends AppCompatActivity {
    // Current date
    private LocalDate chosenDate;
    private TextView dateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weekly_calendar);

        // All date related shenanigans
        chosenDate = LocalDate.now();
        dateText = findViewById(R.id.dateTextView);

        // Buttons to control weeks
        Button nextWeekBtn = findViewById(R.id.next_week_btn);
        Button prevWeekBtn = findViewById(R.id.prev_week_btn);
        nextWeekBtn.setOnClickListener(this::nextWeek);
        prevWeekBtn.setOnClickListener(this::previousWeek);

        setWeekView();


        // Calendar type buttons
        TextView dailyTypeBtn = findViewById(R.id.daily_calendar_btn);
        TextView weeklyTypeBtn = findViewById(R.id.weekly_calendar_btn);
        TextView monthlyTypeBtn = findViewById(R.id.monthly_calendar_btn);
        dailyTypeBtn.setOnClickListener(this::changeToDailyType);
        weeklyTypeBtn.setOnClickListener(this::changeToWeeklyType);
        monthlyTypeBtn.setOnClickListener(this::changeToMonthlyType);

        // Back button to start screen connection
        ImageButton menuBackBtn = findViewById(R.id.calendar_back_btn);
        menuBackBtn.setOnClickListener(this::goBackToMenu);
    }

    private void setWeekView() {
        dateText.setText(formatDateWeekly(chosenDate));
    }

    private void formatDateWeekly(LocalDate date) {
        DateTimeFormatter formatter; // TODO: CONTINUE HERE, FINISH WEEKLY CALENDAR DAYS AND DAILY ONE TOO
        return date.format(formatter);
    }

    private void nextWeek(View view) {
        chosenDate = chosenDate.plusWeeks(1);
    }

    private void previousWeek(View view) {
        chosenDate = chosenDate.minusWeeks(1);
    }

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

                goToDailyView();
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

                goToMonthlyView();
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

    private void goToDailyView() {
        Intent intent = new Intent(CalendarWeeklyActivity.this, CalendarDailyView.class);
        startActivity(intent);
    }

    private void goToMonthlyView() {
        Intent intent = new Intent(CalendarWeeklyActivity.this, CalendarActivity.class);
        startActivity(intent);
    }

    private void goBackToMenu(View view) {
        Intent intent = new Intent(CalendarWeeklyActivity.this, MainActivity.class);
        startActivity(intent);

        overridePendingTransition(R.anim.slide_from_right, 0);
    }
}
