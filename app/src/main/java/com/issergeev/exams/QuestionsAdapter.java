package com.issergeev.exams;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

public class QuestionsAdapter extends ArrayAdapter<Question> {
    private LayoutInflater layoutInflater;

    private List<Question> questionList;
    Context context;

    public QuestionsAdapter(@NonNull Context context, List<Question> questions) {
        super(context, 0, questions);

        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        questionList = questions;
    }

    @Nullable
    @Override
    public Question getItem(int position) {
        return questionList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            View view = layoutInflater.inflate(R.layout.question_card, parent, false);
            viewHolder = ViewHolder.create((RelativeLayout) view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Question item = getItem(position);

        if (item != null) {
            viewHolder.question.setText(item.getQuestion());
            viewHolder.answer.setText(item.getAnswer());
        }

        return viewHolder.parentLayout;
    }

    private static class ViewHolder {
        private final RelativeLayout parentLayout;
        private final TextView question;
        private final TextView answer;

        private ViewHolder(RelativeLayout parentLayout, TextView question, TextView answer) {
            this.parentLayout = parentLayout;
            this.question = question;
            this.answer = answer;
        }

        private static ViewHolder create(RelativeLayout parentLayout) {
            TextView question = parentLayout.findViewById(R.id.question_text);
            TextView answer = parentLayout.findViewById(R.id.answer_text);

            return new ViewHolder(parentLayout, question, answer);
        }
    }
}