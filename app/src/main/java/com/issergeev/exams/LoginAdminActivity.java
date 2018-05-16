package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginAdminActivity extends AppCompatActivity {
    private static final String DATA_PREFS_NAME = "Data";

    private static SharedPreferences examsData;
    private static SharedPreferences.Editor editor;

    private String firstName, lastName, patronymic;
    private String loginText, passwordText, salt;

    RelativeLayout rootLayout;
    EditText login, password;
    Button loginButton;
    ProgressBar progressBar;

    AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        examsData = getSharedPreferences(DATA_PREFS_NAME, Context.MODE_PRIVATE);
        editor = examsData.edit();

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.loginButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SignInChecker().execute();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class SignInChecker extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final String APPENDIX_URL = "http://exams-online.online/get_appendix_admin.php";
            final RequestQueue request = Volley.newRequestQueue(LoginAdminActivity.this);
            StringRequest query = new StringRequest(Request.Method.POST, APPENDIX_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(final String response) {
                    if (response.equals("")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            alert = new AlertDialog.Builder(LoginAdminActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            alert = new AlertDialog.Builder(LoginAdminActivity.this);
                        }
                        alert.setCancelable(false)
                                .setTitle(R.string.warning_title_text)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setMessage(R.string.incorrect_identity_data)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        loginButton.setEnabled(true);
                                        progressBar.setVisibility(View.GONE);
                                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                                    }
                                })
                                .show();
                    } else {
                        loginButton.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

                        loginText = login.getText().toString();
                        passwordText = password.getText().toString();

                        salt = response;

                        new SignIn().execute();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse (VolleyError error){
                    if (error.networkResponse != null) {
                        loginButton.setEnabled(true);

                        int errorCode = error.networkResponse.statusCode;

                        switch (errorCode) {
                            case 302 :
                                Snackbar.make(rootLayout, R.string.hotspot_error, Snackbar.LENGTH_SHORT).show();
                                break;
                            case 423 :
                                Snackbar.make(rootLayout, R.string.server_sleeping_text, Snackbar.LENGTH_SHORT).show();
                                break;
                            default :
                                Snackbar.make(rootLayout, R.string.unknown_error, Snackbar.LENGTH_SHORT).show();
                                break;
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            alert = new AlertDialog.Builder(LoginAdminActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            alert = new AlertDialog.Builder(LoginAdminActivity.this);
                        }
                        alert.setCancelable(true)
                                .setTitle(R.string.warning_title_text)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setMessage(R.string.connection_error_text)
                                .setPositiveButton(R.string.accept_text, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).show();
                    }

                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> data = new HashMap<>();

                    loginText = login.getText().toString();

                    data.put("admin_verif", loginText);

                    return data;
                }
            };
            request.add(query);

            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class SignIn extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final String LOGIN_URL = "http://exams-online.online/sign_in_admin.php";
            final RequestQueue request = Volley.newRequestQueue(LoginAdminActivity.this);
            StringRequest query = new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

                    if (!response.equals("null")) {

                        try {
                            JSONObject serverResponse = new JSONObject(response);
                            JSONArray user = serverResponse.getJSONArray("Admin");
                            JSONObject data = user.getJSONObject(0);

                            firstName = data.getString("first_name");
                            lastName = data.getString("last_name");
                            patronymic = data.getString("patronymic");

                            editor.putString("FirstName", firstName);
                            editor.putString("LastName", lastName);
                            editor.putString("Patronymic", patronymic);
                            editor.commit();

                            //finish();
                            //startActivity(new Intent(LoginAdminActivity.this, HomeActivity.class));
                        } catch (JSONException e) {
                            Snackbar.make(rootLayout, R.string.unknown_response, Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            alert = new AlertDialog.Builder(LoginAdminActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            alert = new AlertDialog.Builder(LoginAdminActivity.this);
                        }
                        alert.setCancelable(true)
                                .setTitle(R.string.warning_title_text)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setMessage(R.string.incorrect_identity_data)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Snackbar.make(rootLayout, R.string.unknown_response, Snackbar.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> adminData = new HashMap<>();

                    adminData.put("admin_verif", loginText);
                    adminData.put("admin_auth", Encryption.EncryptWithSalt(passwordText, salt));

                    return adminData;
                }
            };
            request.add(query);

            return null;
        }
    }
}