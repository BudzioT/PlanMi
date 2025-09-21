package com.budzio.planmi.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.budzio.planmi.R;

import org.w3c.dom.Text;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks;

    public TaskAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void updateTasks(List<Task> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_daily, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final CheckBox checkBox;
        private final TextView titleTextView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.task_checkbox);
            titleTextView = itemView.findViewById(R.id.task_title);
        }

        public void bind(Task task) {
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(task.isCompleted());
            titleTextView.setText(task.getTitle());

            if (task.isScheduled() && task.getStartTime() != null) {
                String timeText = task.getStartTime().toString();
                if (task.getEndTime() != null) {
                    timeText += " - " + task.getEndTime().toString();
                }
            }

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                task.setCompleted(isChecked);
                TaskManager.getInstance().updateTask(task);
            });
        }
    }
}
