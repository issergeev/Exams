package com.issergeev.exams;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class WelcomeActivity extends AppCompatActivity {
    public static final String DATA_PREFS_NAME = "Data";

    public static SharedPreferences examsData;
    public static SharedPreferences.Editor editor;

    Listener listener;

    Button startButtonStudent, startButtonTeacher;
    ImageView signinButton;

    @Override
    protected void onResume() {
        super.onResume();

        startButtonStudent.setEnabled(true);
        startButtonTeacher.setEnabled(true);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        examsData = getApplicationContext().getSharedPreferences(DATA_PREFS_NAME, Context.MODE_PRIVATE);
        editor = examsData.edit();

        listener = new Listener();

        startButtonStudent = (Button) findViewById(R.id.startButtonStudent);
        startButtonTeacher = (Button) findViewById(R.id.startButtonTeacher);
        signinButton = (ImageView) findViewById(R.id.logo);
        startButtonStudent.setOnClickListener(listener);
        startButtonTeacher.setOnClickListener(listener);
        signinButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginAdminActivity.class));
                return false;
            }
        });
    }

    class Listener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.startButtonStudent :
                    startButtonStudent.setEnabled(false);
                    startActivity(new Intent(getApplicationContext(), LoginStudentActivity.class));
                    overridePendingTransition(R.anim.activity_appear_anim, R.anim.activity_disappear_anim);
                    break;
                case R.id.startButtonTeacher :
                    startButtonTeacher.setEnabled(false);
                    startActivity(new Intent(getApplicationContext(), LoginTeacherActivity.class));
                    overridePendingTransition(R.anim.activity_appear_anim, R.anim.activity_disappear_anim);
                    break;
            }
        }
    }
}