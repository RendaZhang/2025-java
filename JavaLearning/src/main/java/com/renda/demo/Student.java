package com.renda.demo;

public class Student implements Person{
    private String name;
    private int age;
    private String studentId;


    @Override
    public void printInfo() {
        System.out.println("name: " + name + ", age: " + age + ", studentId: " + studentId);
    }

    @Override
    public String toString() {
        return name + ", " + age + ", " + studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
