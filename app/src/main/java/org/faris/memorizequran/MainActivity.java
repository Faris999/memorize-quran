package org.faris.memorizequran;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<String> surah = new ArrayList<>();
        surah.add("Al-Fatihah");
        surah.add("Al-Baqarah");
        surah.add("An-Naba");

        ListView listView = findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_view, surah);
        listView.setAdapter(adapter);
    }
}
