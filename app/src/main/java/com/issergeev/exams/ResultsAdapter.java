package com.issergeev.exams;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ResultsAdapter extends ArrayAdapter<Result> {
    private LayoutInflater layoutInflater;

    private List<Result> resultList;
    Context context;

    public ResultsAdapter(@NonNull Context context, List<Result> results) {
        super(context, 0, results);

        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.resultList = results;
    }

    @Nullable
    @Override
    public Result getItem(int position) {
        return resultList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            View view = layoutInflater.inflate(R.layout.result_card, parent, false);
            viewHolder = ViewHolder.create((LinearLayout) view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Result item = getItem(position);

        if (item != null) {
            viewHolder.exam.setText(item.getExamName());
            viewHolder.result.setText(item.getResult());
        }

        return viewHolder.parentLayout;
    }

    private static class ViewHolder {
        private final LinearLayout parentLayout;
        private final TextView exam;
        private final TextView result;

        private ViewHolder(LinearLayout parentLayout, TextView exam, TextView result) {
            this.parentLayout = parentLayout;
            this.exam = exam;
            this.result = result;
        }

        private static ViewHolder create(LinearLayout parentLayout) {
            TextView exam = parentLayout.findViewById(R.id.examName);
            TextView result = parentLayout.findViewById(R.id.result);

            return new ViewHolder(parentLayout, exam, result);
        }
    }
}