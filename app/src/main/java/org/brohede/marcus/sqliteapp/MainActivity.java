package org.brohede.marcus.sqliteapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Mountain> mountainData = new ArrayList<Mountain>();
    List itemIds = new ArrayList<>();
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //new FetchData().execute();

        Mountain berg = new Mountain("K2", "Dunno", 53);
        Mountain berg2 = new Mountain("K3", "UUUUUH", 42);
        mountainData.add(berg);
        mountainData.add(berg2);

        adapter = new ArrayAdapter(getApplicationContext(), R.layout.list_item_textview, R.id.my_item_textview, itemIds);
        ListView myListView = (ListView) findViewById(R.id.my_listview);
        myListView.setAdapter(adapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Mountain m = mountainData.get(position);
                Toast.makeText(getApplicationContext(), m.info(), Toast.LENGTH_SHORT).show();
            }
        });

        MountainReaderDbHelper kjell = new MountainReaderDbHelper(getApplicationContext());
        // Gets the data repository in write mode
        SQLiteDatabase dbWrite = kjell.getWritableDatabase();

    // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_NAME, "K5");
        values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_LOCATION, "Dalarna");

    // Insert the new row, returning the primary key value of the new row
        dbWrite.insert(MountainReaderContract.MountainEntry.TABLE_NAME, null, values);


        SQLiteDatabase dbRead = kjell.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                MountainReaderContract.MountainEntry.COLUMN_NAME_NAME,
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = MountainReaderContract.MountainEntry.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = { "K5" };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                MountainReaderContract.MountainEntry.COLUMN_NAME_NAME + " DESC";

        Cursor cursor = dbRead.query(
                MountainReaderContract.MountainEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        while(cursor.moveToNext()) {
                long itemId = cursor.getLong(
                        cursor.getColumnIndexOrThrow(MountainReaderContract.MountainEntry._ID));
                //itemIds.add(itemId);
                Log.d("klabbe", cursor.getString(cursor.getColumnIndexOrThrow(MountainReaderContract.MountainEntry.COLUMN_NAME_NAME)));
            }
        cursor.close();
    }


    //JSON
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
