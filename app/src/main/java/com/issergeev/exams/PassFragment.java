package com.issergeev.exams;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PassFragment extends Fragment {
    private static int questionNumber = 0;
    private  static int maxQuestion;

    TextView question;
    EditText answer;
    Button next, previous;

    Listener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.exams_question_fragment, container, false);

        listener = new Listener();

        question = (TextView) rootView.findViewById(R.id.question);
        answer = (EditText) rootView.findViewById(R.id.answer);
        next = (Button) rootView.findViewById(R.id.nextButton);
        previous = (Button) rootView.findViewById(R.id.previousButton);

        maxQuestion = PassActivity.questionArrayList.size();
        inflateQuestion(0);

        Log.i("num", String.valueOf(maxQuestion));

        next.setOnClickListener(listener);
        previous.setOnClickListener(listener);

        return rootView;
    }

    private void inflateQuestion(int questionNumber) {
        if (questionNumber == 0) {
            question.setText(PassActivity.questionArrayList.get(questionNumber).getQuestion());
            answer.setText("");
        } else {
            question.setText(PassActivity.questionArrayList.get(questionNumber).getQuestion());
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

                        PassActivity.examList.put(question.getText().toString(),
                                PassActivity.questionArrayList.get(questionNumber - 1).getAnswer()
                                        .equals(answer.getText().toString()));
                        for (String s : PassActivity.examList.keySet()) {
                            Log.i("set", "Key : " + s + " = " + PassActivity.examList.get(s));
                        }
                        inflateQuestion(questionNumber);
                    } else {
                        questionNumber = maxQuestion - 1;
                        Toast.makeText(getActivity(), "Maximal activity", Toast.LENGTH_SHORT).show();
                    }

                    Log.i("num", String.valueOf(questionNumber));
                    break;
                case R.id.previousButton :
                    questionNumber--;

                    if (questionNumber > -1) {
                        inflateQuestion(questionNumber);

                        PassActivity.examList.put(question.getText().toString(),
                                PassActivity.questionArrayList.get(questionNumber + 1).getAnswer()
                                        .equals(answer.getText().toString()));
                        for (String s : PassActivity.examList.keySet()) {
                            Log.i("set", "Key : " + s + " = " + PassActivity.examList.get(s));
                        }
                    } else {
                        questionNumber = 0;
                        Toast.makeText(getActivity(), "Minimal activity", Toast.LENGTH_SHORT).show();
                    }

                    Log.i("num", String.valueOf(questionNumber));
                    break;
            }
        }
    }
}