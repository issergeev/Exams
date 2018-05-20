package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuestionsListFragment extends Fragment {
    private static final String DATA_PREFS_NAME = "Data";

    private SharedPreferences examsData;

    private String exam;

    private ListView questionList;
    public static ArrayList<Question> arrayListQuestions;

    private RelativeLayout noQuestionLayout;
    private ListView examList;
    private CardView heading;
    private ProgressBar progressBar;

    public static View parentView;
    private QuestionsAdapter adapter;

    private AlertDialog.Builder alert;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.questions_fragment, container, false);

        examsData = getActivity().getSharedPreferences(DATA_PREFS_NAME, Context.MODE_PRIVATE);

        parentView = rootView.findViewById(R.id.rootLayoutFragment);
        noQuestionLayout = (RelativeLayout) rootView.findViewById(R.id.noQuestions);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        heading = (CardView) getActivity().findViewById(R.id.heading_activity);
        examList = (ListView) getActivity().findViewById(R.id.resultsList);
        questionList = (ListView) rootView.findViewById(R.id.questions_list);

        arrayListQuestions = new ArrayList<>(10);

        exam = getArguments().getString("examName");

        questionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Intent intent = new Intent(getActivity(), QuestionEditActivity.class)
                            .putExtra("question", adapter.getItem(i).getQuestion())
                            .putExtra("answer", adapter.getItem(i).getAnswer())
                            .putExtra("question_number", adapter.getItem(i).getQuestionNumber());
                    startActivity(intent);
                } catch (NullPointerException e) {
                    Snackbar.make(rootView, R.string.unknown_error, Snackbar.LENGTH_LONG).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        new LoadQuestions().execute();
    }

    @Override
    public void onStop() {
        super.onStop();

        noQuestionLayout.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            heading.setVisibility(View.VISIBLE);
            examList.setVisibility(View.VISIBLE);
            adapter.clear();
            adapter.notifyDataSetChanged();
            noQuestionLayout.setVisibility(View.GONE);
        } catch (NullPointerException e) {}
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadQuestions extends AsyncTask<Void, Void, Void> {
        private final String BASE_URL = "http://exams-online.online/";

        @Override
        protected Void doInBackground(Void... voids) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RequestAPI api = retrofit.create(RequestAPI.class);

            final Call<QuestionList> questions = api
                .getQuestions(exam);
            questions.enqueue(new Callback<QuestionList>() {
                @Override
                public void onResponse(Call<QuestionList> call, Response<QuestionList> response) {
                    progressBar.setVisibility(View.GONE);

                    if (response.body() != null) {
                        try {
                            arrayListQuestions = response.body().getQuestions();
                            adapter = new QuestionsAdapter(parentView.getContext(), arrayListQuestions);
                            questionList.setAdapter(adapter);

                            if (arrayListQuestions.size() == 0) {
                                noQuestionLayout.setVisibility(View.VISIBLE);
                            }
                        } catch (NullPointerException e) {
                            Snackbar.make(parentView, R.string.unknown_error, Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        noQuestionLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<QuestionList> call, Throwable t) {
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
                            .setPositiveButton(R.string.accept_text, null)
                            .show();
                }
            });

            return null;
        }
    }
}