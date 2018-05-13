package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StudentsActivity extends AppCompatActivity {
    private static final String DATA_PREFS_NAME = "Data";

    private SharedPreferences examsData;
    private SharedPreferences.Editor editor;

    ArrayAdapter<String> adapter;

    RelativeLayout rootLayout, noGroupsLayout;
    ListView studentsList;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        examsData = getSharedPreferences(DATA_PREFS_NAME, Context.MODE_PRIVATE);
        editor = examsData.edit();

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        noGroupsLayout = (RelativeLayout) findViewById(R.id.noGroups);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        adapter = new ArrayAdapter<>(StudentsActivity.this, android.R.layout.simple_list_item_1);

        studentsList = (ListView) findViewById(R.id.students_list);
        studentsList.setAdapter(adapter);
        studentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("press", adapter.getItem(i));
            }
        });

        new LoadGroups().execute();
    }

    @SuppressLint("StaticFieldLeak")
    class LoadGroups extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://exams-online.online/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RequestAPI api = retrofit.create(RequestAPI.class);

            final Call<List<String>> groups = api.getGroups(examsData.getInt("TeacherIDNumber", 0));
            groups.enqueue(new Callback<List<String>>() {
                @Override
                public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                    progressBar.setVisibility(View.GONE);

                    if (!response.body().toString().equals("[]")) {
                        try {
                            adapter.addAll(response.body());
                            adapter.notifyDataSetChanged();
                        } catch (NullPointerException e) {
                            Snackbar.make(rootLayout, R.string.unknownError, Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        noGroupsLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<List<String>> call, Throwable t) {
                    Snackbar.make(rootLayout, R.string.unknownError, Snackbar.LENGTH_LONG).show();
                }
            }); {
            }

            return null;
        }
    }
}