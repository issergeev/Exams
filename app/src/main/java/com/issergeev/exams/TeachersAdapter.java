package com.issergeev.exams;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class TeachersAdapter extends ArrayAdapter<Teacher> {
    private LayoutInflater layoutInflater;

    private List<Teacher> teachersList;
    Context context;

    public TeachersAdapter(@NonNull Context context, List<Teacher> teachers) {
        super(context, 0, teachers);

        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        teachersList = teachers;
    }

    @Nullable
    @Override
    public Teacher getItem(int position) {
        return teachersList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            View view = layoutInflater.inflate(R.layout.teacher_card, parent, false);
            viewHolder = ViewHolder.create((RelativeLayout) view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Teacher item = getItem(position);

        viewHolder.lastName.setText(item.getLastName());
        viewHolder.FaPName.setText(item.getFirstName() + " " +
            item.getPatronymic());
        viewHolder.teacherID.setText(item.getTeacherID());

        return viewHolder.parentLayout;
    }

    private static class ViewHolder {
        private final RelativeLayout parentLayout;
        private final TextView lastName;
        private final TextView FaPName;
        private final TextView teacherID;

        private ViewHolder(RelativeLayout parentLayout, TextView lastName, TextView FaPName, TextView teacherID) {
            this.parentLayout = parentLayout;
            this.lastName = lastName;
            this.FaPName = FaPName;
            this.teacherID = teacherID;
        }

        private static ViewHolder create(RelativeLayout parentLayout) {
            TextView lastName = parentLayout.findViewById(R.id.lastName);
            TextView FaPName = parentLayout.findViewById(R.id.FaPName);
            TextView teacherID = parentLayout.findViewById(R.id.teacherID);

            return new ViewHolder(parentLayout, lastName, FaPName, teacherID);
        }
    }
}