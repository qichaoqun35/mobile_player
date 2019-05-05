package com.example.qichaoqun.mobileplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SetSharedPreference {

    public static void setPlayerMode(Context context,int playMode){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "playMode",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("playMode",playMode);
        editor.commit();
    }

    public static int getPlayerMode(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "playMode",Context.MODE_PRIVATE);
        int playMode = sharedPreferences.getInt("playMode",0);
        return playMode;
    }

}
