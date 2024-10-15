package com.example.healthplex;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SlotBooking.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_SLOTS = "slots";
    private static final String COLUMN_SLOT = "slot";
    private static final String COLUMN_BOOKED_SEATS = "bookedSeats";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_SLOTS + " (" +
                COLUMN_SLOT + " TEXT PRIMARY KEY, " +
                COLUMN_BOOKED_SEATS + " INTEGER DEFAULT 0)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SLOTS);
        onCreate(db);
    }

    // Get the number of booked seats for a specific slot
    public int getBookedSeats(String slot) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SLOTS, new String[]{COLUMN_BOOKED_SEATS},
                COLUMN_SLOT + "=?", new String[]{slot}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int bookedSeats = cursor.getInt(cursor.getColumnIndex(COLUMN_BOOKED_SEATS));
            cursor.close();
            return bookedSeats;
        } else {
            return 0;  // Default to 0 if slot doesn't exist
        }
    }

    // Book a slot (increment the number of booked seats)
    public void bookSlot(String slot) {
        SQLiteDatabase db = this.getWritableDatabase();
        int currentSeats = getBookedSeats(slot);

        ContentValues values = new ContentValues();
        values.put(COLUMN_BOOKED_SEATS, currentSeats + 1);

        int rowsUpdated = db.update(TABLE_SLOTS, values, COLUMN_SLOT + "=?", new String[]{slot});

        if (rowsUpdated == 0) {
            // If slot does not exist, insert new row
            values.put(COLUMN_SLOT, slot);
            db.insert(TABLE_SLOTS, null, values);
        }
    }
}
