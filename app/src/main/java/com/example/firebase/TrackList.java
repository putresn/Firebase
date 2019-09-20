package com.example.firebase;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;


public class TrackList extends ArrayAdapter<Track> {

    private Activity context;
    private List<Track> tracks;

    public TrackList(Activity context, List<Track> tracks){
        super(context, R.layout.track_layout, tracks);
        this.context = context;
        this.tracks = tracks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.track_layout, null, true);

        TextView textViewN = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView textViewG = (TextView) listViewItem.findViewById(R.id.textViewRating);

        Track track = tracks.get(position);
        textViewN.setText(track.getTrackName());
        textViewG.setText(track.getTrackRating()+ "");

        return listViewItem;

    }
}