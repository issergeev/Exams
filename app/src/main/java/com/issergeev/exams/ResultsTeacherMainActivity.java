package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResultsTeacherMainActivity extends AppCompatActivity {
    private static final String DATA_PREFS_NAME = "Data";

    private ArrayAdapter<String> adapter;

    private SharedPreferences examsData;
    private SharedPreferences.Editor editor;

    private RelativeLayout rootLayout, noExamsLayout;
    private ListView list;
    private CardView heading;
    private ProgressBar progressBar;

    private static FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private Fragment fragment;

    private AlertDialog.Builder alert;

    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams_teacher);

        examsData = getSharedPreferences(DATA_PREFS_NAME, Context.MODE_PRIVATE);
        editor = examsData.edit();

        fragmentManager = getFragmentManager();
        fragment = new ResultsListFragment();

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        noExamsLayout = (RelativeLayout) findViewById(R.id.noExams);
        heading = (CardView) findViewById(R.id.heading_activity);
        list = (ListView) findViewById(R.id.resultsList);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                heading.setVisibility(View.GONE);

                Bundle bundle = new Bundle();
                bundle.putString("examName", adapter.getItem(i));

                fragment.setArguments(bundle);

                transaction = fragmentManager.beginTransaction()
                        .add(R.id.rootLayout, fragment)
                        .addToBackStack(null);
                transaction.commit();
            }
        });

        new LoadExams().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadExams extends AsyncTask<Void, Void, Void> {
        private final String BASE_URL = "http://exams-online.online/";

        @Override
        protected Void doInBackground(Void... voids) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RequestAPI api = retrofit.create(RequestAPI.class);

            final Call<List<String>> exams = api.getExams(examsData.getString("TeacherIDNumber", "0"));
            exams.enqueue(new Callback<List<String>>() {
                @Override
                public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                    progressBar.setVisibility(View.GONE);

                    if (response.body() != null) {
                        try {
                            if (!response.body().toString().equals("[]")) {
                                adapter.addAll(response.body());
                                adapter.notifyDataSetChanged();
                            } else {
                                noExamsLayout.setVisibility(View.VISIBLE);
                            }
                        } catch (NullPointerException e) {
                            Snackbar.make(rootLayout, R.string.unknown_error, Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        noExamsLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<List<String>> call, Throwable t) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        alert = new AlertDialog.Builder(ResultsTeacherMainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        alert = new AlertDialog.Builder(ResultsTeacherMainActivity.this);
                    }
                    alert.setCancelable(true)
                            .setTitle(R.string.warning_title_text)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setMessage(R.string.connection_error_text)
                            .setPositiveButton(R.string.accept_text, null)
                            .show();
                }
            });

            return null;
        }
    }
}