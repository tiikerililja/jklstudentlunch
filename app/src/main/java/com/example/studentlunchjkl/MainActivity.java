package com.example.studentlunchjkl;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.studentlunchjkl.data.Meal;
import com.example.studentlunchjkl.data.RMenu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

/**
 * Class for mainActivity, defines the main functionality of the app
 */
public class MainActivity extends AppCompatActivity implements DownloadCallback<RMenu> {

    public static final String EXTRA_RESTAURANT_INFO = "com.example.studentlunchjkl.RESTAURANT_INFO";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String date;
    private String urlStartSemma = "https://www.semma.fi/api/restaurant/menu/";

    private ArrayList<Meal> myDataset = new ArrayList<Meal>();

    // Keep a reference to the NetworkFragment, which owns the AsyncTask object
    // that is used to execute network ops.
    private NetworkFragment networkFragment;

    // Boolean telling us whether a download is in progress, so we don't trigger overlapping
    // downloads with consecutive button clicks.
    private boolean downloading = false;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Date d = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
        date = dateFormat.format(d);
        Log.d(TAG, date);

        networkFragment = NetworkFragment.getInstance(getSupportFragmentManager(), urlStartSemma);

        recyclerView = findViewById(R.id.my_recycler_view);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);

        // use this setting to improve performance if you know that changes
        // in content not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MealListAdapter(myDataset, getResources(), this);
        recyclerView.setAdapter(mAdapter);




    }


    /**
     * Checks chosen restaurants from settings and  starts the download for menuinfo
     */
    private void startDownload() {
        myDataset.clear();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> restaurantCodes = sharedPref.getStringSet("chosen_restaurants", null);
        ArrayList urlParts = new ArrayList();
        urlParts.add(urlStartSemma);
        urlParts.add("day?date=" + date + "&language=fi&restaurantPageId=");
        urlParts.add("recipe?language=fi&recipeId=");
        if(!restaurantCodes.isEmpty())
            for(String restaurantCode: restaurantCodes) {
                urlParts.add(restaurantCode);
            }
        networkFragment.startDownload(urlParts);
    }

    /**
     * Called when download is complete. Calls checkallergies on each meal and notifies the listView
     * of the changed data
     * @param result data from download
     */
    @Override
    public void updateFromDownload(RMenu result) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String allergies = sharedPref.getString("saved_allergies","");
        String[] allergyList = allergies.split(",");
        //TODO: BUG checks everything forbidden if allergies is empty
        Meal meal;
        for(int i = 0; i < result.getMealCount(); i++){
            meal = result.getMeal(i);
            meal.checkAllergies(allergyList);
            myDataset.add(meal);
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Gives the restaurant names that corresponds to each code
     * @param chosen_restaurant restaurant code
     * @return restaurant name
     */
    public static String getRestaurantName(String chosen_restaurant) {
        switch (chosen_restaurant){
            case "207735":
                return "Piato";
            case "207659":
                return "Maija";
            case "207190":
                return "Uno";
            case "206838":
                return "Rentukka";
            case "207038":
                return "Kvarkki";
            case "207103":
                return "Ylistö";
            case "207272":
                return "Lozzi";
            case "207483":
                return "Syke";
            case "207412":
                return "Tilia";
            default:
                return "Error";


        }
    }


    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    /**
     * Called when options item is selected, calls the correct method
     * @param item what is selected
     * @return same as super method
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_update:
                startDownload();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch (progressCode) {
            // You can add UI behavior for progress updates here.
            case Progress.ERROR:

                break;
            case Progress.CONNECT_SUCCESS:

                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:

                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:

                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:

                break;
        }
    }

    public void openSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void finishDownloading() {
        downloading = false;
        if (networkFragment != null) {
            networkFragment.cancelDownload();
        }
    }
}
