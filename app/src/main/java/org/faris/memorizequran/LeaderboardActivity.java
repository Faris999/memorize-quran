package org.faris.memorizequran;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    private static final String REQUEST_URL = "http://farishafiz999.pythonanywhere.com/get";

    private LeaderboardAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        setTitle("Leaderboard");
        ListView mLeaderboardList = findViewById(R.id.list_leaderboard);
        ArrayList<User> accounts = new ArrayList<>();
        mAdapter = new LeaderboardAdapter(this, accounts);
        mLeaderboardList.setAdapter(mAdapter);
        QueryAsyncTask task = new QueryAsyncTask();
        task.execute(REQUEST_URL);
    }

    private class QueryAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            return QueryUtils.fetchSurahData(urls[0]);
        }

        @Override
        protected void onPostExecute(String data) {
            // Clear the adapter of previous earthquake data
            mAdapter.clear();

            List<User> users = QueryUtils.extractLeaderboardFromJson(data);
            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (users != null && !users.isEmpty()) {
                mAdapter.addAll(users);
            }
        }
    }

}
