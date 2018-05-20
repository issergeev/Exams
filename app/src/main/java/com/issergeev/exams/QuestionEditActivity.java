package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

    private RelativeLayout rootLayout;
    private EditText question, answer;
    private Button updateButton, deleteButton;
    private ProgressBar progressBar;

    private Intent intent;

    private AlertDialog.Builder alert;

    private Listener listener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_edit);

        intent = getIntent();
        questionText = intent.getStringExtra("question");
        answerText = intent.getStringExtra("answer");
        questionNumber = intent.getIntExtra("question_number", -1);

        listener = new Listener();

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        question = (EditText) findViewById(R.id.question);
        answer = (EditText) findViewById(R.id.answer);
        updateButton = (Button) findViewById(R.id.updateButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        question.setText(questionText);
        answer.setText(answerText);

        updateButton.setOnClickListener(listener);

        deleteButton.setOnClickListener(listener);


    }

    class Listener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.updateButton :
                    updateButton.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);

                    new QuestionUpdater().execute();
                    break;
                case R.id.deleteButton :
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        alert = new AlertDialog.Builder(QuestionEditActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        alert = new AlertDialog.Builder(QuestionEditActivity.this);
                    }
                    alert.setCancelable(true)
                            .setTitle(R.string.warning_title_text)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setMessage(R.string.deleteMessage)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteButton.setEnabled(false);
                                    progressBar.setVisibility(View.VISIBLE);

                                    new QuestionDeleter().execute();
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                    break;
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class QuestionUpdater extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final String CREATE_URL = "http://exams-online.online/update_question.php";

            RequestQueue request = Volley.newRequestQueue(QuestionEditActivity.this);
            StringRequest query = new StringRequest(Request.Method.POST, CREATE_URL,
                    new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null) {
                        updateButton.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

                        switch (response) {
                            case "Success" :
                                Snackbar.make(QuestionsListFragment.parentView, R.string.question_updated,
                                        Snackbar.LENGTH_SHORT).show();
                                finish();
                                break;
                            case "Question exists" :
                                Snackbar.make(rootLayout, R.string.question_exists_new,
                                        Snackbar.LENGTH_SHORT).show();
                                break;
                            default :
                                Snackbar.make(rootLayout, R.string.unknown_response, Snackbar.LENGTH_LONG).show();
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
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            alert = new AlertDialog.Builder(QuestionEditActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            alert = new AlertDialog.Builder(QuestionEditActivity.this);
                        }
                        alert.setCancelable(true)
                                .setTitle(R.string.warning_title_text)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setMessage(R.string.connection_error_text)
                                .setPositiveButton(R.string.accept_text, null)
                                .show();
                    }

                    progressBar.setVisibility(View.GONE);
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> questionData = new HashMap<>();

                    questionText = question.getText().toString();
                    answerText = answer.getText().toString();

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

    @SuppressLint("StaticFieldLeak")
    private class QuestionDeleter extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final String CREATE_URL = "http://exams-online.online/delete_question.php";

            final RequestQueue request = Volley.newRequestQueue(QuestionEditActivity.this);
            StringRequest query = new StringRequest(Request.Method.POST, CREATE_URL,
                    new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null) {
                        updateButton.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

                        if (!response.equals("0")) {
                            Snackbar.make(QuestionsListFragment.parentView, R.string.question_deleted,
                                    Snackbar.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Snackbar.make(rootLayout, R.string.unknown_error, Snackbar.LENGTH_LONG).show();
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
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            alert = new AlertDialog.Builder(QuestionEditActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            alert = new AlertDialog.Builder(QuestionEditActivity.this);
                        }
                        alert.setCancelable(true)
                                .setTitle(R.string.warning_title_text)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setMessage(R.string.connection_error_text)
                                .setPositiveButton(R.string.accept_text, null)
                                .show();
                    }

                    progressBar.setVisibility(View.GONE);
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> questionData = new HashMap<>();

                    questionData.put("number", String.valueOf(questionNumber));

                    return questionData;
                }
            };
            request.add(query);

            return null;
        }
    }
}