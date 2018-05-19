package com.issergeev.exams;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RequestAPI {
    @POST("get_list_of_groups.php")
    @FormUrlEncoded
    Call<List<String>> getGroups(@Field("teacher_id") String teacherID);

    @POST("get_list_of_students.php")
    @FormUrlEncoded
    Call<StudentList> getStudentList(@Field("group_number") String groupNumber);

    @POST("get_list_of_exams.php")
    @FormUrlEncoded
    Call<List<String>> getExams(@Field("teacher_ID") String teacherID);

    @POST("get_list_of_questions.php")
    @FormUrlEncoded
    Call<QuestionList> getQuestions(@Field("exam_name") String examName);

    @POST("get_list_of_questions_student.php")
    @FormUrlEncoded
    Call<QuestionList> getQuestionsStudent(@Field("exam_name") String examName);

    @POST("get_list_of_exams_students.php")
    @FormUrlEncoded
    Call<List<String>> getExamsStudents(@Field("group_number") String groupNumber);

    @POST("get_list_of_results.php")
    @FormUrlEncoded
    Call<ResultList> getResults(@Field("student_id_number") String studentIDNumber);

    @POST("get_list_of_results_teacher.php")
    @FormUrlEncoded
    Call<ResultList> getResultsTeacher(@Field("teacher_id") String teacher_id, @Field("exam_name") String examName);
}