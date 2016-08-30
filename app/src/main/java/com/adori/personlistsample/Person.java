package com.adori.personlistsample;

/**
 * Created by adria.navarro on 29/8/16.
 */
public class Person {

    public String id;

    public String firstName;
    public String lastName;
    public String dob;
    public int zipCode;

    public Person() {

    }

    public Person(String firstName, String lastName, String dob, int zipCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.zipCode = zipCode;
    }
}
