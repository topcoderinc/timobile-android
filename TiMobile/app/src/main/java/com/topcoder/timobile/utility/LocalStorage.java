package com.topcoder.timobile.utility;

import android.content.Context;
import android.content.SharedPreferences;
import com.topcoder.timobile.baseclasses.BaseApplication;

/**
 * Utility wrapper around {@link SharedPreferences}
 */
public class LocalStorage {

  public static final String CURRENT_PRIZE_ID = "current_prize_id_key";
  private static final LocalStorage instance = new LocalStorage();
  private static final String PREFS_NAME = "com.topcoder.timobile.SharedPrefs";
  private final SharedPreferences preferences;

  private LocalStorage() {
    preferences = BaseApplication.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
  }

  public static LocalStorage getInstance() {
    return instance;
  }

  public void storeData(String key, String value) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(key, value);
    editor.apply();
  }

  public void storeData(String key, long value) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putLong(key, value);
    editor.apply();
  }

  public void storeData(String key, int value) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putInt(key, value);
    editor.apply();
  }

  public void storeData(String key, boolean value) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putBoolean(key, value);
    editor.apply();
  }

  public void storeData(String key, float value) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putFloat(key, value);
    editor.apply();
  }

  public String getString(String key) {
    return preferences.getString(key, null);
  }

  public float getFloat(String key) {
    return preferences.getFloat(key, 0.0f);
  }

  public long getLong(String key) {
    return preferences.getLong(key, 0);
  }

  public int getInt(String key) {
    return preferences.getInt(key, 0);
  }

  public boolean getBoolean(String key, boolean defValue) {
    return preferences.getBoolean(key, defValue);
  }

  public void clear() {
    preferences.edit().clear().apply();
  }
}
