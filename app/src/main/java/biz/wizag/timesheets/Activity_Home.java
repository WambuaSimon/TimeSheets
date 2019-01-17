package biz.wizag.timesheets;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.skyhope.eventcalenderlibrary.CalenderEvent;
import com.skyhope.eventcalenderlibrary.listener.CalenderDayClickListener;
import com.skyhope.eventcalenderlibrary.model.DayContainerModel;
import com.skyhope.eventcalenderlibrary.model.Event;

import java.util.Calendar;

public class Activity_Home extends AppCompatActivity {
    private static final String TAG = "CalenderTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CalenderEvent calenderEvent = findViewById(R.id.calender_event);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Event event = new Event(calendar.getTimeInMillis(), "Test");
        calenderEvent.addEvent(event);

        calenderEvent.initCalderItemClickCallback(new CalenderDayClickListener() {
            @Override
            public void onGetDay(DayContainerModel dayContainerModel) {
                Log.d(TAG, dayContainerModel.getDate());
                /*show time-range picker: pick start and end time then get start time,end time, and date*/

                /*on click set end time, invoke dialog to show project and task*/




            }
        });
    }
}
