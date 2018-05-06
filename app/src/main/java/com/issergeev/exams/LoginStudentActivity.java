package com.issergeev.exams;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

public class LoginStudentActivity extends AppCompatActivity {
    public static final String DATA_PREFS_NAME = "Data";

    static String[] passData = new String[2];

    private String loginText = "", passwordText = "";
    private String firstName, lastName, patronymic, groupNumber;
    private int studentIDNumber;

    Listener listener;

    AlertDialog.Builder alert;

    RelativeLayout rootLayout;
    ProgressBar progressBar;
    Button createButton, loginButton;
    EditText login;
    TextInputEditText password;

    InputMethodManager inputMethodManager;
    View view;

    public static SharedPreferences examsData;
    public static SharedPreferences.Editor editor;

    @Override
    protected void onResume() {
        super.onResume();

        loginText = examsData.getString("Login", "");
        passwordText = examsData.getString("Password", "");
        login.setText(loginText);
        password.setText(passwordText);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_student);

        listener = new Listener();

        examsData = getApplicationContext().getSharedPreferences(DATA_PREFS_NAME, Context.MODE_PRIVATE);
        editor = examsData.edit();

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(listener);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(listener);
        login = (EditText) findViewById(R.id.login);
        password = (TextInputEditText) findViewById(R.id.password);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        view = this.getCurrentFocus();
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    class Listener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loginButton :
                    loginButton.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    lockScreenOrientation();

                    loginText = login.getText().toString();
                    passwordText = password.getText().toString();

                    new SignInChecker().execute();
                    break;
                case R.id.createButton :
                    startActivity(new Intent(getApplicationContext(), RegActivity.class));
                    overridePendingTransition(R.anim.activity_appear_anim, R.anim.activity_disappear_anim);
                    break;
            }
        }
    }

    private void lockScreenOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();

        switch (rotation) {
            case Surface.ROTATION_0 :
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case Surface.ROTATION_90 :
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
            case Surface.ROTATION_180 :
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            case Surface.ROTATION_270 :
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        loginText = login.getText().toString();
        passwordText = password.getText().toString();

        editor.putString("Login", loginText);
        editor.putString("Password", passwordText);
        editor.putInt("progressBarVisibility", View.GONE);
        editor.apply();
    }

    class SignInChecker extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
                final String appendixURL = "http://exams-online.000webhostapp.com/get_appendix.php";
                final RequestQueue request = Volley.newRequestQueue(LoginStudentActivity.this);
                StringRequest query = new StringRequest(Request.Method.POST, appendixURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        if (response.equals("")) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                alert = new AlertDialog.Builder(LoginStudentActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                alert = new AlertDialog.Builder(LoginStudentActivity.this);
                            }
                            alert.setCancelable(false)
                                    .setTitle(R.string.warningTitleText)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setMessage(R.string.userNotFound)
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

                            startActivity(new Intent(LoginStudentActivity.this, HomeSplashActivity.class)
                                    .putExtra("Login", loginText)
                                    .putExtra("Password", passwordText)
                                    .putExtra("Salt", response));
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse (VolleyError error){
                        if (error.networkResponse != null) {
                            loginButton.setEnabled(true);

                            int errorCode = error.networkResponse.statusCode;

                            if (errorCode == 423) {
                                Snackbar.make(rootLayout, R.string.serverSleepingText, Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(rootLayout, R.string.unknownErrorText, Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                alert = new AlertDialog.Builder(LoginStudentActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                alert = new AlertDialog.Builder(LoginStudentActivity.this);
                            }
                            alert.setCancelable(true)
                                    .setTitle(R.string.warningTitleText)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setMessage(R.string.connectionErrorText)
                                    .setPositiveButton(R.string.acceptText, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).show();
                        }

                        progressBar.setVisibility(View.GONE);

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