package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminMainActivity extends AppCompatActivity {
    private String reportText;

    private RelativeLayout rootLayout;
    private ProgressBar progressBar;

    private BottomNavigationView navigation;

    private FragmentManager fragmentManager;
    private Fragment groupsFragment, teachersFragment;

    private AlertDialog.Builder alert;

    private Intent intent;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.teachers:
                    fragmentManager.beginTransaction()
                            .replace(R.id.mainFragment, teachersFragment)
                            .commit();
                    return true;
                case R.id.groups:
                    fragmentManager.beginTransaction()
                            .replace(R.id.mainFragment, groupsFragment)
                            .commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        fragmentManager = getFragmentManager();

        groupsFragment = new GroupsFragment();
        teachersFragment = new TeachersFragment();

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        fragmentManager.beginTransaction()
                .replace(R.id.mainFragment, teachersFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reportButton:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alert = new AlertDialog.Builder(AdminMainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    alert = new AlertDialog.Builder(AdminMainActivity.this);
                }
                final EditText report = new EditText(AdminMainActivity.this);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                report.setLayoutParams(params);
                report.setTextColor(getResources().getColor(R.color.colorMain));
                alert.setCancelable(true)
                        .setTitle(R.string.report)
                        .setMessage(R.string.report_message)
                        .setView(report)
                        .setPositiveButton(R.string.send_button_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                reportText = report.getText().toString();

                                new ReportSender().execute();
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                Snackbar.make(rootLayout, R.string.report_denied, Snackbar.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alert = new AlertDialog.Builder(AdminMainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            alert = new AlertDialog.Builder(AdminMainActivity.this);
        }
        alert.setCancelable(true)
                .setTitle(R.string.exit_text)
                .setMessage(R.string.exit_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @SuppressLint("StaticFieldLeak")
    private class ReportSender extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            final String LOGIN_URL = "http://exams-online.online/send_report.php";
            final RequestQueue request = Volley.newRequestQueue(AdminMainActivity.this);
            StringRequest query = new StringRequest(Request.Method.POST, LOGIN_URL, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    switch (response) {
                        case "Success" :
                            Snackbar.make(rootLayout, R.string.report_sent, Snackbar.LENGTH_SHORT).show();
                            break;
                        case "Error sending response" :
                            Snackbar.make(rootLayout, R.string.report_error, Snackbar.LENGTH_LONG).show();
                            break;
                        default :
                            Snackbar.make(rootLayout, R.string.unknown_response, Snackbar.LENGTH_LONG).show();
                            break;
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    int errorCode = error.networkResponse.statusCode;

                    switch (errorCode) {
                        case 302 :
                            Snackbar.make(rootLayout, R.string.hotspot_error, Snackbar.LENGTH_SHORT).show();
                            break;
                        case 423 :
                            Snackbar.make(rootLayout, R.string.server_sleeping_text, Snackbar.LENGTH_SHORT).show();
                            break;
                        default :
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                alert = new AlertDialog.Builder(AdminMainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                alert = new AlertDialog.Builder(AdminMainActivity.this);
                            }
                            alert.setCancelable(true)
                                    .setTitle(R.string.warning_title_text)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setMessage(R.string.connection_error_text)
                                    .setPositiveButton(R.string.accept_text, null).show();
                            break;
                    }
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> data = new HashMap<>();

                    data.put("report", reportText);

                    return data;
                }
            };
            request.add(query);

            return null;
        }
    }
}