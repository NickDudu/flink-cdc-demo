package com.nick.model.item;

public class Person {
    public String customerId;
    public String sex;
    public int age;
    public int birthMonth;
    public int birthDay;
    public int openDays;
    public int totalPoints;
    public String province;
    public String membership;

    public Person(String customerId,
            String sex,
            int age,
            int birthMonth,
            int birthDay,
            int openDays,
            int totalPoints,
            String province,
            String membership) {
        this.customerId = customerId;
        this.sex = sex;
        this.age = age;
        this.birthMonth = birthMonth;
        this.birthDay = birthDay;
        this.openDays = openDays;
        this.totalPoints = totalPoints;
        this.province = province;
        this.membership = membership;
    }

}
