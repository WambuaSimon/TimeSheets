package biz.wizag.timesheets;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class Activity_Task extends AppCompatActivity {
    TextView task;
    String task_data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task);

        task = findViewById(R.id.task);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
// get data via the key
        String value1 = extras.getString("task");
        if (value1 != null) {
            task.setText(value1);
            // do something with the data
        }
    }
}
