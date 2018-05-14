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

public class AddNewStudentActivity extends AppCompatActivity {
    private String SIDText, firstNameText, lastNameText, patronymicText, groupNumberText;

    RelativeLayout rootLayout;
    EditText SIDInput, firstNameInput, lastNameInput, patronymicInput;
    Spinner groupNumberInput;
    Button addButton;
    ProgressBar progressBar;

    AlertDialog.Builder alert;

    ArrayList<String> groupsList;
    ArrayAdapter<String> adapter;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_student);

        intent = getIntent();
        Bundle data = intent.getBundleExtra("GroupsList");

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        SIDInput = (EditText) findViewById(R.id.student_student_id_number);
        firstNameInput = (EditText) findViewById(R.id.student_first_name);
        lastNameInput = (EditText) findViewById(R.id.student_last_name);
        patronymicInput = (EditText) findViewById(R.id.student_patronymic);
        groupNumberInput = (Spinner) findViewById(R.id.group_selector);
        addButton = (Button) findViewById(R.id.addButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        groupsList = new ArrayList<>(5);
        groupsList.addAll((Collection<? extends String>) data.getSerializable("Groups"));

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groupsList);
        groupNumberInput.setAdapter(adapter);

        groupNumberInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                groupNumberText = adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                groupNumberText = null;
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                new Adder().execute();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class Adder extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final String CREATE_URL = "http://exams-online.online/add_new_student.php";

            RequestQueue request = Volley.newRequestQueue(AddNewStudentActivity.this);
            StringRequest query = new StringRequest(Request.Method.POST, CREATE_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null) {
                        addButton.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

                        switch (response) {
                            case "Success" :
                                Snackbar.make(rootLayout, R.string.student_added, Snackbar.LENGTH_SHORT).show();
                                break;
                            case "User exists" :
                                Snackbar.make(rootLayout, R.string.student_exists_text, Snackbar.LENGTH_LONG).show();
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
                            alert = new AlertDialog.Builder(AddNewStudentActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            alert = new AlertDialog.Builder(AddNewStudentActivity.this);
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

                    SIDText = SIDInput.getText().toString();
                    firstNameText = firstNameInput.getText().toString();
                    lastNameText = lastNameInput.getText().toString();
                    patronymicText = patronymicInput.getText().toString();

                    data.put("student_studentid_number", SIDText);
                    data.put("student_firstName", firstNameText);
                    data.put("student_lastName", lastNameText);
                    data.put("student_patronymic", patronymicText);
                    data.put("group_number", groupNumberText);

                    return data;
                }
            };
            request.add(query);

            return null;
        }
    }
}