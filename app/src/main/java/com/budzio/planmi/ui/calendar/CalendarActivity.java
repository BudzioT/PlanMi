package com.budzio.planmi.ui.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.budzio.planmi.R;
import com.budzio.planmi.ui.main.MainActivity;

public class CalendarActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calendar);

        ImageButton backButton = findViewById(R.id.calendar_back_btn);
        backButton.setOnClickListener(this::goBackToMenu);
    }

    public void goBackToMenu(View view) {
        Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
        startActivity(intent);

        overridePendingTransition(R.anim.slide_from_right, 0);
    }
}
