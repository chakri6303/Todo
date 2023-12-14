package com.example.todo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

// TaskAdapter.java
public class TaskAdapter extends ArrayAdapter<Task> {
    private Context context;
    private TaskDBHelper dbHelper; // Add this line to declare dbHelper

    public TaskAdapter(Context context, ArrayList<Task> tasks, TaskDBHelper dbHelper) {
        super(context, 0, tasks);
        this.context = context;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Task task = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.taskDescription);
        textView.setText(task.getDescription());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PT, 10);

        if (task != null) {
            textView.setText(task.getDescription());
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle item click (for editing)
                editTask(task);
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Handle long press (for deleting)
                deleteTask(task);
                return true;
            }
        });

        return convertView;
    }
    private void editTask(final Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Task");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(task.getDescription());
        builder.setView(input);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String newDescription = input.getText().toString().trim();
                task.setDescription(newDescription);

                // Notify any listener (e.g., the activity) that the task was edited
                if (onTaskEditListener != null) {
                    onTaskEditListener.onTaskEdit(task);
                }

                notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void deleteTask(final Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Task");
        builder.setMessage("Are you sure you want to delete this task?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete the task from the database
                dbHelper.deleteTask(task);

                // Remove the task from the in-memory list
                remove(task);

                notifyDataSetChanged();
            }
        });


        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Interface to define a listener for task edit events
    public interface OnTaskEditListener {
        void onTaskEdit(Task task);
    }

    private OnTaskEditListener onTaskEditListener;

    public void setOnTaskEditListener(OnTaskEditListener listener) {
        this.onTaskEditListener = listener;
    }

    // Interface to define a listener for task delete events
    public interface OnTaskDeleteListener {
        void onTaskDelete(Task task);
    }

    private OnTaskDeleteListener onTaskDeleteListener;

    public void setOnTaskDeleteListener(OnTaskDeleteListener listener) {
        this.onTaskDeleteListener = listener;
    }
}