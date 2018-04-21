package com.issergeev.exams;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    static final String DATA_PREFS_NAME = "Data";
    private String loginText = "", passwordText = "";

    Listener listener;

    Button createButton, loginButton;
    EditText login;
    TextInputEditText password;
    static SharedPreferences examsData;
    static SharedPreferences.Editor editor;

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
        setContentView(R.layout.activity_login);

        listener = new Listener();

        examsData = getApplicationContext().getSharedPreferences(DATA_PREFS_NAME, Context.MODE_PRIVATE);
        editor = examsData.edit();

        createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(listener);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(listener);
        login = (EditText) findViewById(R.id.login);
        password = (TextInputEditText) findViewById(R.id.password);
    }

    class Listener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loginButton :
                    //Login procedure
                    break;
                 case R.id.createButton :
                     startActivity(new Intent(getApplicationContext(), RegActivity.class));
                     overridePendingTransition(R.anim.activity_appear_anim, R.anim.activity_disappear_anim);
                     break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        loginText = login.getText().toString();
        passwordText = password.getText().toString();

        editor.putString("Login", loginText);
        editor.putString("Password", passwordText);
        editor.apply();
    }
}