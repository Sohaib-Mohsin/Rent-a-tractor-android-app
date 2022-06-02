package com.tractor.rentatractorapp.Helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class Current_Login_Status {

    public static void Save_Login(Context context, String login_status) {
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences("Login_Status", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("Current_Status", login_status);
        editor.apply();
    }

    public static String get_login(Context context) {
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences("Login_Status", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Current_Status", "none");
    }
}
