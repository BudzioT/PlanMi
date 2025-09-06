package com.budzio.planmi.ui.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

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

        // Back button to start screen connection
        ImageButton menuBackBtn = findViewById(R.id.calendar_back_btn);
        menuBackBtn.setOnClickListener(this::goBackToMenu);
    }

    private void goBackToMenu(View view) {
        Intent intent = new Intent(CalendarDailyView.this, view.getClass());
        startActivity(intent);

        overridePendingTransition(R.anim.slide_from_right, 0);
    }
}
