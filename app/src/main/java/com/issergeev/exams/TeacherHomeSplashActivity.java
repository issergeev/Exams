package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class TeacherHomeSplashActivity extends AppCompatActivity {
    private SharedPreferences.Editor editor = WelcomeActivity.editor;
    private String firstName, lastName, patronymic;
    private int teacherIDNumber;

    String loginText, passwordText, salt;

    RelativeLayout rootLayout;

    AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home_splash);

        Intent intent = getIntent();
        loginText = intent.getStringExtra("Login");
        passwordText = intent.getStringExtra("Password");
        salt = intent.getStringExtra("Salt");

        Log.i("net", salt);
        if (salt == null) {
            salt = "0000000000";
        }

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        new SignIn().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class SignIn extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final String loginURL = "http://exams-online.online/sign_in_teacher.php";
            final RequestQueue request = Volley.newRequestQueue(TeacherHomeSplashActivity.this);
            StringRequest query = new StringRequest(Request.Method.POST, loginURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

                    if (!response.equals("null")) {
                        try {
                            JSONObject serverResponse = new JSONObject(response);
                            JSONArray user = serverResponse.getJSONArray("Teacher");
                            JSONObject data = user.getJSONObject(0);

                            teacherIDNumber = data.getInt("teacher_id");
                            firstName = data.getString("teacher_firstName");
                            lastName = data.getString("teacher_lastName");
                            patronymic = data.getString("teacher_patronymic");

                            editor.putString("firstName", firstName);
                            editor.putString("lastName", lastName);
                            editor.putString("patronymic", patronymic);
                            editor.putInt("SIDNumber", teacherIDNumber);
                            editor.commit();

                            finish();
                            startActivity(new Intent(TeacherHomeSplashActivity.this, TeacherHomeActivity.class));
                        } catch (JSONException e) {
                            Log.d("login", e.getMessage());
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            alert = new AlertDialog.Builder(TeacherHomeSplashActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            alert = new AlertDialog.Builder(TeacherHomeSplashActivity.this);
                        }
                        alert.setCancelable(true)
                                .setTitle(R.string.warningTitleText)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setMessage(R.string.userNotFound)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                finish();
                            }
                        }).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    int errorCode = error.networkResponse.statusCode;

                    switch (errorCode) {
                        case 302 :
                            Snackbar.make(rootLayout, R.string.hotspotError, Snackbar.LENGTH_SHORT).show();
                            break;
                        case 423 :
                            Snackbar.make(rootLayout, R.string.serverSleepingText, Snackbar.LENGTH_SHORT).show();
                            break;
                        default :
                            Snackbar.make(rootLayout, R.string.unknownError, Snackbar.LENGTH_SHORT).show();
                            break;
                    }
                    Log.d("login", error.getMessage());
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> userData = new HashMap<>();

                    userData.put("student_verif", loginText);
                    userData.put("student_auth", Encryption.EncryptWithSalt(passwordText, salt));

                    return userData;
                }
            };
            request.add(query);
            return null;
        }
    }
}