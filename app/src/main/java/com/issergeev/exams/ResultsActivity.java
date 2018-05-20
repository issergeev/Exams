package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResultsActivity extends AppCompatActivity {
    private static final String DATA_PREFS_NAME = "Data";

    private ArrayList<Result> results;
    private ResultsAdapter adapter;

    private SharedPreferences examsData;

    private RelativeLayout rootLayout, noResultsLayout;
    private ListView list;
    private ProgressBar progressBar;

    private AlertDialog.Builder alert;

    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        examsData = getSharedPreferences(DATA_PREFS_NAME, Context.MODE_PRIVATE);

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        noResultsLayout = (RelativeLayout) findViewById(R.id.noResults);
        list = (ListView) findViewById(R.id.resultsList);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        results = new ArrayList<>(5);

        new LoadResults().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadResults extends AsyncTask<Void, Void, Void> {
        private final String BASE_URL = "http://exams-online.online/";

        @Override
        protected Void doInBackground(Void... voids) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            final RequestAPI api = retrofit.create(RequestAPI.class);

            final Call<ResultList> resultListCall = api.getResults(examsData.getString("SIDNumber", "0"));
            resultListCall.enqueue(new Callback<ResultList>() {
                @Override
                public void onResponse(Call<ResultList> call, Response<ResultList> response) {
                    progressBar.setVisibility(View.GONE);

                    try {
                        results = response.body().getResults();
                        adapter = new ResultsAdapter(ResultsActivity.this, results);

                        list.setAdapter(adapter);


                        if (results.size() == 0) {
                            noResultsLayout.setVisibility(View.VISIBLE);
                        }
                    } catch (NullPointerException e) {
                        Snackbar.make(rootLayout, R.string.unknown_error, Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResultList> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        alert = new AlertDialog.Builder(ResultsActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        alert = new AlertDialog.Builder(ResultsActivity.this);
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