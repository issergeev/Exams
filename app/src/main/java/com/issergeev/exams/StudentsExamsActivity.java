package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StudentsExamsActivity extends AppCompatActivity {
    private final String DATA_PREFS_NAME = "Data";

    private ArrayAdapter<String> adapter;

    public static RelativeLayout rootLayout;
    RelativeLayout noExamsLayout;
    ListView examsList;
    CardView heading;
    ProgressBar progressBar;

    SharedPreferences examsData;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams_student);

        examsData = getSharedPreferences(DATA_PREFS_NAME, Context.MODE_PRIVATE);
        editor = examsData.edit();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        noExamsLayout = (RelativeLayout) findViewById(R.id.noExams);
        examsList = (ListView) findViewById(R.id.exams_list);
        heading = (CardView) findViewById(R.id.heading_activity);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        examsList.setAdapter(adapter);

        examsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(StudentsExamsActivity.this, PassActivity.class)
                        .putExtra("ExamName", adapter.getItem(i));
                startActivity(intent);
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

            final Call<List<String>> exams = api.getExamsStudents(examsData.getString("GroupNumber", "0"));
            exams.enqueue(new Callback<List<String>>() {
                @Override
                public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                    progressBar.setVisibility(View.GONE);

                    try {
                        if (!response.body().toString().equals("[]")) {
                            try {
                                adapter.addAll(response.body());
                                adapter.notifyDataSetChanged();
                            } catch (NullPointerException e) {
                                Snackbar.make(rootLayout, R.string.unknown_error, Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            noExamsLayout.setVisibility(View.VISIBLE);
                        }
                    } catch (NullPointerException e) {
                        Snackbar.make(rootLayout, R.string.unknown_response, Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<List<String>> call, Throwable t) {
                    Snackbar.make(rootLayout, R.string.unknown_error, Snackbar.LENGTH_LONG).show();
                }
            }); {
            }

            return null;
        }
    }
}