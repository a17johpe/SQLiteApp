package org.brohede.marcus.sqliteapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Mountain> mountainData = new ArrayList<Mountain>();
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Mountain berg = new Mountain("K2", "Dunno", 53 );
        Mountain berg2 = new Mountain("K3", "UUUUUH", 42);
        mountainData.add(berg);
        mountainData.add(berg2);

        adapter = new ArrayAdapter(getApplicationContext(), R.layout.list_item_textview, R.id.my_item_textview, mountainData);
        ListView myListView = (ListView) findViewById(R.id.my_listview);
        myListView.setAdapter(adapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Mountain m = mountainData.get(position);
                Toast.makeText(getApplicationContext(), "hej", Toast.LENGTH_SHORT).show();
            }
        });

        MountainReaderDbHelper kjell = new MountainReaderDbHelper(getApplicationContext());
    }

    /*
        TODO: Create an App that stores Mountain data in SQLite database

        TODO: Schema for the database must include columns for all member variables in Mountain class
              See: https://developer.android.com/training/data-storage/sqlite.html

        TODO: The Main Activity must have a ListView that displays the names of all the Mountains
              currently in the local SQLite database.

        TODO: In the details activity an ImageView should display the img_url
              See: https://developer.android.com/reference/android/widget/ImageView.html

        TODO: The main activity must have an Options Menu with the following options:
              * "Fetch mountains" - Which fetches mountains from the same Internet service as in
                "Use JSON data over Internet" assignment. Re-use code.
              * "Drop database" - Which drops the local SQLite database

        TODO: All fields in the details activity should be EditText elements

        TODO: The details activity must have a button "Update" that updates the current mountain
              in the local SQLite database with the values from the EditText boxes.
              See: https://developer.android.com/training/data-storage/sqlite.html

        TODO: The details activity must have a button "Delete" that removes the
              current mountain from the local SQLite database
              See: https://developer.android.com/training/data-storage/sqlite.html

        TODO: The SQLite database must not contain any duplicate mountain names

     */
}
