package com.musicbean.preference;


import android.content.Context;
import android.content.SharedPreferences;

import com.musicbean.App;

public class RecommendPreference {

    private static final String pref_name = "first_time_recommend";

    public static void update(String userId, boolean hasRecommend) {
        SharedPreferences sp = App.getContext().getSharedPreferences(pref_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(userId, hasRecommend);
        editor.apply();
    }

    public static boolean hasRecommend(String userId) {
        SharedPreferences sp = App.getContext().getSharedPreferences(pref_name, Context.MODE_PRIVATE);
        return sp.getBoolean(userId, false);
    }
}
