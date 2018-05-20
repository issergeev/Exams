package com.issergeev.exams;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.io.Serializable;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupsFragment extends Fragment {
    private RelativeLayout parentView, noGroupsLayout;
    private ListView groupList;
    private ProgressBar progressBar;

    private GroupsAdapter adapter;
    private ArrayList<Group> groups;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.groups_fragment, container, false);

        parentView = (RelativeLayout) getActivity().findViewById(R.id.rootLayout);
        noGroupsLayout = (RelativeLayout) rootView.findViewById(R.id.noGroups);
        groupList = rootView.findViewById(R.id.groupsList);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        groups = new ArrayList<Group>(10);

        new LoadGroups().execute();

        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putString("groupNumber", adapter.getItem(i).getGroupNumber());
                AllStudentsFragment studentsFragment = new AllStudentsFragment();
                studentsFragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.groupFragment, studentsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return rootView;
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadGroups extends AsyncTask<Void, Void, Void> {
        private final String BASE_URL = "http://exams-online.online/";

        @Override
        protected Void doInBackground(Void... voids) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RequestAPI api = retrofit.create(RequestAPI.class);

            final Call<GroupList> students = api.getAllGroups();
            students.enqueue(new Callback<GroupList>() {
                @Override
                public void onResponse(Call<GroupList> call, Response<GroupList> response) {
                    progressBar.setVisibility(View.GONE);

                    try {
                        groups = response.body().getGroups();
                        adapter = new GroupsAdapter(parentView.getContext(), groups);
                        groupList.setAdapter(adapter);

                        if (groups.size() == 0) {
                            noGroupsLayout.setVisibility(View.VISIBLE);
                        }
                    } catch (NullPointerException e) {
                        Snackbar.make(parentView, R.string.unknown_error, Snackbar.LENGTH_LONG).show();

                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<GroupList> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(parentView, R.string.unknown_error, Snackbar.LENGTH_LONG).show();
                }
            });

            return null;
        }
    }
}