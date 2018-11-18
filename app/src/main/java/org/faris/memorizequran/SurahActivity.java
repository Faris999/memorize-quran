package org.faris.memorizequran;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SurahActivity extends AppCompatActivity {

    private static final String REQUEST_URL = "http://staging.quran.com:3000/api/v3/chapters/id/verses/?limit=50";
    private static final String LOG_TAG = SurahActivity.class.getSimpleName();

    private VerseAdapter mVerseAdapter;
    private SharedPreferences mSharedPref;
    private Intent mIntent;
    private ProgressBar mProgressBar;
    private TextView mPercentTextView;
    private ArrayList<Verse> mVerses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surah);

        //Init variables
        mPercentTextView = findViewById(R.id.surah_text);
        mProgressBar = findViewById(R.id.progressBar2);
        ListView verseView = findViewById(R.id.verse_list);
        mVerses = new ArrayList<>();
        mIntent = getIntent();
        mSharedPref = this.getSharedPreferences("org.faris.memorizequran.SharedPref", Context.MODE_PRIVATE);
        mVerseAdapter = new VerseAdapter(this, mVerses, mIntent.getIntExtra("id", 1));

        //Set adapter for listView.
        verseView.setAdapter(mVerseAdapter);

        //Set title
        setTitle(getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE));

        String verseJson = mSharedPref.getString("verseJson" + mIntent.getIntExtra("id", 1), "");
        if (verseJson.isEmpty()) {
            SurahActivity.QueryAsyncTask task = new SurahActivity.QueryAsyncTask();
            task.execute(REQUEST_URL.replace("id", String.valueOf(mIntent.getIntExtra("id", 1))));
        } else {
            mVerseAdapter.clear();
            List<Verse> verseList = QueryUtils.extractVerseFromJson(verseJson);
            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (verseList != null && !verseList.isEmpty()) {
                mVerseAdapter.addAll(verseList);
            } else {
                // If there isn't a valid response, inform the user.
                Toast.makeText(this, "Cannot load verse list.", Toast.LENGTH_SHORT).show();
            }
        }
        //Check if verses already loaded.
        if (mVerses.size() != 0) {
            AsyncTask.execute(() -> {
                int checked = 0;
                for (Verse verse : MainActivity.db.verseDao().getBySurah(mIntent.getIntExtra("id", 1))) {
                    if (verse.memorized) checked++;
                    runOnUiThread(() -> mVerseAdapter.checkCheckBox(verse.num - 1, verse.memorized));
                }
                int checked2 = checked;
                runOnUiThread(() -> checkCheckBox(checked2));
            });
        }
    }

    void checkCheckBox(int checked) {
        DecimalFormat df = new DecimalFormat("#.##");
        mPercentTextView.setText(df.format(checked * 100.0 / mVerses.size()) + "%");
        mProgressBar.setProgress(checked * 100 / mVerses.size());
    }

    private class QueryAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            String json = QueryUtils.fetchVerseData(urls[0]);
            mSharedPref.edit().putString("verseJson" + mIntent.getIntExtra("id", 1), json).apply();

            return json;
        }

        @Override
        protected void onPostExecute(String data) {
            // Clear the adapter of previous earthquake data
            mVerseAdapter.clear();

            List<Verse> verseList = QueryUtils.extractVerseFromJson(data);
            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (verseList != null && !verseList.isEmpty()) {
                mVerseAdapter.addAll(verseList);
            }
        }
    }
}
