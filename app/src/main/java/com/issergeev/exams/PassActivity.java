package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PassActivity extends AppCompatActivity {
    private String exam;
    public static HashMap<String, Boolean> examList;

    public static ArrayList<Question> questionArrayList;

    RelativeLayout rootLayout, noQuestionLayout;
    TextView heading;
    ProgressBar progressBar;

    Intent intent;

    Fragment fragment;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);

        intent = getIntent();
        exam = intent.getStringExtra("ExamName");

        fragmentManager = getFragmentManager();
        fragment = new PassFragment();

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        heading = (TextView) findViewById(R.id.examName);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        heading.setText(exam);

        questionArrayList = new ArrayList<>(10);
        examList = new HashMap<>(10);

        new LoadQuestions().execute();
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

            final Call<QuestionList> questions = api.getQuestions("Exam");
            questions.enqueue(new Callback<QuestionList>() {
                @Override
                public void onResponse(Call<QuestionList> call, Response<QuestionList> response) {
                    progressBar.setVisibility(View.GONE);

                    try {
                        questionArrayList = response.body().getQuestions();

                        fragmentManager.beginTransaction().add(R.id.rootLayout, fragment)
                            .commit();

                        if (questionArrayList.size() == 0) {
                            noQuestionLayout.setVisibility(View.VISIBLE);
                        }
                    } catch (NullPointerException e) {
                        Snackbar.make(rootLayout, R.string.unknown_error, Snackbar.LENGTH_LONG).show();
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