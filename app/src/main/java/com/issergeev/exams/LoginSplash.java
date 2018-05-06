package com.issergeev.exams;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class LoginSplash extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_login);

        try {
            Thread.sleep(2000);
            startActivity(new Intent(this, WelcomeActivity.class));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}