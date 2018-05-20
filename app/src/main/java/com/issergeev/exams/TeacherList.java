package com.issergeev.exams;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TeacherList {

    @SerializedName("Teachers")
    @Expose
    private ArrayList<Teacher> teachers = null;

    public ArrayList<Teacher> getTeachers() {
        return teachers;
    }

    public void setResults(ArrayList<Teacher> teachers) {
        this.teachers = teachers;
    }
}