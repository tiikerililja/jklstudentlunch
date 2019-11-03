package com.example.studentlunchjkl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            //setPreferencesFromResource(R.xml.root_preferences, rootKey);

            Context context = getPreferenceManager().getContext();
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);



            /*ListPreference restaurantPreference = new ListPreference(context);
            restaurantPreference.setKey("chosen_restaurant");
            restaurantPreference.setTitle(R.string.restaurant_choosing);
            restaurantPreference.setEntries(R.array.restaurants);
            restaurantPreference.setEntryValues(R.array.restaurants_values);*/

            MultiSelectListPreference listPref = new MultiSelectListPreference(context);
            listPref.setKey("chosen_restaurants");
            listPref.setTitle(R.string.restaurant_choosing);
            listPref.setEntries(R.array.restaurants);
            listPref.setEntryValues(R.array.restaurants_values);

            EditTextPreference allergiesPreference = new EditTextPreference(context);
            allergiesPreference.setKey("saved_allergies");
            allergiesPreference.setTitle(R.string.allergies);
            allergiesPreference.setSummary(sharedPref.getString("saved_allergies", ""));



            //screen.addPreference(restaurantPreference);
            screen.addPreference(listPref);
            screen.addPreference(allergiesPreference);

            setPreferenceScreen(screen);
        }
    }
}