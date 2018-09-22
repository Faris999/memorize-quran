package org.faris.memorizequran;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "org.faris.memorizequran.MESSAGE";
    private static final String REQUEST_URL = "http://staging.quran.com:3000/api/v3/chapters";

    private CustomAdapter adapter;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = this.getSharedPreferences("org.faris.memorizequran.SharedPref", Context.MODE_PRIVATE);
        ListView listView = findViewById(R.id.list_view);
        ArrayList<Surah> surah = new ArrayList<>();
        adapter = new CustomAdapter(this, surah);
        listView.setAdapter(adapter);
        String listJson = sharedPref.getString("listJson", "");
        if(listJson.isEmpty()){
            QueryAsyncTask task = new QueryAsyncTask();
            task.execute(REQUEST_URL);
        } else{
            adapter.clear();

            List<Surah> surahList = QueryUtils.extractFeatureFromJson(listJson);
            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (surahList != null && !surahList.isEmpty()) {
                adapter.addAll(surahList);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_leaderboard:

                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private class QueryAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            String json = QueryUtils.fetchEarthquakeData(urls[0]);
            sharedPref.edit().putString("listJson", json).apply();
            return json;
        }

        @Override
        protected void onPostExecute(String data) {
            // Clear the adapter of previous earthquake data
            adapter.clear();

            List<Surah> surahList = QueryUtils.extractFeatureFromJson(data);
            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (surahList != null && !surahList.isEmpty()) {
                adapter.addAll(surahList);
            }
        }
    }


}
