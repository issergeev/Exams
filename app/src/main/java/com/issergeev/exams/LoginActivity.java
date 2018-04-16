package com.issergeev.exams;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    final int animationDuration = 2000;
    String[] data = new String[] {"", ""};

    Listener listener = new Listener();

    Button createButton, loginButton;
    EditText login, password;
    DisplayMetrics metrics;
    Intent dataIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        metrics = getResources().getDisplayMetrics();
        dataIntent = getIntent();

        createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(listener);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(listener);
        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);

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
                     anim.setDuration(animationDuration);
                     //startButton.startAnimation(anim);

                     new Thread(new Runnable() {
                         @Override
                         public void run() {
                             try {
                                 Thread.sleep(animationDuration-300);
                                 startActivity(new Intent(getApplicationContext(), RegActivity.class));
                             } catch (InterruptedException e) {
                                 e.printStackTrace();
                             }
                         }
                     }).start();
            }
        }
    }
}