package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PassFragment extends Fragment {
    private final String DATA_PREFS_NAME = "Data";

    private int questionNumber = 0;
    private  int maxQuestion;

    private int correct = 0;
    private String answerText;
    private String questionText;

    private String key;

    private HashMap<String, String> examList;
    private ArrayList<? extends Question> questionArrayList;

    RelativeLayout rootLayout;
    TextView question;
    EditText answer;
    Button next, previous;

    TextView heading;

    AlertDialog.Builder alert;

    SharedPreferences examsData;

    Listener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.exams_question_fragment, container, false);

        listener = new Listener();

        examsData = getActivity().getSharedPreferences(DATA_PREFS_NAME, getActivity().MODE_PRIVATE);

        examList = new HashMap<>(10);
        questionArrayList = getArguments().getParcelableArrayList("questionList");

        rootLayout = (RelativeLayout) getActivity().findViewById(R.id.rootLayout);
        question = (TextView) rootView.findViewById(R.id.question);
        answer = (EditText) rootView.findViewById(R.id.answer);
        next = (Button) rootView.findViewById(R.id.nextButton);
        previous = (Button) rootView.findViewById(R.id.previousButton);
        heading = (TextView) getActivity().findViewById(R.id.examName);

        maxQuestion = PassActivity.questionArrayList.size();
        inflateQuestion(questionNumber);

        next.setOnClickListener(listener);
        previous.setOnClickListener(listener);

        return rootView;
    }

    private void inflateQuestion(int questionNumber) {
        question.setText(questionArrayList.get(questionNumber).getQuestion());

        key = question.getText().toString();

        if (examList.containsKey(key)) {
            answer.setText(examList.get(key));
        } else {
            answer.setText("");
        }
    }

    class Listener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.nextButton :
                    questionNumber++;

                    if (questionNumber < maxQuestion) {

                        examList.put(question.getText().toString(), answer.getText().toString());

                        inflateQuestion(questionNumber);
                    } else {
                        questionNumber = maxQuestion - 1;

                        examList.put(question.getText().toString(), answer.getText().toString());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            alert = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            alert = new AlertDialog.Builder(getActivity());
                        }
                        alert.setCancelable(true)
                                .setTitle(R.string.warning_title_text)
                                .setMessage(R.string.maximalQuestionMessage)
                                .setPositiveButton(R.string.continue_text, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        for (int c = 0; c < questionArrayList.size(); c++) {
                                            answerText = questionArrayList.get(c).getAnswer();
                                            questionText = questionArrayList.get(c).getQuestion();

                                            if (examList.get(questionText).compareToIgnoreCase(answerText) == 0) {
                                                correct++;
                                            }

                                            Log.i("array", "\nStudent answer : " + examList.get(questionText) +  " = " + answerText);

                                            i++;
                                        }

                                        new SaveResults().execute(heading.getText().toString());
                                    }
                                })
                                .setNegativeButton(R.string.return_to_attempt, null)
                                .show();
                    }
                    break;
                case R.id.previousButton :
                    questionNumber--;

                    if (questionNumber > -1) {

                        examList.put(question.getText().toString(), answer.getText().toString());

                        inflateQuestion(questionNumber);
                    } else {
                        questionNumber = 0;

                        examList.put(question.getText().toString(), answer.getText().toString());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            alert = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            alert = new AlertDialog.Builder(getActivity());
                        }
                        alert.setCancelable(true)
                                .setTitle(R.string.warning_title_text)
                                .setMessage(R.string.minimalQuestionMessage)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        PassActivity.fragmentManager.beginTransaction()
                                                .remove(PassActivity.fragment)
                                                .commit();
                                        try {
                                            getView().findViewById(R.id.rootLayout).setVisibility(View.GONE);
                                        } catch (NullPointerException e) {}
                                        getActivity().finish();
                                    }
                                })
                                .setNegativeButton(R.string.no, null)
                                .show();
                    }
                    break;
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class SaveResults extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(final String... strings) {
            final String LOGIN_URL = "http://exams-online.online/save_results.php";
            final RequestQueue request = Volley.newRequestQueue(getActivity());
            StringRequest query = new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    switch (response) {
                        case "Success" :
                            Snackbar.make(rootLayout, R.string.email_added, Snackbar.LENGTH_SHORT).show();
                            break;
                        default :
                            Snackbar.make(rootLayout, R.string.unknown_response, Snackbar.LENGTH_LONG).show();
                            Log.i("net", response);
                            break;
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    int errorCode = error.networkResponse.statusCode;

                    switch (errorCode) {
                        case 302 :
                            Snackbar.make(getView().getRootView(), R.string.hotspot_error, Snackbar.LENGTH_LONG).show();
                            break;
                        case 423 :
                            Snackbar.make(getView().getRootView(), R.string.server_sleeping_text, Snackbar.LENGTH_LONG).show();
                            break;
                        default :
                            Snackbar.make(getView().getRootView(), R.string.unknown_error, Snackbar.LENGTH_LONG).show();
                            break;
                    }
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> results = new HashMap<>();

                    results.put("student_id_number", examsData.getString("SIDNumber", "0"));
                    results.put("exam_name", strings[0]);
                    results.put("result", String.valueOf(correct));

                    return results;
                }
            };
            request.add(query);

            return null;
        }
    }
}