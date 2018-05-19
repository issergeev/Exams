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

import org.w3c.dom.Text;

import java.util.List;
import java.util.Locale;

public class ResultsAdapterTeacher extends ArrayAdapter<Result> {
    private LayoutInflater layoutInflater;

    private List<Result> resultList;
    Context context;

    public ResultsAdapterTeacher(@NonNull Context context, List<Result> results) {
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
            View view = layoutInflater.inflate(R.layout.results_card_teacher, parent, false);
            viewHolder = ViewHolder.create((LinearLayout) view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Result item = getItem(position);

        if (item != null) {
            if (Double.valueOf(item.getResult()) >= Double.valueOf(item.getTotal())) {
                viewHolder.result.setTextColor(context.getResources().getColor(android.R.color.holo_green_light));
            }

            viewHolder.SIDText.setText(context.getResources().getString(R.string.SIDText) + ":\n"
                    + item.getStudentID());
            viewHolder.FaLName.setText(item.getLastName() + "\n" + item.getFirstName());
            viewHolder.result.setText(context.getResources().getString(R.string.result_teacher) +
                    item.getResult() + "/" + item.getTotal() + "\n" +
                    String.format("%.2f", Double.valueOf(item.getResult())/Double.valueOf(item.getTotal()) * 100) + "%");
        }

        return viewHolder.parentLayout;
    }

    private static class ViewHolder {
        private final LinearLayout parentLayout;
        private final TextView SIDText;
        private final TextView FaLName;
        private final TextView result;

        private ViewHolder(LinearLayout parentLayout, TextView SIDText, TextView FaLName, TextView result) {
            this.parentLayout = parentLayout;
            this.SIDText = SIDText;
            this.FaLName = FaLName;
            this.result = result;
        }

        private static ViewHolder create(LinearLayout parentLayout) {
            TextView SIDText = parentLayout.findViewById(R.id.SIDText);
            TextView FaLName = parentLayout.findViewById(R.id.FaL_name);
            TextView result = parentLayout.findViewById(R.id.result);

            return new ViewHolder(parentLayout, SIDText, FaLName, result);
        }
    }
}