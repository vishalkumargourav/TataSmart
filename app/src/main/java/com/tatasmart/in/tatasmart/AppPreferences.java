package com.tatasmart.in.tatasmart;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by windows 8 on 07-03-2016.
 */
public class AppPreferences extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}