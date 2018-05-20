package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.issergeev.exams.StudentsExamsActivity.rootLayout;

public class AdminMainActivity extends AppCompatActivity {
    private ProgressBar progressBar;

    private AlertDialog.Builder alert;

    private FragmentManager fragmentManager;

    private Fragment groupsFragment, teachersFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.teachers :
                        fragmentManager.beginTransaction()
                                .replace(R.id.mainFragment, teachersFragment)
                                .commit();
                    return true;
                case R.id.groups :
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

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
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
            case R.id.reportButton :
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alert = new AlertDialog.Builder(AdminMainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    alert = new AlertDialog.Builder(AdminMainActivity.this);
                }
                final EditText email = new EditText(AdminMainActivity.this);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                email.setLayoutParams(params);
                email.setTextColor(getResources().getColor(R.color.colorMain));
                alert.setCancelable(false)
                        .setTitle(R.string.report)
                        .setMessage(R.string.report_message)
                        .setView(email)
                        .setPositiveButton(R.string.send_button_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

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
    private class SendReport extends AsyncTask<String, Void, Void> {
        private final String BASE_URL = "http://exams-online.online/";

        @Override
        protected Void doInBackground(String... strings) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            final RequestAPI api = retrofit.create(RequestAPI.class);

            final Call<String> resultListCall = api.sendReport(strings[0]);
            resultListCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    progressBar.setVisibility(View.GONE);

                    try {

                    } catch (Exception e) {
                        Snackbar.make(rootLayout, R.string.unknown_error, Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(rootLayout, R.string.unknown_error, Snackbar.LENGTH_LONG).show();
                }
            });

            return null;
        }
    }
}