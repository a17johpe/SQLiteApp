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
    private ArrayAdapter adapter;
    MountainReaderDbHelper kjell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new FetchData().execute();

        adapter = new ArrayAdapter(getApplicationContext(), R.layout.list_item_textview, R.id.my_item_textview, mountainData);
        ListView myListView = (ListView) findViewById(R.id.my_listview);
        myListView.setAdapter(adapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Mountain m = mountainData.get(position);
                Toast.makeText(getApplicationContext(), m.info(), Toast.LENGTH_SHORT).show();
            }
        });

        //DATABASE
        kjell = new MountainReaderDbHelper(getApplicationContext());
        SQLiteDatabase dbRead = kjell.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                MountainReaderContract.MountainEntry.COLUMN_NAME_NAME,
                MountainReaderContract.MountainEntry.COLUMN_NAME_LOCATION,
                MountainReaderContract.MountainEntry.COLUMN_NAME_HEIGHT
        };

        // Filter results WHERE "title" = 'My Title'
        //String selection = MountainReaderContract.MountainEntry.COLUMN_NAME_NAME + " = ?";
        //String[] selectionArgs = { "Matterhorn" };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                MountainReaderContract.MountainEntry.COLUMN_NAME_NAME + " DESC";

        Cursor cursor = dbRead.query(
                MountainReaderContract.MountainEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        while(cursor.moveToNext()) {
                String mountainName = cursor.getString(cursor.getColumnIndexOrThrow(MountainReaderContract.MountainEntry.COLUMN_NAME_NAME));
                String mountainLocation = cursor.getString(cursor.getColumnIndexOrThrow(MountainReaderContract.MountainEntry.COLUMN_NAME_LOCATION));
                int mountainHeight = cursor.getInt(cursor.getColumnIndexOrThrow(MountainReaderContract.MountainEntry.COLUMN_NAME_HEIGHT));

                Mountain m = new Mountain(mountainName, mountainLocation, mountainHeight);
                adapter.add(m);
            }
        cursor.close();
    }


    //JSON
    private class FetchData extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... params) {
            // These two variables need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a Java string.
            String jsonStr = null;

            try {
                // Construct the URL for the Internet service
                URL url = new URL("http://wwwlab.iit.his.se/brom/kurser/mobilprog/dbservice/admin/getdataasjson.php?type=brom");

                // Create the request to the PHP-service, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonStr = buffer.toString();
                return jsonStr;
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in
                // attempting to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Network error", "Error closing stream", e);
                    }
                }
            }
        }
        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);
            // This code executes after we have received our data. The String object o holds
            // the un-parsed JSON string or is null if we had an IOException during the fetch.

            // Implement a parsing code that loops through the entire JSON and creates objects
            // of our newly created Mountain class.
            try {
                JSONArray json1 = new JSONArray(o);
                // Gets the data repository in write mode
                SQLiteDatabase dbWrite = kjell.getWritableDatabase();

                for (int i = 0; i < json1.length(); i++) {
                    JSONObject berg = json1.getJSONObject(i);
                    //int mountainId = berg.getInt("ID");
                    String mountainName = berg.getString("name");
                    //String mountainType = berg.getString("type");
                    //String mountainCompany = berg.getString("company");
                    String mountainLocation = berg.getString("location");
                    //String mountainCategory = berg.getString("category");
                    int mountainSize = berg.getInt("size");
                    //int mountainCost = berg.getInt("cost");
                    //JSONArray mountainAuxdata = berg.getJSONArray("auxdata");

                    //DATABASE STUFF
                    // Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_NAME, mountainName);
                    values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_LOCATION, mountainLocation);
                    values.put(MountainReaderContract.MountainEntry.COLUMN_NAME_HEIGHT, mountainSize);

                    // Insert the new row, returning the primary key value of the new row
                    dbWrite.insert(MountainReaderContract.MountainEntry.TABLE_NAME, null, values);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        kjell.close();
        super.onDestroy();
    }
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
