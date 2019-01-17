package biz.wizag.timesheets;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import biz.wizag.SessionManager;
import me.tittojose.www.timerangepicker_library.TimeRangePickerDialog;


public class TimeRangeSelecterActivityFragment extends Fragment implements TimeRangePickerDialog.OnTimeRangeSelectedListener {
    TextView timeRangeSelectedTextView;
    public static final String TIMERANGEPICKER_TAG = "timerangepicker";
    private static final String TAG = "CalenderTest";
    String selecte_date;
    String startTime, endTime;
    Spinner project;
    EditText task;
    JSONArray projects_array;
    ArrayList<String> Projects;
    String project_name;
    SharedPreferences sp;
    String email;
    String task_txt;
    SessionManager session;
    String project_txt;
    String add_project_txt;
    int id_detail;
    Button add;
    EditText add_project;
    LinearLayout project_layout;

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
        session = new SessionManager(getActivity());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Event event = new Event(calendar.getTimeInMillis(), "Test");
        calenderEvent.addEvent(event);

        sp = this.getActivity().getSharedPreferences("profile", Context.MODE_PRIVATE);

        email = sp.getString("email", null);

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
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_task, null);
        dialogBuilder.setView(dialogView);
        project = dialogView.findViewById(R.id.project);
        Projects = new ArrayList<>();
        task = dialogView.findViewById(R.id.task_description);

        project.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String value = spinner_sell_details.getSelectedItem().toString();
                try {
                    if (projects_array != null) {
                        JSONObject projectClicked = projects_array.getJSONObject(i);

                        if (projectClicked != null) {
                            id_detail = projectClicked.getInt("id");


                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                project_txt = project.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dialogBuilder.setTitle("Task Details");
        dialogBuilder.setCancelable(false);

        getProjects();

        dialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                task_txt = task.getText().toString();
                createTasks();
//                Toast.makeText(getActivity(), "Send to db", Toast.LENGTH_SHORT).show();

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        dialogBuilder.setNeutralButton("Add Project", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                /*edittext AND button*/
//                project_layout.setVisibility(View.VISIBLE);
//                add_project_txt = add_project.getText().toString();
                showProjectDialog();
//                createProject();


            }
        });


        AlertDialog b = dialogBuilder.create();
        b.show();


    }


    public void showProjectDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_project, null);
        dialogBuilder.setView(dialogView);
        project_layout = dialogView.findViewById(R.id.project_layout);
        add_project = dialogView.findViewById(R.id.add_project);


        dialogBuilder.setTitle("Add Project");
        dialogBuilder.setCancelable(false);

        getProjects();

        dialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                task_txt = task.getText().toString();
                createProject();
//                Toast.makeText(getActivity(), "Send to db", Toast.LENGTH_SHORT).show();

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


    private void createTasks() {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(getActivity());
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://timesheets.wizag.biz/api/tasks",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject != null) {
                                String success = jsonObject.getString("success");
                                if (success.equalsIgnoreCase("true")) {
                                    Toast.makeText(getActivity(), "Information has been submitted successfully", Toast.LENGTH_SHORT).show();
                                    /*redirect to activity list*/
                                    Intent intent = new Intent(getContext(), Activity_Show_Tasks.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getActivity(), "An Error Occurred", Toast.LENGTH_SHORT).show();


                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Toast.makeText(Activity_Sell.this, "", Toast.LENGTH_SHORT).show();
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                error.getMessage();
                error.printStackTrace();

                Toast.makeText(getActivity(), "Task could not be created", Toast.LENGTH_SHORT).show();


            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
//                params.put("email", email);
                params.put("date", selecte_date);
                params.put("start_time", startTime);
                params.put("end_time", endTime);
                params.put("project_id", String.valueOf(id_detail));
                params.put("name", task_txt);
                return params;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SessionManager sessionManager = new SessionManager(getActivity());
                HashMap<String, String> user = sessionManager.getUserDetails();
                String token = user.get("access_token");
                String bearer = "Bearer " + token;
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


    private void getProjects() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://timesheets.wizag.biz/api/projects", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    pDialog.dismiss();
                    if (jsonObject != null) {

                        projects_array = jsonObject.getJSONArray("data");

                        for (int z = 0; z < projects_array.length(); z++) {
                            JSONObject suppliers_object = projects_array.getJSONObject(z);
                            project_name = suppliers_object.getString("name");

                            if (projects_array != null) {

                                if (Projects.contains(project_name)) {


                                } else {

                                    //Toast.makeText(getApplicationContext(), "data\n" + size.getJSONObject(m).getString("size"), Toast.LENGTH_SHORT).show();
                                    Projects.add(project_name);
//                                    Type.add(materialTypes.getJSONObject(p).getString("id"));

                                }

                            }


                        }


                    }
                    project.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, Projects));


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
                String token = user.get("access_token");
                String bearer = "Bearer " + token;
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

    private void createProject() {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(getActivity());
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://timesheets.wizag.biz/api/projects",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if (success.equalsIgnoreCase("true")) {
                                Toast.makeText(getActivity(), "Project has been created successfully", Toast.LENGTH_SHORT).show();
                                /*redirect to activity list*/
//                                Intent intent = new Intent(getContext(), TimeRangeSelecterActivity.class);
//                                startActivity(intent);
                            } else {
                                Toast.makeText(getActivity(), "An Error Occurred", Toast.LENGTH_SHORT).show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Toast.makeText(Activity_Sell.this, "", Toast.LENGTH_SHORT).show();
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                error.getMessage();
                error.printStackTrace();

                Toast.makeText(getActivity(), "Project could not be created", Toast.LENGTH_SHORT).show();


            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", add_project.getText().toString());


                return params;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SessionManager sessionManager = new SessionManager(getActivity());
                HashMap<String, String> user = sessionManager.getUserDetails();
                String token = user.get("access_token");
                String bearer = "Bearer " + token;
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
