package biz.wizag.timesheets;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import biz.wizag.Adapters.Adapter_Show_Tasks;
import biz.wizag.Models.Model_Show_Tasks;


public class Activity_Show_Tasks extends AppCompatActivity {
    AlertDialog alertDialog = null;
    EditText otp;
    String message;

    Adapter_Show_Tasks adapter_show_tasks;
    TextView date, startTime, endTime,project,task;
    RecyclerView recycler_view;
    private RecyclerView.Adapter adapter;
    private List<Model_Show_Tasks> tasks;
    private static final String URL_DATA = "http://sduka.wizag.biz/api/v1/orders/";
    String order_id;

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

        date = findViewById(R.id.date);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        project = findViewById(R.id.project);
        task = findViewById(R.id.task);
      //  loadUrlData();

    }

//    private void loadUrlData() {
//        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//        final ProgressDialog pDialog = new ProgressDialog(this);
//        pDialog.setMessage("Loading...");
//        pDialog.setCancelable(false);
//        pDialog.show();
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://sduka.wizag.biz/api/v1/orders/", new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//
//                    JSONObject jsonObject = new JSONObject(response);
//                    pDialog.dismiss();
//                    if (jsonObject != null) {
//                        JSONObject data = jsonObject.getJSONObject("data");
//                        JSONArray orders = data.getJSONArray("orders");
//                        for (int k = 0; k < orders.length(); k++) {
//                            Model_Show_Tasks model_show_tasks = new Model_Show_Tasks();
//                            JSONObject ordersObject = orders.getJSONObject(k);
//
//
//                            order_id = ordersObject.getString("order_id");
//                            String date = ordersObject.getString("material_type");
//                            String start_time = ordersObject.getString("material_item");
//                            String end_time = ordersObject.getString("material_detail");
//                            String project = ordersObject.getString("material_class");
//                            String task = ordersObject.getString("material_quantity");
//                            String material_cost = ordersObject.getString("quote");
//                            String order_status = ordersObject.getString("order_status");
//
////                        JSONObject site = ordersObject.getJSONObject("site");
////                        String name = site.getString("name");
//                            model_show_tasks.setDate(date);
//                            model_show_tasks.setStart_time(start_time);
//                            model_show_tasks.setEnd_time(end_time);
//                            model_show_tasks.setProject(project);
//                            model_show_tasks.setTask(task);
//
//
//
//                            if (orderList.contains(order_id)) {
//                                /*do nothing*/
//                            } else {
//                                orderList.add(model_orders);
//                            }
//                        }
//
//
//                    }
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                adapter_view_orders.notifyDataSetChanged();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                pDialog.dismiss();
//                Toast.makeText(getApplicationContext(), "An Error Occurred" + error.getMessage(), Toast.LENGTH_LONG).show();
//
//            }
//
//
//        }) {
//
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                SessionManager sessionManager = new SessionManager(getApplicationContext());
//                HashMap<String, String> user = sessionManager.getUserDetails();
//                String accessToken = user.get("access_token");
//
//                String bearer = "Bearer " + accessToken;
//                Map<String, String> headersSys = super.getHeaders();
//                Map<String, String> headers = new HashMap<String, String>();
//                headersSys.remove("Authorization");
//                headers.put("Authorization", bearer);
//                headers.putAll(headersSys);
//                return headers;
//            }
//        };
//
//
//        //MySingleton.getInstance(this).addToRequestQueue(stringRequest);
//
//
//        int socketTimeout = 30000;
//        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        stringRequest.setRetryPolicy(policy);
//        requestQueue.add(stringRequest);
//
//
//    }


}