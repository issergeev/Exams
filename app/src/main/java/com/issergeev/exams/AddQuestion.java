package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddQuestion extends AppCompatActivity {
    private String questionText, answerText, examText;
    private ArrayList<String> examsList;
    private ArrayAdapter<String> adapter;

    private View view;
    private boolean correct;

    RelativeLayout rootLayout;
    EditText question, answer;
    Button addButton;
    Spinner exams;
    ProgressBar progressBar;

    AlertDialog.Builder alert;

    Intent intent;

    Listener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        listener = new Listener();

        intent = getIntent();
        Bundle data = intent.getBundleExtra("ExamsList");

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        question = (EditText) findViewById(R.id.question_text);
        answer = (EditText) findViewById(R.id.answer_text);
        addButton = (Button) findViewById(R.id.addButton);
        exams = (Spinner) findViewById(R.id.exams_selector);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        examsList = new ArrayList<>(5);
        examsList.addAll((Collection<? extends String>) data.getSerializable("Exams"));
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, examsList);

        exams.setAdapter(adapter);

        exams.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                examText = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                examText = null;
            }
        });

        addButton.setOnClickListener(listener);
        question.setOnClickListener(listener);
        answer.setOnClickListener(listener);
    }

    private void check() {
        for (int i = 0; i < rootLayout.getChildCount(); i++) {
            view = rootLayout.getChildAt(i);
            if (view instanceof EditText && ((EditText) view).getText().toString().trim().equals("")) {
                view.setBackground(getResources().getDrawable(R.drawable.error_background));
                correct = false;
            }
        }

        if (correct) {
            new Adder().execute();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alert = new AlertDialog.Builder(AddQuestion.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                alert = new AlertDialog.Builder(AddQuestion.this);
            }
            alert.setCancelable(true)
                    .setTitle(R.string.warning_title_text)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.fieldsMissed)
                    .setPositiveButton(R.string.accept_text, null).show();
        }
    }

    private class Listener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.addButton :
                    correct = true;
                    check();
                    break;
                default :
                    question.setBackground(getResources().getDrawable(R.drawable.edit_text_background));
                    answer.setBackground(getResources().getDrawable(R.drawable.edit_text_background));
                    break;
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class Adder extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final String CREATE_URL = "http://exams-online.online/add_question.php";

            RequestQueue request = Volley.newRequestQueue(AddQuestion.this);
            StringRequest query = new StringRequest(Request.Method.POST, CREATE_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null) {
                        addButton.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

                        switch (response) {
                            case "Success" :
                                Snackbar.make(rootLayout, R.string.question_added, Snackbar.LENGTH_SHORT).show();
                                break;
                            case "Question exists" :
                                Snackbar.make(rootLayout, R.string.question_exists, Snackbar.LENGTH_LONG).show();
                                break;
                            default :
                                Log.i("net", response);
                                break;
                        }
                    }
                }
            }, new Response.ErrorListener()

            {
                @Override
                public void onErrorResponse (VolleyError error){
                    if (error.networkResponse != null) {
                        addButton.setEnabled(true);

                        int errorCode = error.networkResponse.statusCode;

                        if (errorCode == 423) {
                            Snackbar.make(rootLayout, R.string.server_sleeping_text, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(rootLayout, R.string.unknown_error, Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            alert = new AlertDialog.Builder(AddQuestion.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            alert = new AlertDialog.Builder(AddQuestion.this);
                        }
                        alert.setCancelable(true)
                                .setTitle(R.string.warning_title_text)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setMessage(R.string.connection_error_text)
                                .setPositiveButton(R.string.accept_text, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        addButton.setEnabled(true);
                                    }
                                }).show();
                    }

                    progressBar.setVisibility(View.GONE);
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> data = new HashMap<>();

                    questionText = question.getText().toString().trim();
                    answerText = answer.getText().toString().trim();

                    data.put("exam_name", examText);
                    data.put("question", questionText);
                    data.put("answer", answerText);

                    return data;
                }
            };
            request.add(query);

            return null;
        }
    }
}