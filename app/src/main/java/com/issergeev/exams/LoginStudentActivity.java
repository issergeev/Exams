package com.issergeev.exams;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class LoginStudentActivity extends AppCompatActivity {
    public static final String DATA_PREFS_NAME = "Data";
    private String loginText = "", passwordText = "";

    Listener listener;

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
                    //signIn();
                    startActivity(new Intent(LoginStudentActivity.this, HomeActivity.class));
                    break;
                case R.id.createButton :
                    startActivity(new Intent(getApplicationContext(), RegActivity.class));
                    overridePendingTransition(R.anim.activity_appear_anim, R.anim.activity_disappear_anim);
                    break;
            }
        }
    }

    private void signIn() {
        final String loginURL = "http://exams-online.000webhostapp.com/sign_in.php";
        final RequestQueue request = Volley.newRequestQueue(LoginStudentActivity.this);
        StringRequest query = new StringRequest(Request.Method.POST, loginURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    switch (response) {
                        case "Success" :
                            startActivity(new Intent(LoginStudentActivity.this, LoginSplash.class));
                            break;
                        case "User not found" :
                            break;
                        default :
                            Log.i("net", response);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
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
}