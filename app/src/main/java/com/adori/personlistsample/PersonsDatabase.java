package com.adori.personlistsample;

/**
 * Created by adria.navarro on 29/8/16.
 */
public interface PersonsDatabase {

    void writeNewPerson(Person person);
    void editPerson(String key, Person person);
    void deletePerson(String key);
}
