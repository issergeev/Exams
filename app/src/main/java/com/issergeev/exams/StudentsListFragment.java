package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StudentsListFragment extends Fragment {
    private String groupNumber;

    private ListView studentsList;
    private View parentView;

    RelativeLayout noStudentsLayout;
    CardView heading;
    ProgressBar progressBar;

    private ArrayList<Student> arrayListStudent;
    private StudentsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.students_fragment, container, false);

        parentView = rootView.findViewById(R.id.rootLayout);
        noStudentsLayout = (RelativeLayout) rootView.findViewById(R.id.noStudents);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        heading = (CardView) getActivity().findViewById(R.id.heading);
        studentsList = (ListView) rootView.findViewById(R.id.students_list);
        studentsList.setAdapter(adapter);
        arrayListStudent = new ArrayList<>(10);

        groupNumber = getArguments().getString("groupNumber");

        new LoadStudents().execute();

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            heading.setVisibility(View.VISIBLE);
            adapter.clear();
            adapter.notifyDataSetChanged();
            noStudentsLayout.setVisibility(View.GONE);
        } catch (NullPointerException e) {}
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadStudents extends AsyncTask<Void, Void, Void> {
        private final String BASE_URL = "http://exams-online.online/";

        @Override
        protected Void doInBackground(Void... voids) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RequestAPI api = retrofit.create(RequestAPI.class);

            final Call<StudentList> students = api.getStudentList(groupNumber);
            students.enqueue(new Callback<StudentList>() {
                @Override
                public void onResponse(Call<StudentList> call, Response<StudentList> response) {
                    progressBar.setVisibility(View.GONE);

                    try {
                        arrayListStudent = response.body().getStudents();
                        adapter = new StudentsAdapter(parentView.getContext(), arrayListStudent);
                        studentsList.setAdapter(adapter);

                        if (arrayListStudent.size() == 0) {
                            noStudentsLayout.setVisibility(View.VISIBLE);
                        }
                    } catch (NullPointerException e) {
                        Snackbar.make(parentView, R.string.unknown_error, Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<StudentList> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(parentView, R.string.unknown_error, Snackbar.LENGTH_LONG).show();
                }
            });

            return null;
        }
    }
}