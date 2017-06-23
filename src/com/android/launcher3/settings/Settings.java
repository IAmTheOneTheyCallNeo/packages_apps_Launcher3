package com.android.launcher3.settings;

import android.content.SharedPreferences;

import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.Utilities;

public class Settings implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String KEY_PREF_ICON_PACK_PACKAGE = "pref_iconPackPackage";

    private static Settings instance;
    private Launcher mLauncher;

    private Settings(Launcher launcher) {
        mLauncher = launcher;
        SharedPreferences prefs = Utilities.getPrefs(launcher);
        prefs.registerOnSharedPreferenceChangeListener(this);
        init(prefs);
    }

    public static void init(Launcher launcher) {
        instance = new Settings(launcher);
    }

    public static Settings getInstance() {
        return instance;
    }

    private void init(SharedPreferences prefs) {
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.startsWith("pref_")) {
            switch (key) {
                case KEY_PREF_ICON_PACK_PACKAGE:
                default:
                    mLauncher.reloadAll();
            }
        }
    }
}
