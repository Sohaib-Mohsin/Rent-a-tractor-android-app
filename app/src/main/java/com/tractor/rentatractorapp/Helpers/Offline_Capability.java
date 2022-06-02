package com.tractor.rentatractorapp.Helpers;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Offline_Capability extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}
