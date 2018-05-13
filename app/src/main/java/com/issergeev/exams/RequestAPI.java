package com.issergeev.exams;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RequestAPI {
    @POST("get_list_of_groups.php")
    @FormUrlEncoded
    Call<List<String>> getGroups(@Field("teacher_id") Integer teacherID);
}