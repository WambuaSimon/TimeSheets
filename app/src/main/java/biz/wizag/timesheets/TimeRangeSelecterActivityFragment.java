package biz.wizag.timesheets;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.skyhope.eventcalenderlibrary.CalenderEvent;
import com.skyhope.eventcalenderlibrary.listener.CalenderDayClickListener;
import com.skyhope.eventcalenderlibrary.model.DayContainerModel;
import com.skyhope.eventcalenderlibrary.model.Event;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import biz.wizag.SessionManager;
import me.tittojose.www.timerangepicker_library.TimeRangePickerDialog;


public class TimeRangeSelecterActivityFragment extends Fragment implements TimeRangePickerDialog.OnTimeRangeSelectedListener {
    Button selectTimeRangeButton;
    TextView timeRangeSelectedTextView;
    public static final String TIMERANGEPICKER_TAG = "timerangepicker";
    private static final String TAG = "CalenderTest";
    String selecte_date;
    String startTime,endTime;
    Spinner project;
    EditText task;
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
         startTime = startHour + " : " + startMin;
         endTime = endHour + " : " + endMin;
//        timeRangeSelectedTextView.setText(startTime + "\n" + endTime + "\n" + selecte_date);
        showTasksDialog();
    }


    public void showTasksDialog() {
        Button cancel, proceed;


        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_task, null);
        dialogBuilder.setView(dialogView);
        project = dialogView.findViewById(R.id.project);
        task = dialogView.findViewById(R.id.task_description);

        dialogBuilder.setTitle("Task Details");
        dialogBuilder.setCancelable(false);

        dialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

//                Toast.makeText(getActivity(), "Send to db", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), Activity_Show_Tasks.class);
                startActivity(intent);
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


    private void loadRequest() {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(getActivity());
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            pDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Toast.makeText(Activity_Sell.this, "", Toast.LENGTH_SHORT).show();
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.cancel();
                error.getMessage();
                Toast.makeText(getActivity(), "Task could not be created", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();

            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("material_item", String.valueOf(id_material));
                params.put("material_detail", String.valueOf(id_detail));
                params.put("material_quantity", quantity_txt);
                params.put("material_class", String.valueOf(id_class));
                params.put("material_unit", String.valueOf(id_unit));
                params.put("supplier_id", String.valueOf(id_supplier));
                params.put("driver_location", location);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SessionManager sessionManager = new SessionManager(getActivity());
                HashMap<String, String> user = sessionManager.getUserDetails();
                String token = user.get("access_token");
                String bearer = "Bearer ".concat(token);
                Map<String, String> headersSys = super.getHeaders();
                Map<String, String> headers = new HashMap<String, String>();
                headersSys.remove("Authorization");
                headers.put("Authorization", bearer);
                headers.putAll(headersSys);
                return headers;
            }
        };
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    /*Selecting Material type*/
    private void getProjects() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, " ", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    pDialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                pDialog.dismiss();
                Toast.makeText(getActivity(), "An Error Occurred" + error.getMessage(), Toast.LENGTH_LONG).show();

            }


        }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SessionManager sessionManager = new SessionManager(getActivity());
                HashMap<String, String> user = sessionManager.getUserDetails();
                String accessToken = user.get("access_token");

                String bearer = "Bearer " + accessToken;
                Map<String, String> headersSys = super.getHeaders();
                Map<String, String> headers = new HashMap<String, String>();
                headersSys.remove("Authorization");
                headers.put("Authorization", bearer);
                headers.putAll(headersSys);
                return headers;
            }
        };


        MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);


        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);


    }


    private void createTask() {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(getActivity());
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            pDialog.dismiss();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Toast.makeText(Activity_Sell.this, "", Toast.LENGTH_SHORT).show();
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.getMessage();
                Toast.makeText(getActivity(), "Could not create task", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("material_item", String.valueOf(id_material));
                params.put("material_detail", String.valueOf(id_detail));
                params.put("material_quantity", quantity_txt);
                params.put("material_class", String.valueOf(id_class));
                params.put("material_unit", String.valueOf(id_unit));
                params.put("supplier_id", String.valueOf(id_supplier));
                params.put("driver_location", location);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SessionManager sessionManager = new SessionManager(getActivity());
                HashMap<String, String> user = sessionManager.getUserDetails();
                String token = user.get("access_token");
                String bearer = "Bearer ".concat(token);
                Map<String, String> headersSys = super.getHeaders();
                Map<String, String> headers = new HashMap<String, String>();
                headersSys.remove("Authorization");
                headers.put("Authorization", bearer);
                headers.putAll(headersSys);
                return headers;
            }
        };
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


}
