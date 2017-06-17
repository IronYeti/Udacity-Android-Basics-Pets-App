/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.odometer;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.odometer.data.OdometerContract;
import com.example.android.odometer.data.OdometerContract.OdometerEntry;
import com.example.android.odometer.data.OdometerDbHelper;

/**
 * Displays list of mileage entries that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    /** Database helper that will provide us access to the database */
    private OdometerDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new OdometerDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                OdometerContract.OdometerEntry._ID,
                OdometerEntry.COLUMN_VEHICLE_ID,
                OdometerEntry.COLUMN_DATE,
                OdometerEntry.COLUMN_ODOMETER};

        // Perform a query on the mileage table
        Cursor cursor = db.query(
                OdometerEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        TextView displayView = (TextView) findViewById(R.id.text_view_mileage);

        try {
            // Create a header in the Text View that looks like this:
            //
            // The pets table contains <number of rows in Cursor> pets.
            // _id - name - breed - gender - weight
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText("The mileage table contains " + cursor.getCount() + " entries.\n\n");
            displayView.append(OdometerEntry._ID + " - " +
                    OdometerContract.OdometerEntry.COLUMN_VEHICLE_ID + " - " +
                    OdometerEntry.COLUMN_DATE + " - " +
                    OdometerEntry.COLUMN_ODOMETER + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(OdometerContract.OdometerEntry._ID);
            int vehicleIdColumnIndex = cursor.getColumnIndex(OdometerEntry.COLUMN_VEHICLE_ID);
            int dateColumnIndex = cursor.getColumnIndex(OdometerEntry.COLUMN_DATE);
            int mileageColumnIndex = cursor.getColumnIndex(OdometerEntry.COLUMN_ODOMETER);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                int currentVehicleId = cursor.getInt(vehicleIdColumnIndex);
                String currentDate = cursor.getString(dateColumnIndex);
                int currentMileage = cursor.getInt(mileageColumnIndex);
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentVehicleId + " - " +
                        currentDate + " - " +
                        currentMileage));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertMileage() {
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(OdometerEntry.COLUMN_VEHICLE_ID, 1);
        values.put(OdometerEntry.COLUMN_DATE, "1/1/2017");
        values.put(OdometerEntry.COLUMN_ODOMETER, 100);

        // Insert a new row for Jan 1, 2017 in the database, returning the ID of that new row.
        // The first argument for db.insert() is the mileage table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for this entry.
        long newRowId = db.insert(OdometerEntry.TABLE_NAME, null, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertMileage();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
