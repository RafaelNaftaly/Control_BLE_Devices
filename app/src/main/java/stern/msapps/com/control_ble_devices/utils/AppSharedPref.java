package stern.msapps.com.control_ble_devices.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.Set;


/**
 * Created by Rafael on 3/7/19.
 */

public class AppSharedPref {

    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;
    private static AppSharedPref appSharedPrefInstance;

    private AppSharedPref() {

    }


    public static AppSharedPref getInstance(Context context) {
        if (appSharedPrefInstance == null) {
            appSharedPrefInstance = new AppSharedPref();
        }

        AppSharedPref.settings = context.getSharedPreferences(Constants.APP_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        return appSharedPrefInstance;
    }


    public void savePrefString(String key, String val) {
        editor.putString(key, val).commit();
    }


    /**
     * @param key
     * @param key2
     * @return
     */
    public String getPrefString(String key, @Nullable String key2) {

        if (key2 != null) {
            return settings.getString(key, key2);
        } else {
            return settings.getString(key, "");
        }

    }

    public void savePrefBollean(String key, boolean bool) {

        editor.putBoolean(key, bool);

    }

    public boolean getPrefBoolean(String key, boolean bool) {

        return settings.getBoolean(key, bool);
    }

    public String getPrefString(String key) {


        return settings.getString(key, "");


    }

    public void savePrefInt(String key, int val) {
        editor.putInt(key, val).commit();

    }

    public void deleteAll() {
        editor.remove(Constants.APP_SHARED_PREF_NAME).commit();
    }


    public void savePrefLong(String key, long val) {
        editor.putLong(key, val).commit();
    }

    public void savePrefSet(String key, Set<String> strings) {

        editor.remove(key);
        editor.apply();

        editor.putStringSet(key, strings).apply();

    }

    public Set<String> getPrefSet(String key) {

        return settings.getStringSet(key, null);
    }

    public void savePrefFloat(String key, float val) {
        editor.putFloat(key, val).commit();
    }

    public float getSharedFloat(String key) {
        return settings.getFloat(key, 0.0f);
    }


    public long getPrefLong(String key) {
        return settings.getLong(key, 0);
    }

    public int getPrefInt(String key) {
        return settings.getInt(key, -1);
    }

    public void deleteAllSharedPref() {
        editor.clear().commit();
    }


}
