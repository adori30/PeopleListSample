package com.adori.personlistsample;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by adria.navarro on 29/8/16.
 */
public class PersonsFirebaseDatabase implements PersonsDatabase {

    private static PersonsFirebaseDatabase mInstance;

    private DatabaseReference mDatabase;
    private String mSessionKey;

    public static PersonsDatabase getInstance() {
        if (mInstance != null) {
            return mInstance;
        }
        mInstance = new PersonsFirebaseDatabase();
        return mInstance;
    }

    private PersonsFirebaseDatabase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("persons").keepSynced(true);

        mSessionKey = BaseApplication.getSessionKey();
    }

    @Override
    public void writeNewPerson(Person person) {
        String key = mDatabase.push().getKey();
        mDatabase.child("persons").child(mSessionKey)
                .child(key).setValue(person);
    }

    @Override
    public void editPerson(String key, Person person) {
        mDatabase.child("persons").child(mSessionKey)
                .child(key).setValue(person);
    }

    @Override
    public void deletePerson(String key) {
        mDatabase.child("persons").child(mSessionKey)
                .child(key).removeValue();
    }
}
