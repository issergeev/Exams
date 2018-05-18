package com.issergeev.exams;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResultList {

    @SerializedName("Results")
    @Expose
    private ArrayList<Result> results = null;

    public ArrayList<Result> getResults() {
        return results;
    }

    public void setResults(ArrayList<Result> results) {
        this.results = results;
    }
}