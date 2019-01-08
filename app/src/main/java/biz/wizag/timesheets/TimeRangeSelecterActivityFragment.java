package biz.wizag.timesheets;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skyhope.eventcalenderlibrary.CalenderEvent;
import com.skyhope.eventcalenderlibrary.listener.CalenderDayClickListener;
import com.skyhope.eventcalenderlibrary.model.DayContainerModel;
import com.skyhope.eventcalenderlibrary.model.Event;

import org.json.JSONObject;

import java.util.Calendar;

import me.tittojose.www.timerangepicker_library.TimeRangePickerDialog;


public class TimeRangeSelecterActivityFragment extends Fragment implements TimeRangePickerDialog.OnTimeRangeSelectedListener {
    Button selectTimeRangeButton;
    TextView timeRangeSelectedTextView;
    public static final String TIMERANGEPICKER_TAG = "timerangepicker";
    private static final String TAG = "CalenderTest";
    String selecte_date;

    public TimeRangeSelecterActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time_range_selecter, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        CalenderEvent calenderEvent = getActivity().findViewById(R.id.calender_event);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Event event = new Event(calendar.getTimeInMillis(), "Test");
        calenderEvent.addEvent(event);


        timeRangeSelectedTextView = (TextView) getActivity().findViewById(R.id.tvSelectedTimeRangeFragment);
        if (savedInstanceState != null) {
            TimeRangePickerDialog tpd = (TimeRangePickerDialog) getActivity().getSupportFragmentManager()
                    .findFragmentByTag(TIMERANGEPICKER_TAG);
            if (tpd != null) {
                tpd.setOnTimeRangeSetListener(this);
            }
        }

        calenderEvent.initCalderItemClickCallback(new CalenderDayClickListener() {
            @Override
            public void onGetDay(DayContainerModel dayContainerModel) {
                Log.d(TAG, dayContainerModel.getDate());
                /*show time-range picker: pick start and end time then get start time,end time, and date*/

                /*on click set end time, invoke dialog to show project and task*/
                final TimeRangePickerDialog timePickerDialog = TimeRangePickerDialog.newInstance(
                        TimeRangeSelecterActivityFragment.this, false);
                timePickerDialog.show(getActivity().getSupportFragmentManager(), TIMERANGEPICKER_TAG);

                selecte_date = dayContainerModel.getDate();


            }
        });
    }

    @Override
    public void onTimeRangeSelected(int startHour, int startMin, int endHour, int endMin) {
        String startTime = startHour + " : " + startMin;
        String endTime = endHour + " : " + endMin;
        timeRangeSelectedTextView.setText(startTime + "\n" + endTime + "\n" + selecte_date);
        showTasksDialog();
    }


    public void showTasksDialog() {
        Button cancel, proceed;
        Spinner project;
        EditText task;

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_task, null);
        dialogBuilder.setView(dialogView);
        project = dialogView.findViewById(R.id.project);
        task = dialogView.findViewById(R.id.task_description);

       /* cancel = dialogView.findViewById(R.id.cancel);
        proceed = dialogView.findViewById(R.id.proceed);
*/

        dialogBuilder.setTitle("Task Details");
        dialogBuilder.setCancelable(false);

       /* cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
        dialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                Toast.makeText(getActivity(), "Send to db", Toast.LENGTH_SHORT).show();

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();


    }
}
