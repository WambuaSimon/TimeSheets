package biz.wizag.timesheets;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import biz.wizag.APIClient;
import biz.wizag.ApiInterface;
import biz.wizag.AuthUser;
import biz.wizag.SessionManager;
import biz.wizag.Validation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Login extends AppCompatActivity {
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String TOKEN_TYPE = "tokenType";
    String access_token, refresh_token, token_type;
    String username, password;
    Button timesheet_login, supaduka_signup;
    CoordinatorLayout coordinatorLayout;
    EditText enter_username;
    TextInputEditText enter_password;
    SharedPreferences prefs;
    SessionManager session;
    SharedPreferences sp;
    String token_name, name;
    String LoginUser = "http://timesheets.wizag.biz/api/login";
    String login_token;
    private static final String SHARED_PREF_NAME = "profile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        prefs = this.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);


        enter_username = findViewById(R.id.email);
        enter_password = (TextInputEditText) findViewById(R.id.enter_password);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        supaduka_signup = findViewById(R.id.supaduka_signup);
        supaduka_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Login.this, Activity_Register.class);
                startActivity(intent);
//                finish();
            }
        });
        // Session Manager
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
//        token_name = user.get("access_token");


        if (session.isLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), Activity_Timesheets.class));
            finish();
        }

        timesheet_login = findViewById(R.id.button_login);
        timesheet_login.setOnClickListener(v -> {


            /** Validation class will check the error and display the error on respective fields
             but it won't resist the form submission, so we need to check again before submit
             */

            if (loginValidation()) {
                username = enter_username.getText().toString();
                password = enter_password.getText().toString();

                if (!isNetworkConnected()) {
                    Toast.makeText(this, "Ensure you are have internet connection", Toast.LENGTH_LONG).show();
                }
                loginUser();

            } else {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Form contains errors", Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });

                snackbar.show();

            }
        });

    }

    private boolean loginValidation() {
        boolean ret = true;

        if (!Validation.hasText(enter_password)) ret = false;
        if (!Validation.hasText(enter_username)) ret = false;
        return ret;
    }

    private void loginUser() {

        com.android.volley.RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LoginUser,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("success")) {

                                /*get token*/
                                JSONObject success = jsonObject.getJSONObject("success");
                                login_token = success.getString("token");
                                Log.i("login_token_log", login_token);
                                /*store in sharedprefs*/

                                session.createLoginSession(username, password, login_token);

                                Toast.makeText(getApplicationContext(), "User Logged in successfully", Toast.LENGTH_LONG).show();

                                SharedPreferences sp = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("email", username);
                                editor.apply();

                                Intent intent = new Intent(getBaseContext(), Activity_Timesheets.class);

                                startActivity(intent);
                                finish();


                            } else {
                                Toast.makeText(Activity_Login.this, "User Could not be logged in", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Toast.makeText(Activity_Sell.this, "", Toast.LENGTH_SHORT).show();
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.getMessage();
                Toast.makeText(getApplicationContext(), "Request could not be placed", Toast.LENGTH_LONG).show();
                pDialog.dismiss();
            }
        }) {
            //adding parameters to the request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", username);
                params.put("password", password);

                return params;
            }


        };
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnected()) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}

