package com.java24hours;

public class GradeBook {

    private String courseName;

    public GradeBook(String name)
    {
        courseName = name;
    }

    public void setCourseName(String name)
    {
        courseName = name;
    }

    public String getCourseName()
    {
        return courseName;
    }

    public void displayMessage()
    {
        System.out.printf("Welcome to the Grade Book for\n%s!\n",getCourseName());
    }

}
