package org.faris.memorizequran;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static AppDatabase db;
    public static final String EXTRA_MESSAGE = "org.faris.memorizequran.MESSAGE";
    private static final String REQUEST_URL = "http://staging.quran.com:3000/api/v3/chapters";

    SurahAdapter adapter;
    private SharedPreferences sharedPref;
    private ArrayList<Surah> surah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Default template
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init variables
        ListView listView = findViewById(R.id.list_view);
        surah = new ArrayList<>();
        sharedPref = this.getSharedPreferences("org.faris.memorizequran.SharedPref", Context.MODE_PRIVATE);
        adapter = new SurahAdapter(this, surah);
        listView.setAdapter(adapter);
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "verse").build();

        // Check if we already have list data.
        String listJson = sharedPref.getString("listJson", "");
        if(listJson.isEmpty()){
            QueryAsyncTask task = new QueryAsyncTask();
            task.execute(REQUEST_URL);
        } else{
            adapter.clear();
            List<Surah> surahList = QueryUtils.extractSurahFromJson(listJson);
            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (surahList != null && !surahList.isEmpty()) {
                adapter.addAll(surahList);
            } else {
                // If there isn't a valid response, inform the user.
                Toast.makeText(this, "Cannot load surah list.", Toast.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check if surah is already loaded.
        if (surah.size() != 0) {
            AsyncTask.execute(() -> {
                for (int i = 1; i <= 114; i++) {
                    int checked = 0;
                    for (Verse verse : db.verseDao().getBySurah(i)) {
                        if (verse.memorized) checked++;
                    }
                    int i2 = i;
                    if (checked == surah.get(i - 1).getVersesCount()) {
                        runOnUiThread(() -> adapter.checkCheckBox(i2 - 1, true));
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_leaderboard:
                // Start activity if the user clicked on leaderboard text.
//                startActivity(new Intent(this, LeaderboardActivity.class));
                //TODO Make Leaderboard
                Toast.makeText(this, "Coming soon..", Toast.LENGTH_SHORT).show();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     *
     */
    private class QueryAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            String json = QueryUtils.fetchSurahData(urls[0]);
            sharedPref.edit().putString("listJson", json).apply();
            return json;
        }

        @Override
        protected void onPostExecute(String data) {
            // Clear the adapter of previous earthquake data
            adapter.clear();

            List<Surah> surahList = QueryUtils.extractSurahFromJson(data);
            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (surahList != null && !surahList.isEmpty()) {
                adapter.addAll(surahList);
            }
        }
    }


}
