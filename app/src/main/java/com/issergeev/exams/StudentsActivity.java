package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class StudentsActivity extends AppCompatActivity {
    private static final String DATA_PREFS_NAME = "Data";

    private ArrayAdapter<String> adapter;
    private ArrayList<String> groupsList;

    private SharedPreferences examsData;
    private SharedPreferences.Editor editor;

    private RelativeLayout rootLayout, noGroupsLayout;
    private ListView groupList;
    private CardView heading;
    private ProgressBar progressBar;

    private Fragment fragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private AlertDialog.Builder alert;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addButton :
                Intent intent = new Intent(this, AddNewStudentActivity.class);

                groupsList.clear();

                for (int i = 0; i < adapter.getCount(); i++) {
                    groupsList.add(adapter.getItem(i));
                }

                if (groupsList.size() > 0) {
                    Bundle data = new Bundle();
                    data.putSerializable("Groups", (Serializable) groupsList);
                    intent.putExtra("GroupsList", data);

                    startActivity(intent);
                } else {
                    Snackbar.make(rootLayout, R.string.no_groups_to_manage, Snackbar.LENGTH_LONG).show();
                }
                break;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        examsData = getSharedPreferences(DATA_PREFS_NAME, Context.MODE_PRIVATE);
        editor = examsData.edit();

        groupsList = new ArrayList<>(5);

        adapter = new ArrayAdapter<>(StudentsActivity.this, android.R.layout.simple_list_item_1);

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        noGroupsLayout = (RelativeLayout) findViewById(R.id.noGroups);
        heading = (CardView) findViewById(R.id.heading);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        groupList = (ListView) findViewById(R.id.groups_list);

        fragmentManager = getFragmentManager();
        fragment = new StudentsListFragment();

        groupList.setAdapter(adapter);
        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                heading.setVisibility(View.GONE);
                groupList.setVisibility(View.GONE);

                Bundle bundle = new Bundle();
                bundle.putString("groupNumber", adapter.getItem(i));

                fragment.setArguments(bundle);

                transaction = fragmentManager.beginTransaction()
                        .add(R.id.rootLayout, fragment)
                        .addToBackStack(null);
                transaction.commit();
            }
        });

        new LoadGroups().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadGroups extends AsyncTask<Void, Void, Void> {
        private final String BASE_URL = "http://exams-online.000webhostapp.com/";

        @Override
        protected Void doInBackground(Void... voids) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RequestAPI api = retrofit.create(RequestAPI.class);

            final Call<List<String>> groups = api.getGroups(examsData.getString("TeacherIDNumber", "0"));
            groups.enqueue(new Callback<List<String>>() {
                @Override
                public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                    progressBar.setVisibility(View.GONE);
                    try {
                        if (!response.body().toString().equals("[]")) {
                            adapter.addAll(response.body());
                            adapter.notifyDataSetChanged();
                        } else {
                            noGroupsLayout.setVisibility(View.VISIBLE);
                        }
                    } catch (NullPointerException e) {
                        Snackbar.make(rootLayout, R.string.unknown_error, Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<List<String>> call, Throwable t) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        alert = new AlertDialog.Builder(StudentsActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        alert = new AlertDialog.Builder(StudentsActivity.this);
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