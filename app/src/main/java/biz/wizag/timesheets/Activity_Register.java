package biz.wizag.timesheets;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Activity_Register extends AppCompatActivity {
    ProgressDialog progressDialog;
    EditText edit_firstname, edit_lastname, edit_id, edit_phone, email_address, create_password, confirm_password;
    private static final String SHARED_PREF_NAME = "token_name";
    Button button_register;
    String RegisterUrl = "http://timesheets.wizag.biz/api/register";

    TextView text_dummy_hint_first_name, text_dummy_hint_last_name,

    text_dummy_hint_email_address, text_dummy_hint_create_password,
            text_dummy_hint_confirm_password;


    String f_name, l_name,
            email_address_txt, create_password_txt, confirm_password_txt;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = new ProgressDialog(this);


        edit_firstname = findViewById(R.id.edit_firstname);
        edit_lastname = findViewById(R.id.edit_lastname);

        email_address = findViewById(R.id.email_address);
        create_password = findViewById(R.id.create_password);
        confirm_password = findViewById(R.id.confirm_password);
        button_register = findViewById(R.id.button_register);
        text_dummy_hint_first_name = findViewById(R.id.text_dummy_hint_first_name);
        text_dummy_hint_last_name = findViewById(R.id.text_dummy_hint_last_name);

        text_dummy_hint_email_address = findViewById(R.id.text_dummy_hint_email_address);
        //handling editexts hints on focus changed
        // first name
        edit_firstname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // Show white background behind floating label
                            text_dummy_hint_first_name.setVisibility(View.VISIBLE);
                        }
                    }, 100);
                } else {
                    // Required to show/hide white background behind floating label during focus change
                    if (edit_firstname.getText().length() > 0)
                        text_dummy_hint_first_name.setVisibility(View.VISIBLE);
                    else
                        text_dummy_hint_first_name.setVisibility(View.INVISIBLE);
                }
            }
        });

        edit_lastname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // Show white background behind floating label
                            text_dummy_hint_last_name.setVisibility(View.VISIBLE);
                        }
                    }, 100);
                } else {
                    // Required to show/hide white background behind floating label during focus change
                    if (edit_lastname.getText().length() > 0)
                        text_dummy_hint_last_name.setVisibility(View.VISIBLE);
                    else
                        text_dummy_hint_last_name.setVisibility(View.INVISIBLE);
                }
            }
        });


        email_address.setOnFocusChangeListener((v, hasFocus) -> {

            if (hasFocus) {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // Show white background behind floating label
                        text_dummy_hint_email_address.setVisibility(View.VISIBLE);
                    }
                }, 100);
            } else {
                // Required to show/hide white background behind floating label during focus change
                if (email_address.getText().length() > 0)
                    text_dummy_hint_email_address.setVisibility(View.VISIBLE);
                else
                    text_dummy_hint_email_address.setVisibility(View.INVISIBLE);
            }
        });


        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*store reg details on shared prefs*/
                SharedPreferences sp = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();

                f_name = edit_firstname.getText().toString();
                l_name = edit_lastname.getText().toString();
                email_address_txt = email_address.getText().toString();
                create_password_txt = create_password.getText().toString();
                confirm_password_txt = confirm_password.getText().toString();


                if (TextUtils.isEmpty(f_name)) {
                    edit_firstname.setError("Please enter first name");
                    edit_firstname.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(l_name)) {
                    edit_lastname.setError("Please enter last name");
                    edit_lastname.requestFocus();
                    return;
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email_address_txt).matches()) {
                    email_address.setError("Enter a valid email");
                    email_address.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(create_password_txt)) {
                    create_password.setError("Enter a password");
                    create_password.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(confirm_password_txt)) {
                    confirm_password.setError("Confirm password");
                    confirm_password.requestFocus();
                    return;
                } else if (!create_password_txt.equals(confirm_password_txt)) {
                    confirm_password.setError("Both passwords should match");
                    confirm_password.requestFocus();
                    return;
                }
                /*else if(id_image.getDrawable() == null){
                    Toast.makeText(Activity_Register.this, "", Toast.LENGTH_SHORT).show();
                }*/

                else if (!isNetworkConnected()) {

                    Toast.makeText(Activity_Register.this, "Ensure you have internet connection", Toast.LENGTH_SHORT).show();

                } else {
                    registerUser();

                }


            }
        });


    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    private void registerUser() {
        name = f_name + "" + l_name;
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing in...");
        progressDialog.show();
        //getText

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RegisterUrl, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();

                try {
                    //converting response to json object

                    JSONObject obj = new JSONObject(response);

                    if (obj.has("success")) {
                        /*get access token*/
                        JSONObject success = obj.getJSONObject("success");
                        String token = success.getString("token");

                        /*store token and name*/
                        SharedPreferences sp = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("token", token);
                        editor.putString("name", name);
                        editor.apply();


                        Toast.makeText(Activity_Register.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), Activity_Login.class));
                        finish();

                    } else {
                        Toast.makeText(Activity_Register.this, "An error occurred during registration", Toast.LENGTH_SHORT).show();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        error.getMessage();
                        Toast.makeText(Activity_Register.this, "An error occurred" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }


        ) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email_address_txt);
                params.put("password", create_password_txt);
                params.put("c_password", confirm_password_txt);


//                params.put("role_id", "2");
                return params;
            }


        };

        MySingleton.getInstance(this).addToRequestQueue(stringRequest);


    }


}
