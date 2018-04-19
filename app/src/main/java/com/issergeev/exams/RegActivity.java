package com.issergeev.exams;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class RegActivity extends AppCompatActivity implements View.OnLongClickListener {
    final int ANIMATION_DURATION = 2000;
    String loginText = "", passwordText = "";

    EditText SIDInput, loginInput, passwordInput;
    Button createButton;

    DisplayMetrics metrics;
    TextMask mask;
    Animation shakeAnimation;
    SharedPreferences examsData = LoginActivity.examsData;
    SharedPreferences.Editor editor = LoginActivity.editor;

    @Override
    protected void onResume() {
        super.onResume();

        loginText = examsData.getString("Login", "Login");
        passwordText = examsData.getString("Password", "Password");
        loginInput.setText(loginText);
        passwordInput.setText(passwordText);
    }

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
                            Thread.sleep(ANIMATION_DURATION - 300);
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

        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.forbid_anim);
    }

    @Override
    public boolean onLongClick(View view) {
        //Toast.makeText(this, "LongClick", Toast.LENGTH_SHORT).show();
        view.startAnimation(shakeAnimation);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        loginText = loginInput.getText().toString();
        passwordText = passwordInput.getText().toString();

        editor.putString("Login", loginText);
        editor.putString("Password", passwordText);
    }
}