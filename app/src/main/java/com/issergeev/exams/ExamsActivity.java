package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExamsActivity extends AppCompatActivity {
    private static final String DATA_PREFS_NAME = "Data";

    private ArrayAdapter<String> adapter;
    private ArrayList<String> examsList;

    private SharedPreferences examsData;
    private SharedPreferences.Editor editor;

    RelativeLayout rootLayout, noExamsLayout;
    ListView list;
    CardView heading;
    ProgressBar progressBar;

    private static FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private Fragment fragment;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addButton:
                Intent intent = new Intent(this, AddQuestion.class);

                examsList.clear();

                for (int i = 0; i < adapter.getCount(); i++) {
                    examsList.add(adapter.getItem(i));
                }

                if (examsList.size() > 0) {
                    Bundle data = new Bundle();
                    data.putSerializable("Exams", (Serializable) examsList);
                    intent.putExtra("ExamsList", data);

                    startActivity(intent);
                } else {
                    Snackbar.make(rootLayout, R.string.no_exams_to_manage, Snackbar.LENGTH_LONG).show();
                }
                break;
        }

        return true;
    }

    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams);

        examsData = getSharedPreferences(DATA_PREFS_NAME, Context.MODE_PRIVATE);
        editor = examsData.edit();

        fragmentManager = getFragmentManager();
        fragment = new QuestionsListFragment();

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        noExamsLayout = (RelativeLayout) findViewById(R.id.noExams);
        heading = (CardView) findViewById(R.id.heading_activity);
        list = (ListView) findViewById(R.id.resultsList);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        examsList = new ArrayList<>(5);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                heading.setVisibility(View.GONE);
                list.setVisibility(View.GONE);

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
                    Snackbar.make(rootLayout, R.string.unknown_error, Snackbar.LENGTH_LONG).show();
                }
            }); {
            }

            return null;
        }
    }
}