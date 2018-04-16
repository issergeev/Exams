package com.issergeev.exams;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegActivity extends AppCompatActivity implements View.OnLongClickListener {
    final int animationDuration = 2000;
    boolean isDeleted = false;

    EditText SIDInput, loginInput, passwordInput;
    Button createButton;

    DisplayMetrics metrics;
    TextMask mask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        metrics = getResources().getDisplayMetrics();

        SIDInput = (EditText) findViewById(R.id.SIDInput);
        SIDInput.setOnLongClickListener(this);
        loginInput = (EditText) findViewById(R.id.loginInput);
        loginInput.setOnLongClickListener(this);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        passwordInput.setOnLongClickListener(this);
        createButton = (Button) findViewById(R.id.regButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                            Thread.sleep(animationDuration - 300);
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class).putExtra("LoginData",
                                    new String[]{loginInput.getText().toString(), passwordInput.getText().toString()}));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        mask = new TextMask(SIDInput, "###-##/##");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(SIDInput.getWindowToken(), 0);
    }

    @Override
    public boolean onLongClick(View view) {
        Toast.makeText(this, "LongClick", Toast.LENGTH_SHORT).show();
        return true;
    }
}