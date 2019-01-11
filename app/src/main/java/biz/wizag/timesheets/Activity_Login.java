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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;

import java.util.HashMap;
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
    //String username = "admin@cosand.com";
    String username, password;
    Button timesheet_login;
    CoordinatorLayout coordinatorLayout;
    EditText enter_username;
    TextInputEditText enter_password;
    SharedPreferences prefs;
    SessionManager session;
    private static final String SHARED_PREF_NAME = "profile";
    String get_profile_url = "http://timesheets.wizag.biz/api/login";
    String token;
    JSONArray role_array;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        enter_username = findViewById(R.id.email);
        enter_password =(TextInputEditText) findViewById(R.id.enter_password);

        // Session Manager
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        token = user.get("access_token");


        if (session.isLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), Activity_Home.class));
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
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing in...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface service = retrofit.create(ApiInterface.class);
        // Call<AuthUser> call = service.loginUser("admin@cosand.com", "Qwerty123!","password", "2", "GEf81B8TnpPDibW4NKygaatvBG3RmbYSaJf8SZTA");
        Call<AuthUser> call = service.loginUser(username, password, "password", "2", "GEf81B8TnpPDibW4NKygaatvBG3RmbYSaJf8SZTA");
        call.enqueue(new Callback<AuthUser>() {
            @Override
            public void onResponse(Call<AuthUser> call, Response<AuthUser> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        AuthUser authUser = response.body();
                        access_token = authUser.getAccessToken();
                        refresh_token = authUser.getRefreshToken();
                        token_type = authUser.getTokenType();


                        prefs.edit().putBoolean("oauth.loggedin", true).apply();
                        prefs.edit().putString(ACCESS_TOKEN, access_token).apply();
                        prefs.edit().putString(REFRESH_TOKEN, refresh_token).apply();
                        prefs.edit().putString(TOKEN_TYPE, token_type).apply();

                        //  String token = prefs.getString("access_token", ACCESS_TOKEN);

                        session.createLoginSession(username, password, access_token);

                        SharedPreferences sp = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("password", password);
                        editor.apply();


//                        getIndividualProfile();
                       /* getDriverProfile();
                        getCorporateProfile();
                        getTruckOwner();
                        getSupplierProfile();*/
                        Intent intent=new Intent(getApplicationContext(), Activity_Home.class);
                        intent.putExtra("USERNAME", username);
                        startActivity(intent);
                        finish();

                    }

                } else if (response.code() >= 400 && response.code() < 599) {
                    Snackbar snackbar = Snackbar
                            // .make(coordinatorLayout, ""+response.code(), Snackbar.LENGTH_INDEFINITE)
                            .make(coordinatorLayout, "Wrong username or password", Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    loginUser();
//                                    getDriverProfile();
                                }
                            });

                    snackbar.show();
                } else {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Something went wrong", Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    loginUser();
//                                    getDriverProfile();
                                }
                            });

                    snackbar.show();
                }

            }

            @Override
            public void onFailure(Call<AuthUser> call, Throwable t) {
                progressDialog.dismiss();
                t.printStackTrace();
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, " " + fetchErrorMessage(t), Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                loginUser();
                            }
                        });

                snackbar.show();

            }
        });
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

