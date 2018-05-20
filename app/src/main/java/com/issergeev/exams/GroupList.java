package com.issergeev.exams;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GroupList {

    @SerializedName("Groups")
    @Expose
    private ArrayList<Group> groups = null;

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void setResults(ArrayList<Group> groups) {
        this.groups = groups;
    }
}