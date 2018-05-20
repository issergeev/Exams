package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
    private static final String DATA_PREFS_NAME = "Data";

    private static SharedPreferences examsData;
    private static SharedPreferences.Editor editor;

    private Listener listener;

    RelativeLayout rootLayout;
    CardView examsView, resultsView;
    TextView fullName, patronymic, passedExams, examsToPass;
    FloatingActionButton logoutButton;

    AlertDialog.Builder alert, alert_email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        examsData = getSharedPreferences(DATA_PREFS_NAME, Context.MODE_PRIVATE);
        editor = examsData.edit();

        listener = new Listener();

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        examsView = (CardView) findViewById(R.id.exams_view);
        resultsView = (CardView) findViewById(R.id.results_view);
        examsToPass = (TextView) findViewById(R.id.exams_to_pass);
        passedExams = (TextView) findViewById(R.id.exams_passed);
        fullName = (TextView) findViewById(R.id.FaL_name);
        patronymic = (TextView) findViewById(R.id.patronymic);
        logoutButton = (FloatingActionButton) findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(listener);

        fullName.setText(examsData.getString("firstName", "First Name") + " " +
                examsData.getString("lastName", "Last Name"));
        patronymic.setText(examsData.getString("patronymic", "Patronymic"));
        examsToPass.setText(examsData.getString("ExamsToPass", getResources().getString(R.string.not_detected)));
        passedExams.setText(examsData.getString("PassedExams", getResources().getString(R.string.not_detected)));

        examsView.setOnClickListener(listener);
        resultsView.setOnClickListener(listener);

        if (examsData.getString("Email", "null").equals("null") && examsData.getBoolean("firstStart", true)) {
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
                    .setTitle(R.string.important_title)
                    .setMessage(R.string.email_message)
                    .setView(email)
                    .setPositiveButton(R.string.continue_text, new DialogInterface.OnClickListener() {
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
                                        .setTitle(R.string.warning_title_text)
                                        .setMessage(R.string.email_correct_message)
                                        .setView(email_new)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if (!email_new.getText().toString().trim().equals("")) {
                                                    new EmailSender().execute(email_new.getText().toString());
                                                } else {
                                                    Snackbar.make(rootLayout, R.string.email_not_added, Snackbar.LENGTH_LONG).show();
                                                }
                                            }
                                        })
                                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialogInterface) {
                                                Snackbar.make(rootLayout, R.string.email_not_added, Snackbar.LENGTH_LONG).show();
                                            }
                                        })
                                        .show();

                            } else {
                                new EmailSender().execute(email.getText().toString());
                            }
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Snackbar.make(rootLayout, R.string.email_not_added, Snackbar.LENGTH_LONG).show();
                        }
                    })
                    .show();

            editor.putBoolean("firstStart", false);
            editor.apply();
        }
    }

    private class Listener implements View.OnClickListener {
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
                            .setTitle(R.string.exit_text)
                            .setMessage(R.string.exit_message)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                    break;
                case R.id.exams_view :
                    startActivity(new Intent(HomeActivity.this, StudentsExamsActivity.class));
                    break;
                case R.id.results_view :
                    startActivity(new Intent(HomeActivity.this, ResultsActivity.class));
                    break;
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
                .setTitle(R.string.exit_text)
                .setMessage(R.string.exit_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @SuppressLint("StaticFieldLeak")
    class EmailSender extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(final String... strings) {
            final String LOGIN_URL = "http://exams-online.online/add_email.php";
            final RequestQueue request = Volley.newRequestQueue(HomeActivity.this);
            StringRequest query = new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    switch (response) {
                        case "Success" :
                            Snackbar.make(rootLayout, R.string.email_added, Snackbar.LENGTH_SHORT).show();
                            break;
                        case "Error sending email" :
                            Snackbar.make(rootLayout, R.string.emailError, Snackbar.LENGTH_LONG).show();
                        default :
                            Snackbar.make(rootLayout, R.string.unknown_response, Snackbar.LENGTH_LONG).show();
                            break;
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    int errorCode = 0;

                    try {
                        errorCode = error.networkResponse.statusCode;
                    } catch (NullPointerException e) {
                        Snackbar.make(rootLayout, R.string.unknown_error, Snackbar.LENGTH_LONG).show();
                    }

                    Log.i("net", error.getMessage());

                    switch (errorCode) {
                        case 302 :
                            Snackbar.make(rootLayout, R.string.hotspot_error, Snackbar.LENGTH_LONG).show();
                            break;
                        case 423 :
                            Snackbar.make(rootLayout, R.string.server_sleeping_text, Snackbar.LENGTH_LONG).show();
                            break;
                        default :
                            Snackbar.make(rootLayout, R.string.unknown_error, Snackbar.LENGTH_LONG).show();
                            break;
                    }
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> userData = new HashMap<>();

                    userData.put("studentID_number", examsData.getString("SIDNumber", "0"));
                    userData.put("student_email", strings[0]);

                    return userData;
                }
            };
            request.add(query);

            return null;
        }
    }
}