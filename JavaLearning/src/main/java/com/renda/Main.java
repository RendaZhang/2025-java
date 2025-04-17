package com.renda;

import com.renda.demo.Person;
import com.renda.demo.Student;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Student student = new Student();
        student.setName("Renda");
        student.setAge(22);
        student.setStudentId("123");

        Person person = student;
        person.printInfo();
        System.out.println(person);
    }
}


