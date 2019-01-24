package biz.wizag.timesheets;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import biz.wizag.SessionManager;

public class Activity_Timesheets extends AppCompatActivity {

    private AppBarLayout appBarLayout;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", /*Locale.getDefault()*/Locale.ENGLISH);

    private CompactCalendarView compactCalendarView;

    private boolean isExpanded = false;

    TextView end_time, start_time;
    EditText task_description, create_new_project;
    Spinner project;
    Button proceed;
    FloatingActionButton add_project_fab;
    View layout;
    Toast toast;
    JSONArray projects_array;
    ArrayList<String> Projects;
    String project_txt, project_txt_spinner, project_name;
    int id_project;
    String task_txt, endTime_txt, startTime_txt;
    String date_txt;
    CardView end_time_card, start_time_card;
    private int mYear, mMonth, mDay, mHour, mMinute;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timesheets);

        session = new SessionManager(getApplicationContext());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Projects = new ArrayList<>();


        end_time = findViewById(R.id.end_time);
        start_time = findViewById(R.id.start_time);
        task_description = findViewById(R.id.task_description);
        project = findViewById(R.id.project);
        proceed = findViewById(R.id.proceed);
        start_time_card = findViewById(R.id.start_time_card);
        start_time_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(Activity_Timesheets.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

//                                start_time.setText(hourOfDay + ":" + minute);
                                start_time.setText(String.format("%02d:%02d", hourOfDay, minute));
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        end_time_card = findViewById(R.id.end_time_card);
        end_time_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(Activity_Timesheets.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

//                                start_time.setText(hourOfDay + ":" + minute);
                                end_time.setText(String.format("%02d:%02d", hourOfDay, minute));
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        proceed = findViewById(R.id.proceed);

        /*set up project spinner to get project id*/
        project.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                String value = spinner_sell_details.getSelectedItem().toString();
                try {
                    if (projects_array != null) {
                        JSONObject projectClicked = projects_array.getJSONObject(i);

                        if (projectClicked != null) {
                            id_project = projectClicked.getInt("id");


                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                project_txt_spinner = project.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        add_project_fab = findViewById(R.id.add_project);
        add_project_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*project create dialog*/

                CreateProjectDialog();
            }
        });

        appBarLayout = findViewById(R.id.app_bar_layout);

        // Set up the CompactCalendarView
        compactCalendarView = findViewById(R.id.compactcalendar_view);

        // Force English
        compactCalendarView.setLocale(TimeZone.getDefault(), /*Locale.getDefault()*/Locale.ENGLISH);

        compactCalendarView.setShouldDrawDaysHeader(true);

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                setSubtitle(dateFormat.format(dateClicked));
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                setSubtitle(dateFormat.format(firstDayOfNewMonth));
            }
        });

        // Set current date to today
        setCurrentDate(new Date());

        final ImageView arrow = findViewById(R.id.date_picker_arrow);

        RelativeLayout datePickerButton = findViewById(R.id.date_picker_button);

        datePickerButton.setOnClickListener(v -> {
            float rotation = isExpanded ? 0 : 180;
            ViewCompat.animate(arrow).rotation(rotation).start();

            isExpanded = !isExpanded;
            appBarLayout.setExpanded(isExpanded, true);
        });

        /*get projects*/
        getProjects();

        /*submit a timesheet entry*/
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task_txt = task_description.getText().toString();
                endTime_txt = end_time.getText().toString();
                startTime_txt = start_time.getText().toString();


                if (startTime_txt.isEmpty()) {
                    Toast.makeText(Activity_Timesheets.this, "Select Start Time to proceed", Toast.LENGTH_LONG).show();
                } else if (endTime_txt.isEmpty()) {
                    Toast.makeText(Activity_Timesheets.this, "Select End Time to proceed", Toast.LENGTH_LONG).show();
                } else if (task_txt.isEmpty()) {
                    Toast.makeText(Activity_Timesheets.this, "Enter Task to proceed", Toast.LENGTH_LONG).show();
                } else {
                    createTasks();
                }


            }
        });
    }

    private void setCurrentDate(Date date) {
        setSubtitle(dateFormat.format(date));
        if (compactCalendarView != null) {
            compactCalendarView.setCurrentDate(date);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        TextView tvTitle = findViewById(R.id.title);

        if (tvTitle != null) {
            tvTitle.setText(title);

        }
    }

    private void setSubtitle(String subtitle) {
        TextView datePickerTextView = findViewById(R.id.date_picker_text_view);

        if (datePickerTextView != null) {
            datePickerTextView.setText(subtitle);
            date_txt = datePickerTextView.getText().toString();

        }
    }

    @Override
    public void onBackPressed() {

    }

    public void CreateProjectDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Activity_Timesheets.this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_project, null);
        dialogBuilder.setView(dialogView);
        create_new_project = dialogView.findViewById(R.id.create_new_project);


        dialogBuilder.setTitle("Add Project");
        dialogBuilder.setCancelable(false);


        dialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                project_txt = create_new_project.getText().toString();
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
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(Activity_Timesheets.this);
        final ProgressDialog pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setMessage("Loading...");
//        pDialog.show();
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
                                    Toast.makeText(getApplicationContext(), "Information has been submitted successfully", Toast.LENGTH_SHORT).show();
//                                    redirect to activity list
                                    Intent intent = new Intent(getApplicationContext(), Activity_Show_Tasks.class);
                                    startActivity(intent);
                                    end_time.setText("");
                                    start_time.setText("");
                                    task_description.setText("");

                                } else {
                                    Toast.makeText(getApplicationContext(), "An Error Occurred", Toast.LENGTH_SHORT).show();


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

                Toast.makeText(getApplicationContext(), "Task could not be created", Toast.LENGTH_SHORT).show();


            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
//                params.put("email", email);
                params.put("date", date_txt);
                params.put("start_time", startTime_txt);
                params.put("end_time", endTime_txt);
                params.put("project_id", String.valueOf(id_project));
                params.put("name", task_txt);
                return params;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SessionManager sessionManager = new SessionManager(getApplicationContext());
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
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final ProgressDialog pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

//        pDialog.show();


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
                    project.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, Projects));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), "An Error Occurred" + error.getMessage(), Toast.LENGTH_LONG).show();

            }


        }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SessionManager sessionManager = new SessionManager(getApplicationContext());
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


        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);


    }

    private void createProject() {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final ProgressDialog pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setMessage("Loading...");
//        pDialog.show();
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
                                toast.makeText(getApplicationContext(), "Project has been created successfully", Toast.LENGTH_LONG).show();
//
//                                Toast.makeText(getApplicationContext(), "Project has been created successfully", Toast.LENGTH_SHORT).show();
                                /*redirect to activity list*/
                                Intent intent = new Intent(getApplicationContext(), Activity_Timesheets.class);
                                startActivity(intent);
                            } else {

                                toast.makeText(getApplicationContext(), "An Error Occurred", Toast.LENGTH_LONG).show();

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

                toast.makeText(getApplicationContext(), "Project could not be created", Toast.LENGTH_LONG).show();


            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", project_txt);


                return params;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SessionManager sessionManager = new SessionManager(getApplicationContext());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {

            session.logoutUser();
            finish();

//            return true;
        }
        if (id == R.id.show_tasks) {

            startActivity(new Intent(getApplicationContext(), Activity_Show_Tasks.class));
//            finish();

//            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
