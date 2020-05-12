package com.example.ownmap.activity.java;

public class Person {
    int id;
    String name;
    String phone;
    String studentNumber;
    public Person(int id,String name,String phone,String studentNumber){
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.studentNumber = studentNumber;
    }
    public Person(String name,String phone,String studentNumber){
        this.name = name;
        this.phone = phone;
        this.studentNumber = studentNumber;
    }
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public int getId() {
        return id;
    }

    public String getStudentNumber() {
        return studentNumber;
    }
}
