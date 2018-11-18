package org.faris.memorizequran;

import android.content.Context;
import android.content.Intent;
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

/**
 * Created by Faris on 13/04/2018.
 */

public class SurahAdapter extends ArrayAdapter<Surah> {

    private ArrayList<Surah> arrayList;
    private SparseBooleanArray selectedItemIds;

    SurahAdapter(@NonNull Context context, ArrayList<Surah> arrayList) {
        super(context, 0, arrayList);
        this.arrayList = arrayList;
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
        surahText.setText(arrayList.get(position).getName());
        checkBox.setClickable(false);
        checkBox.setChecked(selectedItemIds.get(position));
        surahText.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SurahActivity.class);
            intent.putExtra(MainActivity.EXTRA_MESSAGE, arrayList.get(position).getName());
            intent.putExtra("selected", selectedItemIds.get(position));
            intent.putExtra("id", position + 1);
            getContext().startActivity(intent);
        });
        return listItemView;
    }

    void checkCheckBox(int position) {
        selectedItemIds.put(position, true);
        notifyDataSetChanged();
    }
}
