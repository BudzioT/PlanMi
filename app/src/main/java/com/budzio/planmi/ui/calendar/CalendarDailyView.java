package com.budzio.planmi.ui.calendar;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.budzio.planmi.R;

import java.time.LocalDate;

public class CalendarDailyView extends AppCompatActivity {
    // Current date
    private LocalDate chosenDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_daily_view);

        chosenDate = LocalDate.now();
    }
}
