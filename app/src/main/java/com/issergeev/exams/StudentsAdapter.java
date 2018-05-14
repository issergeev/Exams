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

public class StudentsAdapter extends ArrayAdapter<Student> {
    private LayoutInflater layoutInflater;

    private List<Student> studentList;
    Context context;

    public StudentsAdapter(@NonNull Context context, List<Student> students) {
        super(context, 0, students);

        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        studentList = students;
    }

    @Nullable
    @Override
    public Student getItem(int position) {
        return studentList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            View view = layoutInflater.inflate(R.layout.students_card, parent, false);
            viewHolder = ViewHolder.create((RelativeLayout) view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Student item = getItem(position);

        viewHolder.fullName.setText(item.getStudentPatronymic() + "\n"
                + item.getStudentFirstName() + " "
                + item.getStudentLastName());
        if (item.getEmail() != null) {
            viewHolder.emailAddress.setText(item.getEmail());
        } else {
            viewHolder.emailAddress.setText("No email address added");
        }
        viewHolder.photo.setImageDrawable(parent.getResources().getDrawable(R.drawable.exams_icon));

        return viewHolder.parentLayout;
    }

    private static class ViewHolder {
        private final RelativeLayout parentLayout;
        private ImageView photo;
        private final TextView fullName;
        private final TextView emailAddress;

        private ViewHolder(RelativeLayout parentLayout, ImageView photo, TextView fullName, TextView emailAddress) {
            this.parentLayout = parentLayout;
            this.photo = photo;
            this.fullName = fullName;
            this.emailAddress = emailAddress;
        }

        private static ViewHolder create(RelativeLayout parentLayout) {
            ImageView photo = parentLayout.findViewById(R.id.photo);
            TextView fullName = parentLayout.findViewById(R.id.fullName);
            TextView email = parentLayout.findViewById(R.id.email);

            return new ViewHolder(parentLayout, photo, fullName, email);
        }
    }
}