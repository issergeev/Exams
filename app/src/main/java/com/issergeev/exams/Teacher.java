package com.issergeev.exams;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Teacher {

    @SerializedName("Teacher_ID")
    @Expose
    private String teacherID;

    @SerializedName("Teacher_FirstName")
    @Expose
    private String firstName;

    @SerializedName("Teacher_LastName")
    @Expose
    private String lastName;

    @SerializedName("Teacher_Patronymic")
    @Expose
    private String patronymic;

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }
}