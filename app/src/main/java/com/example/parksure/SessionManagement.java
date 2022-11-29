package com.example.parksure;

import android.content.Context;
        import android.content.SharedPreferences;

public class SessionManagement {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "session";
    String SESSION_KEY = "session_user";

    public SessionManagement(Context context){
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(User user){
        //save session of user whenever user is logged in
        long id = user.getId();

        editor.putLong(SESSION_KEY,id).commit();
    }

    public long getSession(){
        //return user id whose session is saved
        return sharedPreferences.getLong(SESSION_KEY, -1);
    }

    public void removeSession(){
        editor.putLong(SESSION_KEY,-1).commit();
    }
}