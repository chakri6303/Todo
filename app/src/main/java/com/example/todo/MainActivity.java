package com.example.todo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText taskInput;
    private ListView taskList;
    private ArrayList<Task> tasks;
    private TaskAdapter taskAdapter;
    private TaskDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskList = findViewById(R.id.taskList);
        dbHelper = new TaskDBHelper(this);
        tasks = new ArrayList<>();
        taskAdapter = new TaskAdapter(this, tasks,dbHelper);
        taskList.setAdapter(taskAdapter);

        loadTasks();

        FloatingActionButton fabAddTask = findViewById(R.id.fabAddTask);
        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTaskDialog();
            }
        });
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Task");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String taskDescription = input.getText().toString().trim();
                if (!taskDescription.isEmpty()) {
                    Task newTask = new Task(taskDescription);
                    addTask(newTask);
                    taskAdapter.notifyDataSetChanged();
                }
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

    private void loadTasks() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(TaskDBHelper.TABLE_NAME, null, null, null, null, null, null);

        tasks.clear();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(TaskDBHelper.COLUMN_ID);
                int descriptionIndex = cursor.getColumnIndex(TaskDBHelper.COLUMN_DESCRIPTION);

                if (idIndex != -1 && descriptionIndex != -1) {
                    int id = cursor.getInt(idIndex);
                    String description = cursor.getString(descriptionIndex);

                    Task task = new Task(id, description);
                    tasks.add(task);
                }
            }

            cursor.close();
        }

        taskAdapter.notifyDataSetChanged();
    }

    private void addTask(Task task) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskDBHelper.COLUMN_DESCRIPTION, task.getDescription());

        long id = database.insert(TaskDBHelper.TABLE_NAME, null, values);
        task.setId((int) id);

        tasks.add(task);
    }
}