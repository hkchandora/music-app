package com.himanshu.musicapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.himanshu.musicapp.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class DeviceMusicActivity extends AppCompatActivity {

    public static final int MY_PERMISSION_REQUEST = 1;
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;

    private String currentTitle, currentArtist, currentLocation;


    private ListView listView;
    private String[] items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_music);


        if (ContextCompat.checkSelfPermission(DeviceMusicActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(DeviceMusicActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(DeviceMusicActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(DeviceMusicActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {
            doStuff();
        }
    }

    private void doStuff() {
        listView = findViewById(R.id.device_song_recycler_view);
        arrayList = new ArrayList<>();
        getMusic();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

    }

    private void getMusic() {
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songLocation = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                currentTitle = songCursor.getString(songTitle);
                currentArtist = songCursor.getString(songArtist);
                currentLocation = songCursor.getString(songLocation);
                arrayList.add("Title: " + currentTitle + "\nArtist: " + currentArtist + "\n" + currentLocation);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String selectedItem = (String) parent.getItemAtPosition(position);
                        Intent intent = new Intent(getApplicationContext(), MusicPlayActivity.class);
                        intent.putExtra("type", "local");
                        intent.putExtra("title", selectedItem.split("\n")[0]);
                        intent.putExtra("artist", selectedItem.split("\n")[1]);
                        intent.putExtra("data", selectedItem.split("\n")[2]);
                        startActivity(intent);
                    }
                });
            } while (songCursor.moveToNext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(DeviceMusicActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                        doStuff();
                    }
                } else {
                    Toast.makeText(this, "No permission granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }
//
//    public void runTimePermission() {
//        Dexter.withContext(this)
//                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                .withListener(new PermissionListener() {
//                    @Override
//                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//
//                        display();
//
//                    }
//
//                    @Override
//                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
//
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
//
//                        permissionToken.continuePermissionRequest();
//                    }
//                }).check();
//    }
//    public ArrayList<File> findSong(File file) {
//
//        ArrayList<File> arrayList = new ArrayList<>();
//
//        File[] files = file.listFiles();
//        assert files != null;
//        for (File singleFile : files) {
//            if (singleFile.isDirectory() && !singleFile.isHidden()) {
//                arrayList.addAll(findSong(singleFile));
//            } else {
//                if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".wav")) {
//                    arrayList.add(singleFile);
//                }
//            }
//        }
//        return arrayList;
//    }
//    void display() {
//        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
//
//        items = new String[mySongs.size()];
//
//        for (int i = 0; i < mySongs.size(); i++) {
//            items[i] = mySongs.get(i).getName().toString().replace(".mp3", "").replace(".wav", "");
//        }
//
//        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items) {
//            @NonNull
//            @Override
//            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//
//                View view = super.getView(position, convertView, parent);
//                TextView textView = view.findViewById(android.R.id.text1);
//                textView.setSingleLine(true);
//                textView.setMaxLines(1);
//
//                return view;
//            }
//        };
//
//        listView.setAdapter(myAdapter);
//    }
}