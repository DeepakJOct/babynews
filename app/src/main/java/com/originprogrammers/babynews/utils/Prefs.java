package com.originprogrammers.babynews.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

public class Prefs {
    public static final String IS_ALREADY_ONLINE = "is_already_online";
    public static final String ONLINE = "online";
    private static SharedPreferences mPrefs;
    private static final String key = "AppPref";

    /**
     * The constant IS_LOGGED.
     */
    public static final String IS_LOGGED = "IsLoggedIn";
    /**
     * The constant PIN_CODE.
     */
    public static final String PIN_CODE = "pincode";

    public static void initPrefs(Context context) {
        if (mPrefs == null) {
            mPrefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        }
    }

    public static SharedPreferences getPreferences() {
        if (mPrefs != null) {
            return mPrefs;
        }
        throw new RuntimeException(
                "Prefs class not correctly instantiated please call Prefs.iniPrefs(context) in the Application class onCreate.");
    }

    public static void logoutUser(Context context) {
        getPreferences().edit().clear().commit();
        getPreferences().edit().clear().apply();
    }

    public static int getInt(final String key, final int defValue) {
        return getPreferences().getInt(key, defValue);
    }

    public static boolean getBoolean(final String key, final boolean defValue) {
        return getPreferences().getBoolean(key, defValue);
    }

    public static String getString(final String key, @Nullable final String defValue) {
        return getPreferences().getString(key, defValue);
    }

    public static void putBoolean(final String key, final boolean value) {
        final SharedPreferences.Editor editor = getPreferences().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }


    public static void putString(final String key, final String value) {
        final SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void clearPref(Context context) {
        getPreferences().edit().clear().apply();
    }
}
