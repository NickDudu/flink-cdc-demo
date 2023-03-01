package com.nick.model.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class Person {
    public String firstName;
    public String lastName;
    public int age;
    public String phone;
    public int id;

    public Person(String firstName,
            String lastName,
            int age,
            String phone,
            int id ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.phone = phone;
        this.id = id;
    }

    public Person (){

    }
}
