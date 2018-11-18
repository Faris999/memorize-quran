package org.faris.memorizequran;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    public static List<Surah> extractSurahFromJson(String JSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(JSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<Surah> surahs = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(JSON);

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or earthquakes).
            JSONArray surahArray = baseJsonResponse.getJSONArray("chapters");

            // For each earthquake in the earthquakeArray, create an {@link Earthquake} object
            for (int i = 0; i < surahArray.length(); i++) {

                // Get a single earthquake at position i within the list of earthquakes
                JSONObject currentSurah = surahArray.getJSONObject(i);

                // For a given earthquake, extract the JSONObject associated with the
                // key called "properties", which represents a list of all properties
                // for that earthquake.
                String name = currentSurah.getString("name_simple");
                int versesCount = currentSurah.getInt("verses_count");

                surahs.add(new Surah((i + 1) + ". " + name, versesCount));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        Log.v(LOG_TAG, String.valueOf(surahs.size()));
        return surahs;
    }

    public static List<Verse> extractVerseFromJson(String JSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(JSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<Verse> verses = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(JSON);

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or earthquakes).
            JSONArray versesArray = baseJsonResponse.getJSONArray("verses");

            // For each earthquake in the earthquakeArray, create an {@link Earthquake} object
            for (int i = 0; i < versesArray.length(); i++) {

                // Get a single earthquake at position i within the list of earthquakes
                JSONObject currentVerse = versesArray.getJSONObject(i);

                // For a given earthquake, extract the JSONObject associated with the
                // key called "properties", which represents a list of all properties
                // for that earthquake.
                String name = "\u200e" + currentVerse.getInt("verse_number") + ". " + currentVerse.getString("text_madani");

                verses.add(new Verse(name));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        Log.v(LOG_TAG, String.valueOf(verses.size()));
        return verses;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static String fetchSurahData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Return the list of {@link Earthquake}s
        return jsonResponse;
    }

    public static String fetchVerseData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        JSONObject json;
        try {
            json = new JSONObject(jsonResponse);
            JSONObject jsonDeep = new JSONObject(jsonResponse);
            JSONArray verses = json.getJSONArray("verses");

            while (true) {
                int page = jsonDeep.getJSONObject("meta").optInt("next_page", -1);
                if (page == -1) break;
                int id = verses.getJSONObject(0).getInt("chapter_id");
                String newUrl = "http://staging.quran.com:3000/api/v3/chapters/id/verses/?limit=50&page=next_page";
                newUrl = newUrl.replace("id", String.valueOf(id)).replace("next_page", String.valueOf(page));
                String newResponse = makeHttpRequest(createUrl(newUrl));
                jsonDeep = new JSONObject(newResponse);
                JSONArray newVerses = jsonDeep.getJSONArray("verses");
                for (int i = 0; i < newVerses.length(); i++) {
                    verses = verses.put(newVerses.getJSONObject(i));
                }
            }

            json.remove("verses");
            json.put("verses", verses);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }


        // Return the list of {@link Earthquake}s
        return json.toString();
    }

    public static void postLeaderboard(String json, String endpoint) {
        HttpURLConnection conn = null;
        URL url = createUrl("http://farishafiz999.pythonanywhere.com/" + endpoint);
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-type", "application/json");

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(json);
            writer.flush();
            writer.close();
            os.close();
            if (conn.getResponseCode() != 200) {
                AsyncTask.execute(() -> postLeaderboard(json, endpoint));
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static List<User> extractLeaderboardFromJson(String JSON) {
        Log.v(LOG_TAG, JSON);
        if (TextUtils.isEmpty(JSON)) {
            return null;
        }
        List<User> users = new ArrayList<>();
        try {

            JSONObject object = new JSONObject(JSON);

            // Create a JSONArray from the JSON response string
            JSONArray baseJsonResponse = object.getJSONArray("array");

            // For each earthquake in the earthquakeArray, create an {@link Earthquake} object
            for (int i = 0; i < baseJsonResponse.length(); i++) {

                JSONObject currentUser = baseJsonResponse.getJSONObject(i);

                // For a given earthquake, extract the JSONObject associated with the
                // key called "properties", which represents a list of all properties
                // for that earthquake.
                String id = currentUser.getString("id");
                String name = currentUser.getString("name");
                int score = currentUser.getInt("score");

                users.add(new User(id, name, score));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        Log.v(LOG_TAG, String.valueOf(users.size()));
        return users;
    }
}
