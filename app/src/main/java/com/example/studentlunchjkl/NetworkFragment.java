package com.example.studentlunchjkl;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.studentlunchjkl.data.Meal;
import com.example.studentlunchjkl.data.RMenu;
import com.example.studentlunchjkl.data.Recipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Much of this is from the android tutorials
 * Implementation of headless Fragment that runs an AsyncTask to fetch data from the network.
 */
public class NetworkFragment extends Fragment {
    public static final String TAG = "NetworkFragment";

    private static final String URL_KEY = "UrlKey";

    private DownloadCallback<RMenu> callback;
    private DownloadTask downloadTask;
    private String urlString;

    /**
     * Static initializer for NetworkFragment that sets the URL of the host it will be downloading
     * from.
     */
    public static NetworkFragment getInstance(FragmentManager fragmentManager, String url) {
        NetworkFragment networkFragment = new NetworkFragment();
        Bundle args = new Bundle();
        args.putString(URL_KEY, url);
        networkFragment.setArguments(args);
        fragmentManager.beginTransaction().add(networkFragment, TAG).commit();
        return networkFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        urlString = getArguments().getString(URL_KEY);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Host Activity will handle callbacks from task.
        callback = (DownloadCallback<RMenu>) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Clear reference to host Activity to avoid memory leak.
        callback = null;
    }

    @Override
    public void onDestroy() {
        // Cancel task when Fragment is destroyed.
        cancelDownload();
        super.onDestroy();
    }

    /**
     * Start non-blocking execution of DownloadTask.
     */
    public void startDownload(ArrayList<String> urls) {
        cancelDownload();
        downloadTask = new DownloadTask(callback);
        downloadTask.execute(urls);
    }

    /**
     * Cancel (and interrupt if necessary) any ongoing DownloadTask execution.
     */
    public void cancelDownload() {
        if (downloadTask != null) {
            downloadTask.cancel(true);
        }
    }

    /**
     * Implementation of AsyncTask designed to fetch data from the network.
     */
    private class DownloadTask extends AsyncTask<ArrayList<String>, Integer, DownloadTask.Result> {

        private DownloadCallback<RMenu> callback;
        private  final String TAG = DownloadTask.class.getSimpleName();


        DownloadTask(DownloadCallback<RMenu> callback) {
            setCallback(callback);
        }

        void setCallback(DownloadCallback<RMenu> callback) {
            this.callback = callback;
        }

        /**
         * Wrapper class that serves as a union of a result value and an exception. When the download
         * task has completed, either the result value or exception can be a non-null value.
         * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
         */
        class Result {
            public RMenu menu;
            public String resultValue;
            public Exception exception;
            public Result(RMenu resultValue) {
                this.menu = resultValue;
            }
            public Result(Exception exception) {
                this.exception = exception;
            }
        }



        /**
         * Cancel background network operation if we do not have network connectivity.
         */
        @Override
        protected void onPreExecute() {
            if (callback != null) {
                NetworkInfo networkInfo = callback.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected() ||
                        (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                                && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                    // If no connectivity, cancel task and update Callback with null data.
                    Log.d(TAG,"ei yhteyttä");
                    callback.updateFromDownload(null);
                    cancel(true);
                }
            }
        }

        /**
         * Defines work to perform on the background thread.
         */
        @Override
        protected DownloadTask.Result doInBackground(ArrayList<String>... lists) {
            ArrayList<String> urls = lists[0];
            Result result = null;
            if (!isCancelled() && urls != null && urls.size() > 0) {
                RMenu menu = new RMenu();
                for(int restaurantI = 3; restaurantI < urls.size(); restaurantI++) {
                    Meal meal;
                    String urlString = urls.get(0) + urls.get(1) + urls.get(restaurantI);
                    try {
                        URL url = new URL(urlString);
                        String resultString = downloadUrl(url);
                        meal = new Meal(MainActivity.getRestaurantName(urls.get(restaurantI)));
                        meal.setAsRestaurantName();
                        menu.addMeal(meal);
                        if (resultString != null) {
                            menu.setFromJSON(resultString);
                            for (int i = 0; i < menu.getMealCount(); i++) {
                                meal = menu.getMeal(i);
                                for (int j = 0; j < meal.getRecipeCount(); j++) {
                                    Recipe recipe = meal.getRecipe(j);
                                    int recipeId = recipe.getRecipeId();
                                    Log.d(TAG, " " + recipeId);
                                    if (recipeId > 0) {
                                        urlString = urls.get(0) + urls.get(2) + recipeId;
                                        url = new URL(urlString);
                                        resultString = downloadUrl(url);
                                        recipe.setIngredientsFromJson(resultString);
                                    }
                                }
                            }
                        } else {
                            throw new IOException("No response received.");
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "virhe " + e.getMessage());
                        result = new Result(e);
                    }
                }
                result = new Result(menu);
            }
            return result;
        }

        /**
         * Given a URL, sets up a connection and gets the HTTP response body from the server.
         * If the network request is successful, it returns the response body in String form. Otherwise,
         * it will throw an IOException.
         */
        private String downloadUrl(URL url) throws IOException {

            InputStream stream = null;
            HttpsURLConnection connection = null;
            String result = "kissa";
            try {
                connection = (HttpsURLConnection) url.openConnection();
                // Timeout for reading InputStream arbitrarily set to 3000ms.
                connection.setReadTimeout(3000);
                // Timeout for connection.connect() arbitrarily set to 3000ms.
                connection.setConnectTimeout(3000);
                // For this use case, set HTTP method to GET.

                connection.setRequestMethod("GET");
                // Already true by default but setting just in case; needs to be true since this request
                // is carrying an input (response) body.
                connection.setDoInput(true);

                // Open communications link (network traffic occurs here).
                connection.connect();
                publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS);
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw new IOException("HTTP error code: " + responseCode);
                }
                // Retrieve the response body as an InputStream.
                stream = connection.getInputStream();
                publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0);
                if (stream != null) {
                    // Converts Stream to String with max length of 500.
                    result = readStream(stream, 50000);
                }
            } catch (Exception e){ Log.d(TAG,e.getMessage());}
            finally {
                // Close Stream and disconnect HTTPS connection.
                if (stream != null) {
                    stream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }

        /**
         * Converts the contents of an InputStream to a String.
         */
        public String readStream(InputStream stream, int maxReadSize)
                throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] rawBuffer = new char[maxReadSize];
            int readSize;
            StringBuffer buffer = new StringBuffer();
            while (((readSize = reader.read(rawBuffer)) != -1) && maxReadSize > 0) {
                if (readSize > maxReadSize) {
                    readSize = maxReadSize;
                }
                buffer.append(rawBuffer, 0, readSize);
                maxReadSize -= readSize;
            }
            return buffer.toString();
        }



        /**
         * Updates the DownloadCallback with the result.
         */
        @Override
        protected void onPostExecute(Result result) {
            Log.d(TAG, " " + callback);
            if (result != null && callback != null) {
                if (result.exception != null) {
                    //callback.updateFromDownload(result.exception.getMessage());
                } else if (result.menu != null) {
                    callback.updateFromDownload(result.menu);
                }
                callback.finishDownloading();
            }
        }

        /**
         * Override to add special behavior for cancelled AsyncTask.
         */
        @Override
        protected void onCancelled(Result result) {
        }
    }



}

