package org.faris.memorizequran;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SurahActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surah);

        Intent intent = getIntent();

        setTitle(intent.getStringExtra(MainActivity.EXTRA_MESSAGE) + " " + intent.getBooleanExtra("selected", false));
    }
}
