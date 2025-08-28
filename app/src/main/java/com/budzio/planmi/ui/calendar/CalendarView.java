package com.budzio.planmi.ui.calendar;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.budzio.planmi.R;

public class CalendarView extends RecyclerView.ViewHolder {
    public TextView day;

    public CalendarView(View view) {
        super(view);

        day = view.findViewById(R.id.dayCell);
    }
}
