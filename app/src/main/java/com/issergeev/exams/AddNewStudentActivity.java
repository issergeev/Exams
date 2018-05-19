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
    private final String TEXT_MASK = "###-##/##";

    private TextMask mask;
    private String SIDText, firstNameText, lastNameText, patronymicText, groupNumberText;

    private View view;
    private boolean correct;

    RelativeLayout rootLayout, container;
    EditText SIDInput, firstNameInput, lastNameInput, patronymicInput;
    Spinner groupNumberInput;
    Button addButton;
    ProgressBar progressBar;

    AlertDialog.Builder alert;

    ArrayList<String> groupsList;
    ArrayAdapter<String> adapter;

    Intent intent;

    Listener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_student);

        listener = new Listener();

        intent = getIntent();
        Bundle data = intent.getBundleExtra("GroupsList");

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        container = (RelativeLayout) findViewById(R.id.info_container);
        SIDInput = (EditText) findViewById(R.id.studentIDNumber);
        firstNameInput = (EditText) findViewById(R.id.firstName);
        lastNameInput = (EditText) findViewById(R.id.lastName);
        patronymicInput = (EditText) findViewById(R.id.patronymic);
        groupNumberInput = (Spinner) findViewById(R.id.groupSelector);
        addButton = (Button) findViewById(R.id.addButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mask = new TextMask(SIDInput, TEXT_MASK);

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

        addButton.setOnClickListener(listener);

        SIDInput.setOnClickListener(listener);
        firstNameInput.setOnClickListener(listener);
        lastNameInput.setOnClickListener(listener);
        patronymicInput.setOnClickListener(listener);
        SIDInput.setOnFocusChangeListener(listener);
        firstNameInput.setOnFocusChangeListener(listener);
        lastNameInput.setOnFocusChangeListener(listener);
        patronymicInput.setOnFocusChangeListener(listener);
    }

    private class Listener implements View.OnClickListener, View.OnFocusChangeListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.addButton :
                    addButton.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    correct = true;

                    SIDText = SIDInput.getText().toString();

                    for (int i = 0; i < SIDText.length(); i++) {
                        Character SIDChar = SIDText.charAt(i);
                        Character maskChar = TEXT_MASK.charAt(i);

                        if (maskChar.compareTo('#') != 0 && SIDChar.compareTo(maskChar) != 0) {
                            Snackbar.make(rootLayout, R.string.sid_length, Snackbar.LENGTH_LONG).show();
                            return;
                        }
                    }

                    check();
                    break;
                default :
                    for (int i = 0; i < container.getChildCount(); i++) {
                        view = container.getChildAt(i);
                        if (view instanceof EditText && ((EditText) view).getText().toString().trim().equals("")) {
                            ((EditText) view).setText("");
                            ((EditText) view).setHintTextColor(getResources().getColor(R.color.colorHint));
                            correct = false;
                        }
                    }
                    break;
            }
        }

        @Override
        public void onFocusChange(View view, boolean b) {
            if (firstNameInput.getCurrentHintTextColor() != getResources().getColor(R.color.colorHint)) {
                for (int i = 0; i < container.getChildCount(); i++) {
                    view = container.getChildAt(i);
                    if (view instanceof EditText && ((EditText) view).getText().toString().trim().equals("")) {
                        ((EditText) view).setText("");
                        ((EditText) view).setHintTextColor(getResources().getColor(R.color.colorHint));
                        correct = false;
                    }
                }
            }
        }
    }

    private void check() {
        SIDText = SIDInput.getText().toString().trim();
        firstNameText = firstNameInput.getText().toString().trim();
        lastNameText = lastNameInput.getText().toString().trim();
        patronymicText = patronymicInput.getText().toString().trim();

        for (int i = 0; i < container.getChildCount(); i++) {
            view = container.getChildAt(i);
            if ((view instanceof EditText && ((EditText) view).getText().toString().trim().equals("") ||
                    view instanceof EditText && ((EditText) view).getText().toString().trim().length() < 2) &&
                    view.getId() != R.id.patronymic) {
                ((EditText) view).setText("");
                ((EditText) view).setHintTextColor(getResources().getColor(R.color.colorError));

                Log.i("happy", String.valueOf(view.getId()));

                correct = false;
            }
        }

        if (SIDText.length() == 9 && firstNameText.length() > 2 && lastNameInput.length() > 2) {
            correct = true;
        } else {
            correct = false;
        }

        if (correct) {
            new Adder().execute();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alert = new AlertDialog.Builder(AddNewStudentActivity.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                alert = new AlertDialog.Builder(AddNewStudentActivity.this);
            }
            alert.setCancelable(true)
                    .setTitle(R.string.warning_title_text)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.fieldsMissed)
                    .setPositiveButton(R.string.accept_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            addButton.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                        }
                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            addButton.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                        }
                    })
                    .show();
        }
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
                                Snackbar.make(rootLayout, R.string.unknown_response, Snackbar.LENGTH_LONG).show();
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

                    SIDText = SIDInput.getText().toString()
                            .replace("/", "").replace("-","");
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