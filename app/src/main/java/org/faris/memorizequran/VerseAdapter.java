package org.faris.memorizequran;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class VerseAdapter extends ArrayAdapter<Verse> {

    private ArrayList<Verse> arrayList;
    private SparseBooleanArray selectedItemIds;
    private int surah;


    VerseAdapter(@NonNull Context context, ArrayList<Verse> arrayList, int surah) {
        super(context, 0, arrayList);
        this.arrayList = arrayList;
        this.surah = surah;
        selectedItemIds = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView;
        if (convertView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_view, parent, false);
        } else {
            listItemView = convertView;
        }
        CheckBox checkBox = listItemView.findViewById(R.id.checkbox);
        TextView surahText = listItemView.findViewById(R.id.surah_text);
        surahText.setText(arrayList.get(position).getText());
        checkBox.setChecked(selectedItemIds.get(position));
        checkBox.setOnClickListener(v -> {
            checkCheckBox(position, !selectedItemIds.get(position));
            int checked = 0;
            for (int i = 0; i < arrayList.size(); i++) {
                if (selectedItemIds.get(i)) checked++;
            }
            ((SurahActivity) getContext()).checkCheckBox(checked);
            AsyncTask.execute(() -> {
                for (Verse verse : MainActivity.db.verseDao().getAll()) {
                    if (verse.num == position + 1 && verse.surah == surah) {
                        MainActivity.db.verseDao().update(surah, position + 1, selectedItemIds.get(position));
                        return;
                    }
                }
                MainActivity.db.verseDao().insert(new Verse(surah, position + 1, selectedItemIds.get(position)));
            });

        });
        return listItemView;
    }

    void checkCheckBox(int position, boolean value) {
        if (value)
            selectedItemIds.put(position, true);
        else
            selectedItemIds.delete(position);

        notifyDataSetChanged();
    }
}
