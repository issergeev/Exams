package com.issergeev.exams;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Student {

    @SerializedName("Student_ID")
    @Expose
    private String studentID;

    @SerializedName("Student_FirstName")
    @Expose
    private String studentFirstName;

    @SerializedName("Student_LastName")
    @Expose
    private String studentLastName;

    @SerializedName("Student_Patronymic")
    @Expose
    private String studentPatronymic;

    @SerializedName("Group_Number")
    @Expose
    private String groupNumber;

    @SerializedName("Email")
    @Expose
    private String email;

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getStudentFirstName() {
        return studentFirstName;
    }

    public void setStudentFirstName(String studentFirstName) {
        this.studentFirstName = studentFirstName;
    }

    public String getStudentLastName() {
        return studentLastName;
    }

    public void setStudentLastName(String studentLastName) {
        this.studentLastName = studentLastName;
    }

    public String getStudentPatronymic() {
        return studentPatronymic;
    }

    public void setStudentPatronymic(String studentPatronymic) {
        this.studentPatronymic = studentPatronymic;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}