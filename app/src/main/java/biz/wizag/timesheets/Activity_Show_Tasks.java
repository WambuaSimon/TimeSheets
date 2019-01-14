package biz.wizag.timesheets;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tasks);

        //initializing the recycler view
        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        tasks = new ArrayList<>();

        //initializing adapter
        adapter_show_tasks = new Adapter_Show_Tasks(tasks, this);
        recycler_view.setAdapter(adapter_show_tasks);


        //  loadUrlData();
        loadTransactions();
    }

    private void loadTransactions() {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(Activity_Show_Tasks.this);
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getTasks,
                response -> {
                    pDialog.dismiss();
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject == null)
                        {
                            Toast.makeText(getApplicationContext(),"Object null",Toast.LENGTH_LONG).show();
                        } else if (jsonObject != null)
                        {
                            Toast.makeText(getApplicationContext(),"Object not null",Toast.LENGTH_LONG).show();
                        }
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int p = 0; p < data.length(); p++) {
                            Model_Show_Tasks model_tasks = new Model_Show_Tasks();
                            JSONObject task_jobs = data.getJSONObject(p);


                                String email = task_jobs.getString("email");
                                String date = task_jobs.getString("date");
                                String start_time = task_jobs.getString("start_time");
                                String end_time = task_jobs.getString("end_time");
                                String project = task_jobs.getString("project");
                                String task = task_jobs.getString("task");

                                model_tasks.setEmail(email);
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
                    }
                    adapter_show_tasks.notifyDataSetChanged();

                    //Toast.makeText(Activity_Show_Tasks.this, "", Toast.LENGTH_SHORT).show();
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getMessage();
                error.printStackTrace();

                Toast.makeText(getApplicationContext(), "Data could not be loaded", Snackbar.LENGTH_LONG).show();
                pDialog.dismiss();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                //params.put("code", "blst786");
                //  params.put("")
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
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.layout_show_tasks, parent, false));
            itemView.setOnClickListener(v -> {
                Context context = v.getContext();
                Intent intent = new Intent(context, Activity_Task.class);
                context.startActivity(intent);
            });
        }
    }

}