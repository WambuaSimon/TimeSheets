package biz.wizag.timesheets;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import biz.wizag.SessionManager;

public class Activity_Task extends AppCompatActivity {
    TextView task;
    String task_data;
    String task_id;
    String task_id_txt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.task);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        task = findViewById(R.id.task);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
// get data via the key
        task_id_txt = extras.getString("task_id");
        String value1 = extras.getString("task");
        if (value1 != null) {
            task.setText(value1);

            // do something with the data
        }
    }

    public void Delete(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to do this?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteTask();

                    }
                })
                .setNegativeButton("No", null)
                .show();


    }

    private void deleteTask() {
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(Activity_Task.this);
        final ProgressDialog pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setMessage("Loading...");
//        pDialog.show();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, "http://timesheets.wizag.biz/api/tasks/" + task_id_txt,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        try {

                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject != null) {
                                String success = jsonObject.getString("success");
                                if (success.equalsIgnoreCase("true")) {
                                    Toast.makeText(getApplicationContext(), "Task has been deleted successfully", Toast.LENGTH_SHORT).show();
//                                    redirect to activity list
                                    Intent intent = new Intent(getApplicationContext(), Activity_Show_Tasks.class);
                                    startActivity(intent);
                                    finish();


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

                Toast.makeText(getApplicationContext(), "Task could not be deleted", Toast.LENGTH_SHORT).show();


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

    @Override
    public void onBackPressed() {

    }


}
