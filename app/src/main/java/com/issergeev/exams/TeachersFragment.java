package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

public class TeachersFragment extends Fragment {
    private RelativeLayout parentView, noTeachersLayout;
    private ListView teachersList;
    private ProgressBar progressBar;

    private TeachersAdapter adapter;
    private ArrayList<Teacher> teachers;

    private AlertDialog.Builder alert;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.teachers_fragment, container, false);

        parentView = (RelativeLayout) getActivity().findViewById(R.id.rootLayout);
        noTeachersLayout = (RelativeLayout) rootView.findViewById(R.id.noTeachers);
        teachersList = rootView.findViewById(R.id.teachersList);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        teachers = new ArrayList<Teacher>(10);

        new LoadTeachers().execute();

        return rootView;
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadTeachers extends AsyncTask<Void, Void, Void> {
        private final String BASE_URL = "http://exams-online.000webhostapp.com/";

        @Override
        protected Void doInBackground(Void... voids) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RequestAPI api = retrofit.create(RequestAPI.class);

            final Call<TeacherList> students = api.getTeachers();
            students.enqueue(new Callback<TeacherList>() {
                @Override
                public void onResponse(Call<TeacherList> call, Response<TeacherList> response) {
                    progressBar.setVisibility(View.GONE);

                    try {
                        teachers = response.body().getTeachers();
                        adapter = new TeachersAdapter(parentView.getContext(), teachers);
                        teachersList.setAdapter(adapter);

                        if (teachers.size() == 0) {
                            noTeachersLayout.setVisibility(View.VISIBLE);
                        }
                    } catch (NullPointerException e) {
                        Snackbar.make(parentView, R.string.unknown_error, Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<TeacherList> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        alert = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        alert = new AlertDialog.Builder(getActivity());
                    }
                    alert.setCancelable(true)
                            .setTitle(R.string.warning_title_text)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setMessage(R.string.connection_error_text)
                            .setPositiveButton(R.string.accept_text, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).show();
                }
            });

            return null;
        }
    }
}