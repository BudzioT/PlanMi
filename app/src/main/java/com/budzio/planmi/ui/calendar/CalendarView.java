package com.budzio.planmi.ui.calendar;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.budzio.planmi.R;

// Singular day cell
public class CalendarView extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView day;
    private final CalendarAdapter.OnItemListener onItemListener;

    public CalendarView(View view, CalendarAdapter.OnItemListener onItemListener) {
        super(view);

        // Set this as a day cell, watch out for clicks
        day = view.findViewById(R.id.dayCell);
        this.onItemListener = onItemListener;
        view.setOnClickListener(this);
    }

    // Forward click to CalendarAdapter (grid)
    public void onClick(View view) {
        onItemListener.onItemClick(getAdapterPosition(), (String)day.getText());
    }
}
