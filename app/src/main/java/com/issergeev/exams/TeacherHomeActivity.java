package com.issergeev.exams;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TeacherHomeActivity extends AppCompatActivity {
    private SharedPreferences examsData = WelcomeActivity.examsData;

    Listener listener;

    RelativeLayout rootLayout;
    FloatingActionButton logoutButton;
    TextView fullName, patronymic;

    AlertDialog.Builder alert, alert_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        listener = new Listener();

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        fullName = (TextView) findViewById(R.id.FaLName);
        patronymic = (TextView) findViewById(R.id.patronymic);

        logoutButton = (FloatingActionButton) findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(listener);

        fullName.setText(examsData.getString("firstName", "First Name") + " " +
            examsData.getString("patronymic", "Patronymic"));
        patronymic.setText(examsData.getString("lastName", "Last Name"));
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alert = new AlertDialog.Builder(TeacherHomeActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            alert = new AlertDialog.Builder(TeacherHomeActivity.this);
        }
        alert.setCancelable(true)
                .setTitle(R.string.exitText)
                .setMessage(R.string.exitMessage)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    class Listener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.logoutButton :
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        alert = new AlertDialog.Builder(TeacherHomeActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        alert = new AlertDialog.Builder(TeacherHomeActivity.this);
                    }
                    alert.setCancelable(true)
                            .setTitle(R.string.exitText)
                            .setMessage(R.string.exitMessage)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                    break;
            }
        }
    }
}