package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.content.Intent;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PassActivity extends AppCompatActivity {
    private static final String DATA_PREFS_NAME = "Data";

    private SharedPreferences examsData;

    private String exam;

    public static ArrayList<Question> questionArrayList;

    public RelativeLayout rootLayout, noQuestionLayout;
    TextView heading;
    ProgressBar progressBar;

    Intent intent;

    public static Fragment fragment;
    public static FragmentManager fragmentManager;

    AlertDialog.Builder alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        examsData = getSharedPreferences(DATA_PREFS_NAME, Context.MODE_PRIVATE);

        intent = getIntent();
        exam = intent.getStringExtra("ExamName");

        fragmentManager = getFragmentManager();
        fragment = new PassFragment();

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        heading = (TextView) findViewById(R.id.examName);
        noQuestionLayout = (RelativeLayout) findViewById(R.id.noQuestions);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        heading.setText(exam);

        questionArrayList = new ArrayList<>(10);

        new LoadQuestions().execute();
    }

    @Override
    public void onBackPressed() {
        if (questionArrayList.size() > 0) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alert = new AlertDialog.Builder(PassActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                alert = new AlertDialog.Builder(PassActivity.this);
            }
            alert.setCancelable(true)
                    .setTitle(R.string.exit_text)
                    .setMessage(R.string.exit_test_message)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.return_to_attempt, null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadQuestions extends AsyncTask<Void, Void, Void> {
        private final String BASE_URL = "http://exams-online.online/";

        @Override
        protected Void doInBackground(Void... voids) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RequestAPI api = retrofit.create(RequestAPI.class);

            final Call<QuestionList> questions = api
                    .getQuestionsStudent(exam);
            questions.enqueue(new Callback<QuestionList>() {
                @Override
                public void onResponse(Call<QuestionList> call, Response<QuestionList> response) {
                    progressBar.setVisibility(View.GONE);

                    if (response.body() != null) {
                        if (!response.body().toString().equals("[]") || !response.body().equals("")) {
                            try {
                                questionArrayList = response.body().getQuestions();

                                Bundle bundle = new Bundle();
                                bundle.putSerializable("questionList", questionArrayList);

                                if (questionArrayList.size() > 0) {

                                    fragment.setArguments(bundle);

                                    fragmentManager.beginTransaction().add(R.id.rootLayout, fragment)
                                            .commit();
                                } else {
                                    noQuestionLayout.setVisibility(View.VISIBLE);
                                }
                            } catch (NullPointerException e) {
                                Snackbar.make(rootLayout, R.string.unknown_error, Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            noQuestionLayout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        noQuestionLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<QuestionList> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(rootLayout, R.string.unknown_error, Snackbar.LENGTH_LONG).show();
                }
            });

            return null;
        }
    }
}