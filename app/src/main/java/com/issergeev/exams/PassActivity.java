package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PassActivity extends AppCompatActivity {
    private String exam;

    public static ArrayList<Question> questionArrayList;

    RelativeLayout rootLayout, noQuestionLayout;
    TextView heading;
    ProgressBar progressBar;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);

        intent = getIntent();
        exam = intent.getStringExtra("ExamName");

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        heading = (TextView) findViewById(R.id.examName);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        heading.setText(exam);

        questionArrayList = new ArrayList<>(10);

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
                        for (int i = 0; i < questionArrayList.size(); i++) {
                            Log.i("array", questionArrayList.get(i).getQuestion());
                        }

                        if (questionArrayList.size() == 0) {
                            noQuestionLayout.setVisibility(View.VISIBLE);
                        }
                    } catch (NullPointerException e) {
                        Snackbar.make(rootLayout, R.string.unknown_error, Snackbar.LENGTH_LONG).show();

                        Log.i("net", "Error " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<QuestionList> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(rootLayout, R.string.unknown_error, Snackbar.LENGTH_LONG).show();

                    Log.i("net", "Failture " + t.getMessage());
                }
            });

            return null;
        }
    }
}