package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String ARTIST_NAME = "artistname";
    public static final String ARTIST_ID = "artistid";

    EditText editText;
    Button button;
    Spinner spinner;

    DatabaseReference databaseArtist;

    ListView listViewArtists;

    List<Artist> artistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseArtist = FirebaseDatabase.getInstance().getReference("artists");

        editText = findViewById(R.id.editTextName);
        button = findViewById(R.id.buttonAdd);
        spinner = findViewById(R.id.spinnerGenres);

        listViewArtists = findViewById(R.id.listViewArtist);

        artistList = new ArrayList<>();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Add();
            }
        });

        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Artist artist = artistList.get(i);
                Intent intent = new Intent(getApplicationContext(), AddTrackActivity.class);
                intent.putExtra(ARTIST_ID, artist.getArtistId());
                intent.putExtra(ARTIST_NAME, artist.getArtistName());

                startActivity(intent);
            }
        });

        listViewArtists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                Artist artist = artistList.get(i);

                showUpdateDialog(artist.getArtistId(), artist.getArtistName());
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseArtist.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                artistList.clear();
                for(DataSnapshot artistSnapshot : dataSnapshot.getChildren()){
                    Artist artist = artistSnapshot.getValue(Artist.class);

                    artistList.add(artist);
                }

                ArtistList adapter = new ArtistList(MainActivity.this,artistList);
                listViewArtists.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showUpdateDialog(final String artistId, String artistName){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.update_dialog, null);

        dialogBuilder.setView(dialogView);

        final EditText editText = dialogView.findViewById(R.id.editTextName);
        final Button buttonUpdate = dialogView.findViewById(R.id.buttonUpdate);
        final Spinner spinner = findViewById(R.id.spinnerGenres);
        final Button buttonDelete = findViewById(R.id.buttonDelete);

        dialogBuilder.setTitle("Updating Artist" + artistName);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = editText.getText().toString().trim();
                String genre = spinner.getSelectedItem().toString();

                if (TextUtils.isEmpty(name)){
                    editText.setError("Name required");
                    return;
                }

                updateArtist(artistId, name, genre);

                alertDialog.dismiss();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteArtist(artistId);
            }
        });


    }

    private void deleteArtist(String artistId) {
        DatabaseReference dArtist = FirebaseDatabase.getInstance().getReference("artist").child(artistId);
        DatabaseReference dTracks = FirebaseDatabase.getInstance().getReference("track").child(artistId);

        dArtist.removeValue();
        dTracks.removeValue();

        Toast.makeText(this, "Artist deleted",Toast.LENGTH_LONG).show();
    }

    private boolean updateArtist(String id, String name, String genre){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("artists").child(id);

        Artist artist = new Artist(id, name, genre);

        databaseReference.setValue(artist);
        Toast.makeText(this, "Artist Updated",Toast.LENGTH_LONG).show();

        return true;

    }
    /*Saving new Input Data to Database Realtime*/

    private void Add(){
        //getting values to save
        String name = editText.getText().toString().trim();
        String genre = spinner.getSelectedItem().toString();

        //checking if the value is provided
        if (!TextUtils.isEmpty(name)){
            //push() and getKey() is used for get unique id
            String id = databaseArtist.push().getKey();

            //create the artist object
            Artist artist = new Artist(id, name, genre);

            //save the artist
            databaseArtist.child(id).setValue(artist);

            //pop-up the success input
            Toast.makeText(this, "Artist Added", Toast.LENGTH_LONG).show();
        }else {
            //pop-up the blank data
            Toast.makeText(this,"U should enter a name",Toast.LENGTH_LONG).show();
        }
    }
}