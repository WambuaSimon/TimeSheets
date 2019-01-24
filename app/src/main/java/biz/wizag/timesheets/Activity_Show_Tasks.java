package biz.wizag.timesheets;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import biz.wizag.Adapters.Adapter_Show_Tasks;
import biz.wizag.Models.Model_Show_Tasks;
import biz.wizag.SessionManager;


public class Activity_Show_Tasks extends AppCompatActivity {
    String message;

    Adapter_Show_Tasks adapter_show_tasks;
    TextView date, startTime, endTime, project, task;
    RecyclerView recycler_view;
    private RecyclerView.Adapter adapter;
    private List<Model_Show_Tasks> tasks;
    private static final String getTasks = "http://timesheets.wizag.biz/api/tasks";
    String task_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tasks);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //initializing the recycler view
        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        tasks = new ArrayList<>();


        //initializing adapter
        adapter_show_tasks = new Adapter_Show_Tasks(tasks, this);
        recycler_view.setAdapter(adapter_show_tasks);


        //  loadUrlData();
        loadTasks();


        recycler_view.addOnItemTouchListener(new RecyclerTouchListener(this, recycler_view, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (tasks.size() > position) {
                    if (tasks.get(position) != null) {
                        Intent intent = new Intent(getApplicationContext(), Activity_Task.class);
                        intent.putExtra("task", tasks.get(position).getTask());
                        intent.putExtra("task_id", task_id);
                        startActivity(intent);
                        finish();
                    }
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void loadTasks() {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(Activity_Show_Tasks.this);
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getTasks,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        try {

//                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = new JSONArray(response);
                            for (int p = 0; p < jsonArray.length(); p++) {


                                Model_Show_Tasks model_tasks = new Model_Show_Tasks();
                                JSONObject task_jobs = jsonArray.getJSONObject(p);


//                                    String email = task_jobs.getString("name");
                                task_id = task_jobs.getString("id");
                                String date = task_jobs.getString("date");
                                String start_time = task_jobs.getString("start_time");
                                String end_time = task_jobs.getString("end_time");
                                String project = task_jobs.getString("project_name");
                                String task = task_jobs.getString("name");


                                model_tasks.setDate(date);
                                model_tasks.setStart_time(start_time);
                                model_tasks.setEnd_time(end_time);
                                model_tasks.setProject(project);
                                model_tasks.setTask(task);


                                if (tasks.contains(date)) {
                                    /*do nothing*/
                                } else {
                                    tasks.add(model_tasks);
                                }


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            e.getMessage();
                        }
                        adapter_show_tasks.notifyDataSetChanged();

                        //Toast.makeText(Activity_Show_Tasks.this, "", Toast.LENGTH_SHORT).show();
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getMessage();
                error.printStackTrace();

                Toast.makeText(getApplicationContext(), "Data could not be loaded", Snackbar.LENGTH_LONG).show();
                pDialog.dismiss();
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
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


}