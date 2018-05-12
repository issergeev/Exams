package com.issergeev.exams;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    public static final String DATA_PREFS_NAME = "Data";

    public static SharedPreferences examsData;
    public static SharedPreferences.Editor editor;

    Listener listener;

    RelativeLayout rootLayout;
    AlertDialog.Builder alert, alert_email;
    FloatingActionButton logoutButton;
    TextView fullName, patronymic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        examsData = getSharedPreferences(DATA_PREFS_NAME, Context.MODE_PRIVATE);
        editor = examsData.edit();

        listener = new Listener();

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        logoutButton = (FloatingActionButton) findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(listener);

        fullName = (TextView) findViewById(R.id.FaLName);
        patronymic = (TextView) findViewById(R.id.patronymic);
        fullName.setText(examsData.getString("firstName", "First Name") + " " +
                examsData.getString("lastName", "Last Name"));
        patronymic.setText(examsData.getString("patronymic", "Patronymic"));

        if (examsData.getBoolean("isFirstStart", true)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alert = new AlertDialog.Builder(HomeActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                alert = new AlertDialog.Builder(HomeActivity.this);
            }
            final EditText email = new EditText(HomeActivity.this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            email.setLayoutParams(params);
            email.setTextColor(getResources().getColor(R.color.colorMain));
            alert.setCancelable(false)
                    .setTitle(R.string.importantTitle)
                    .setMessage(R.string.emailMessage)
                    .setView(email)
                    .setPositiveButton(R.string.continueText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final String emailText = email.getText().toString();
                            if (emailText.trim().length() == 0 || !emailText.contains("@") ||
                                    !emailText.contains(".") || emailText.startsWith("@") || emailText.endsWith("@") ||
                                    emailText.startsWith(".") || emailText.endsWith(".")) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    alert_email = new AlertDialog.Builder(HomeActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                                } else {
                                    alert_email = new AlertDialog.Builder(HomeActivity.this);
                                }
                                final EditText email_new = new EditText(HomeActivity.this);
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                                email_new.setLayoutParams(params);
                                email_new.setTextColor(getResources().getColor(R.color.colorMain));
                                email_new.setText(emailText);

                                alert_email.setCancelable(true)
                                        .setTitle(R.string.warningTitleText)
                                        .setMessage(R.string.emailCorrectMessage)
                                        .setView(email_new)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                new EmailSender().execute(email_new.getText().toString());
                                                editor.putBoolean("isFirstStart", false);
                                                editor.commit();
                                            }
                                        })
                                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialogInterface) {
                                                new EmailSender().execute(email_new.getText().toString());
                                                editor.putBoolean("isFirstStart", false);
                                                editor.commit();
                                            }
                                        }).show();
                            } else {
                                new EmailSender().execute(email.getText().toString());
                                editor.putBoolean("isFirstStart", false);
                                editor.commit();
                            }
                        }
                    })
                    .show();
        }
    }

    class Listener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.logoutButton :
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        alert = new AlertDialog.Builder(HomeActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        alert = new AlertDialog.Builder(HomeActivity.this);
                    }
                    alert.setCancelable(true)
                            .setTitle(R.string.exitText)
                            .setMessage(R.string.exitMessage)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alert = new AlertDialog.Builder(HomeActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            alert = new AlertDialog.Builder(HomeActivity.this);
        }
        alert.setCancelable(true)
                .setTitle(R.string.exitText)
                .setMessage(R.string.exitMessage)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    class EmailSender extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(final String... strings) {
            final String loginURL = "http://exams-online.online/add_email.php";
            final RequestQueue request = Volley.newRequestQueue(HomeActivity.this);
            StringRequest query = new StringRequest(Request.Method.POST, loginURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    switch (response) {
                        case "Success" :
                            Snackbar.make(rootLayout, R.string.emailAdded, Snackbar.LENGTH_SHORT).show();
                            break;
                        default :
                            Snackbar.make(rootLayout, R.string.unknownError, Snackbar.LENGTH_LONG).show();
                            Log.d("net", response);
                            break;
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    int errorCode = error.networkResponse.statusCode;

                    switch (errorCode) {
                        case 302 :
                            Snackbar.make(rootLayout, R.string.hotspotError, Snackbar.LENGTH_LONG).show();
                            break;
                        case 423 :
                            Snackbar.make(rootLayout, R.string.serverSleepingText, Snackbar.LENGTH_LONG).show();
                            break;
                        default :
                            Snackbar.make(rootLayout, R.string.unknownError, Snackbar.LENGTH_LONG).show();
                            break;
                    }
                    Log.d("login", error.getMessage());
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> userData = new HashMap<>();

                    userData.put("studentID_number", String.valueOf(examsData.getInt("SIDNumber", 0)));
                    userData.put("student_email", strings[0]);

                    return userData;
                }
            };
            request.add(query);

            return null;
        }
    }
}