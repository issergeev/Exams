package com.issergeev.exams;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class GroupsAdapter extends ArrayAdapter<Group> {
    private LayoutInflater layoutInflater;

    private List<Group> groupList;
    Context context;

    public GroupsAdapter(@NonNull Context context, List<Group> groups) {
        super(context, 0, groups);

        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        groupList = groups;
    }

    @Nullable
    @Override
    public Group getItem(int position) {
        return groupList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            View view = layoutInflater.inflate(R.layout.group_card, parent, false);
            viewHolder = ViewHolder.create((RelativeLayout) view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Group item = getItem(position);

        viewHolder.groupNumber.setText(item.getGroupNumber());

        return viewHolder.parentLayout;
    }

    private static class ViewHolder {
        private final RelativeLayout parentLayout;
        private final TextView groupNumber;

        private ViewHolder(RelativeLayout parentLayout, TextView groupNumber) {
            this.parentLayout = parentLayout;
            this.groupNumber = groupNumber;
        }

        private static ViewHolder create(RelativeLayout parentLayout) {
            TextView groupNumber = parentLayout.findViewById(R.id.groupNumber);

            return new ViewHolder(parentLayout, groupNumber);
        }
    }
}