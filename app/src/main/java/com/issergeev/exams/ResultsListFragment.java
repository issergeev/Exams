package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResultsListFragment extends Fragment {
    private static final String DATA_PREFS_NAME = "Data";

    private SharedPreferences examsData;

    private String exam;

    public static ArrayList<Result> arrayListResults;

    RelativeLayout noResultsLayout;
    ListView resultsList;
    CardView heading;
    TextView examName;
    ProgressBar progressBar;

    public static View parentView;
    private ResultsAdapterTeacher adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.results_fragment, container, false);

        examsData = getActivity().getSharedPreferences(DATA_PREFS_NAME, Context.MODE_PRIVATE);

        parentView = (RelativeLayout) rootView.findViewById(R.id.rootLayoutFragment);
        noResultsLayout = (RelativeLayout) rootView.findViewById(R.id.noResults);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        heading = (CardView) getActivity().findViewById(R.id.heading_activity);
        examName = (TextView) rootView.findViewById(R.id.examName);
        resultsList = (ListView) rootView.findViewById(R.id.resultsList);
        arrayListResults = new ArrayList<>(10);

        exam = getArguments().getString("examName");

        examName.setText(exam);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        new LoadResults().execute();
    }

    @Override
    public void onStop() {
        super.onStop();

        noResultsLayout.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            heading.setVisibility(View.VISIBLE);
            adapter.clear();
            adapter.notifyDataSetChanged();
            noResultsLayout.setVisibility(View.GONE);
        } catch (NullPointerException e) {}
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
            RequestAPI api = retrofit.create(RequestAPI.class);

            final Call<ResultList> questions = api.getResultsTeacher(examsData.getString("TeacherIDNumber", "0"), exam);
            questions.enqueue(new Callback<ResultList>() {
                @Override
                public void onResponse(Call<ResultList> call, Response<ResultList> response) {
                    progressBar.setVisibility(View.GONE);

                    if (response.body() != null) {
                        try {
                            arrayListResults = response.body().getResults();
                            adapter = new ResultsAdapterTeacher(parentView.getContext(), arrayListResults);
                            resultsList.setAdapter(adapter);

                            if (arrayListResults.size() == 0) {
                                noResultsLayout.setVisibility(View.VISIBLE);
                            }
                        } catch (NullPointerException e) {
                            Snackbar.make(parentView, R.string.unknown_error, Snackbar.LENGTH_LONG).show();

                            e.printStackTrace();
                        }
                    } else {
                        noResultsLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<ResultList> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(parentView, R.string.unknown_response, Snackbar.LENGTH_LONG).show();
                }
            });

            return null;
        }
    }
}