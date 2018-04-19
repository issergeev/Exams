package com.issergeev.exams;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    final int ANIMATION_DURATION = 2000;
    static final String DATA_PREFS_NAME = "Data";
    private String[] data = new String[] {"", ""};
    String loginText = "", passwordText = "";

    Listener listener;

    Button createButton, loginButton;
    EditText login;
    TextInputEditText password;
    DisplayMetrics metrics;
    Intent dataIntent;
    static SharedPreferences examsData;
    static SharedPreferences.Editor editor;

    @Override
    protected void onResume() {
        super.onResume();

        loginText = examsData.getString("Login", "Login");
        passwordText = examsData.getString("Password", "Password");
        login.setText(loginText);
        password.setText(passwordText);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        listener = new Listener();

        metrics = getResources().getDisplayMetrics();
        dataIntent = getIntent();
        examsData = getApplicationContext().getSharedPreferences(DATA_PREFS_NAME, Context.MODE_PRIVATE);
        editor = examsData.edit();

        createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(listener);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(listener);
        login = (EditText) findViewById(R.id.login);
        password = (TextInputEditText) findViewById(R.id.password);

        if (dataIntent.hasExtra("LoginData")) {
            dataIntent = getIntent();
            data = dataIntent.getStringArrayExtra("LoginData");
            login.setText(data[0]);
            password.setText(data[1]);
        }
    }

    class Listener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loginButton :
                    //Login procedure
                    break;
                 case R.id.createButton :
                     Animation anim = new ScaleAnimation(
                             1f, metrics.xdpi / 2, // Start and end values for the X axis scaling
                             1f, metrics.ydpi / 2, // Start and end values for the Y axis scaling
                             Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                             Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
                     anim.setFillAfter(false); // Needed to keep the result of the animation
                     anim.setDuration(ANIMATION_DURATION);
                     //startButton.startAnimation(anim);

                     new Thread(new Runnable() {
                         @Override
                         public void run() {
                             try {
                                 Thread.sleep(ANIMATION_DURATION -300);
                                 startActivity(new Intent(getApplicationContext(), RegActivity.class));
                             } catch (InterruptedException e) {
                                 e.printStackTrace();
                             }
                         }
                     }).start();
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