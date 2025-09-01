package com.budzio.planmi.ui.calendar;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.budzio.planmi.R;

import java.util.ArrayList;
import java.util.Calendar;

// Grid for days
public class CalendarAdapter extends RecyclerView.Adapter<CalendarView> {
    private final ArrayList<CalendarDay> daysInMonth;
    private final OnItemListener onItemListener;

    // You can click the cell, wow!
    public interface OnItemListener {
        void onItemClick(int position, String dayNumber);
    }

    public CalendarAdapter(ArrayList<CalendarDay> daysInMonth, OnItemListener onItemListener) {
        this.daysInMonth = daysInMonth;
        this.onItemListener = onItemListener;
    }

    // Handle initializing and all stuff like that
    @NonNull
    @Override
    public CalendarView onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        // Grab the cells
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cell_calendar, parent, false);

        // Set their size to match days of week
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * (1.0 / 6.0)); //  6 cells (6x7 grid), I love hardcoding stuff

        return new CalendarView(view, onItemListener);
    }

    // Put the day number in cell and do fancy styling for each day if it needs to be dynamic
    @Override
    public void onBindViewHolder(@NonNull CalendarView holder, int position) {
        CalendarDay day = daysInMonth.get(position);

        // If it's today - make a border
        if (day.isCurrentDay) {
            holder.day.setBackgroundResource(R.drawable.pink_circle);
            holder.day.setTextColor(Color.WHITE);
        }
        else {
            holder.day.setBackground(null);
        }

        // Set day number & make different colors for previous/next month days vs current month ones
        holder.day.setText(day.dayText);
        if (day.isCurrentMonth) {
            holder.day.setTextColor(holder.itemView.getContext().getColor(R.color.active_day));
        }
        else {
            holder.day.setTextColor(holder.itemView.getContext().getColor(R.color.inactive_day));
        }
    }

    @Override
    public int getItemCount() {
        return daysInMonth.size();
    }

    // TODO: Add other calendars
}
