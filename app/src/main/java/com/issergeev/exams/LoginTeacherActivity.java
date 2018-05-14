package com.issergeev.exams;

import android.content.Context;
import android.content.SharedPreferences;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.HashMap;
import java.util.Map;

public class LoginTeacherActivity extends AppCompatActivity {
    private static final String DATA_PREFS_NAME = "Data";
    private String loginText, passwordText;

    private static SharedPreferences examsData;
    private static SharedPreferences.Editor editor;

    RelativeLayout rootLayout;
    EditText login, password;
    Button loginButton;
    ProgressBar progressBar;

    AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_teacher);

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
                loginButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                loginText = login.getText().toString();
                passwordText = password.getText().toString();

                new SignInChecker().execute();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class SignInChecker extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final String APPENDIX_URL = "http://exams-online.online/get_appendix_teacher.php";
            final RequestQueue request = Volley.newRequestQueue(LoginTeacherActivity.this);
            StringRequest query = new StringRequest(Request.Method.POST, APPENDIX_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(final String response) {
                    loginButton.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

                    startActivity(new Intent(LoginTeacherActivity.this, TeacherHomeSplashActivity.class)
                            .putExtra("Login", loginText)
                            .putExtra("Password", passwordText)
                            .putExtra("Salt", response));
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
                            alert = new AlertDialog.Builder(LoginTeacherActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            alert = new AlertDialog.Builder(LoginTeacherActivity.this);
                        }
                        alert.setCancelable(true)
                                .setTitle(R.string.warning_title_text)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setMessage(R.string.connection_error_text)
                                .setPositiveButton(R.string.accept_text, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        loginButton.setEnabled(true);
                                    }
                                }).show();
                    }

                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> userData = new HashMap<>();

                    loginText = login.getText().toString();

                    userData.put("student_verif", loginText);

                    return userData;
                }
            };
            request.add(query);

            return null;
        }
    }
}