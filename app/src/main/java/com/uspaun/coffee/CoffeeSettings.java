package com.uspaun.coffee;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by root on 28.07.15.
 */
public class CoffeeSettings {
    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_PERCENT = "percent";
    private static final String APP_PREFERENCES_RATE = "rate";
    private static final String APP_PREFERENCES_ID = "id";
    private static SharedPreferences mSettings;
    private static SharedPreferences.Editor editor = mSettings.edit();

    public static void setRate(float Rate)
    {
        editor.putFloat(APP_PREFERENCES_RATE, Rate);
        editor.apply();
    }

    public static float getRate()
    {
        return mSettings.getFloat(APP_PREFERENCES_RATE, 0);
    }

    public static void setPercent(float Percent)
    {
        editor.putFloat(APP_PREFERENCES_PERCENT, Percent);
        editor.apply();
    }

    public static float getPercent()
    {
        return mSettings.getFloat(APP_PREFERENCES_PERCENT, 0);
    }

    public static void setUser(int ID, String token)
    {
        editor.putInt(APP_PREFERENCES_ID, ID);
        editor.apply();
    }
}
