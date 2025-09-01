package com.budzio.planmi.ui.calendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.budzio.planmi.R;

import java.util.ArrayList;

// Grid for days
public class CalendarAdapter extends RecyclerView.Adapter<CalendarView> {
    private final ArrayList<String> daysInMonth;
    private final OnItemListener onItemListener;

    // You can click the cell, wow!
    public interface OnItemListener {
        void onItemClick(int position, String dayNumber);
    }

    public CalendarAdapter(ArrayList<String> daysInMonth, OnItemListener onItemListener) {
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

    // Put the day number in cell
    @Override
    public void onBindViewHolder(@NonNull CalendarView holder, int position) {
        holder.day.setText(daysInMonth.get(position));
    }

    @Override
    public int getItemCount() {
        return daysInMonth.size();
    }
    // TODO: Fix clicking on cell, add other calendars
}
