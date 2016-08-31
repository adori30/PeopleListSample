package com.adori.personlistsample;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.firebase.client.Firebase;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

/**
 * Created by adria.navarro on 30/8/16.
 */
public class BaseApplication extends Application {

    private static Context mContext;
    private static String mSessionKey;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Firebase.setAndroidContext(mContext);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        mSessionKey = preferences.getString("session_key",  null);
        if (mSessionKey == null) {
            mSessionKey = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("session_key", mSessionKey);
            editor.apply();
        }
    }

    public static Context getAppContext() {
        return mContext;
    }

    public static String getSessionKey() {
        return mSessionKey;
    }
}
