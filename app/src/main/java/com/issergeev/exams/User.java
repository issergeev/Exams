package com.issergeev.exams;

public class User {
    private String FirstName;
    private String LastName;
    private String StudentID;
    private String Login;
    private String Password;

    public User (String FirstName, String LastName, String StudentID, String Login, String Password) {
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.StudentID = StudentID;
        this.Login = Login;
        this.Password = Password;
    }

    public String getFirstName() {
        return FirstName;
    }

    public String getLastName() {
        return LastName;
    }

    public String getStudentID() {
        return StudentID;
    }

    public String getLogin() {
        return Login;
    }

    public String getPassword() {
        return Password;
    }
}