package com.issergeev.exams;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {
    final int ANIMATION_DURATION = 2000;

    DisplayMetrics metrics;
    Handler handler;
    Button startButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        metrics = getResources().getDisplayMetrics();
        handler = new Handler(Looper.myLooper());

        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation anim = new ScaleAnimation(
                        1f, metrics.xdpi / 2,
                        1f, metrics.ydpi / 2,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setFillAfter(false);
                anim.setDuration(ANIMATION_DURATION);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(ANIMATION_DURATION -300);
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


            }
        });
    }
}