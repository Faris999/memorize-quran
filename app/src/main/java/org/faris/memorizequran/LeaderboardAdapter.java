package org.faris.memorizequran;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;

import java.util.ArrayList;

public class LeaderboardAdapter extends ArrayAdapter<User> {

    private ArrayList<User> accounts;

    LeaderboardAdapter(@NonNull Context context, ArrayList<User> accounts) {
        super(context, 0, accounts);
        this.accounts = accounts;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView;
        if (convertView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.leaderboard_list, parent, false);
        } else {
            listItemView = convertView;
        }
        User currentUser = accounts.get(position);
        TextView place = listItemView.findViewById(R.id.place);
        TextView name = listItemView.findViewById(R.id.name);
        TextView score = listItemView.findViewById(R.id.score);
        TextView you = listItemView.findViewById(R.id.you);
        name.setText(currentUser.getName());
        if (currentUser.getId().equals(GoogleSignIn.getLastSignedInAccount(getContext()).getId())) {
            name.setTextColor(Color.parseColor("#00ff00"));
            you.setVisibility(View.VISIBLE);
        } else {
            you.setVisibility(View.GONE);
        }
        score.setText(String.valueOf(currentUser.getScore()));
        place.setText("#" + (position + 1));
        return listItemView;
    }

}
