package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class QuestionEditActivity extends AppCompatActivity {
    private String questionText, answerText;
    private int questionNumber;

    RelativeLayout rootLayout;
    EditText question, answer;
    Button updateButton;
    ProgressBar progressBar;

    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_edit);

        intent = getIntent();
        questionText = intent.getStringExtra("question");
        answerText = intent.getStringExtra("answer");
        questionNumber = intent.getIntExtra("question_number", -1);

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        question = (EditText) findViewById(R.id.question);
        answer = (EditText) findViewById(R.id.answer);
        updateButton = (Button) findViewById(R.id.updateButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        question.setText(questionText);
        answer.setText(answerText);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                new QuestionUpdater().execute();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class QuestionUpdater extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final String CREATE_URL = "http://exams-online.online/update_question.php";

            RequestQueue request = Volley.newRequestQueue(QuestionEditActivity.this);
            StringRequest query = new StringRequest(Request.Method.POST, CREATE_URL, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null) {
                        updateButton.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

                        switch (response) {
                            case "Success" :
                                Snackbar.make(rootLayout, R.string.question_updated, Snackbar.LENGTH_SHORT).show();
                                break;
                            default :
                                Log.i("net", response);
                                break;
                        }
                    }
                }
            }, new com.android.volley.Response.ErrorListener()

            {
                @Override
                public void onErrorResponse (VolleyError error){
                    if (error.networkResponse != null) {
                        updateButton.setEnabled(true);

                        int errorCode = error.networkResponse.statusCode;

                        if (errorCode == 423) {
                            Snackbar.make(rootLayout, R.string.server_sleeping_text, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(rootLayout, R.string.unknown_error, Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    progressBar.setVisibility(View.GONE);
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> questionData = new HashMap<>();

                    questionText = question.getText().toString();
                    answerText = answer.getText().toString();

                    Log.i("info", "Number : " + questionNumber + "; Question: "
                            + questionText + "; Answer: " + answerText);

                    questionData.put("number", String.valueOf(questionNumber));
                    questionData.put("question", questionText);
                    questionData.put("answer", answerText);

                    return questionData;
                }
            };
            request.add(query);

            return null;
        }
    }
}