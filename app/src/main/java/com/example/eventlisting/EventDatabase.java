package com.example.eventlisting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EventDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "eventApp.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";
    private static final String COLUMN_USER_BIRTH_DATE = "birth_date";

    private static final String TABLE_EVENTS = "events";
    private static final String COLUMN_EVENT_ID = "id";
    private static final String COLUMN_EVENT_TITLE = "title";
    private static final String COLUMN_EVENT_DESCRIPTION = "description";
    private static final String COLUMN_EVENT_DATE = "date";
    private static final String COLUMN_EVENT_LOCATION = "location";
    private static final String COLUMN_EVENT_CAPACITY = "capacity";

    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " ("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USER_NAME + " TEXT NOT NULL, "
            + COLUMN_USER_EMAIL + " TEXT UNIQUE NOT NULL, "
            + COLUMN_USER_PASSWORD + " TEXT NOT NULL, "
            + COLUMN_USER_BIRTH_DATE + " TEXT NOT NULL);";

    private static final String CREATE_TABLE_EVENTS = "CREATE TABLE " + TABLE_EVENTS + " ("
            + COLUMN_EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_EVENT_TITLE + " TEXT NOT NULL, "
            + COLUMN_EVENT_DESCRIPTION + " TEXT, "
            + COLUMN_EVENT_DATE + " TEXT NOT NULL, "
            + COLUMN_EVENT_LOCATION + " TEXT NOT NULL, "
            + COLUMN_EVENT_CAPACITY + " INTEGER NOT NULL);";

    public EventDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_EVENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

    public boolean addUser(String name, String email, String password, String birthDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, name);
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PASSWORD, password);
        values.put(COLUMN_USER_BIRTH_DATE, birthDate);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_EMAIL + "=? AND " + COLUMN_USER_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    public boolean addEvent(String title, String description, String date, String location, int capacity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EVENT_TITLE, title);
        values.put(COLUMN_EVENT_DESCRIPTION, description);
        values.put(COLUMN_EVENT_DATE, date);
        values.put(COLUMN_EVENT_LOCATION, location);
        values.put(COLUMN_EVENT_CAPACITY, capacity);

        long result = db.insert(TABLE_EVENTS, null, values);
        db.close();
        return result != -1;
    }

    // 📌 **Etkinlikleri Listeleme - NULL Kontrolü Eklendi**
    public Cursor getAllEvents() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EVENTS, null);
        if (cursor != null && cursor.getCount() > 0) {
            return cursor;
        } else {
            return null; // Eğer veri yoksa NULL döndür
        }
    }

    // 📌 **Etkinlik Silme Metodu Eklendi (İsteğe Bağlı)**
    public boolean deleteEvent(int eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_EVENTS, COLUMN_EVENT_ID + "=?", new String[]{String.valueOf(eventId)});
        db.close();
        return result > 0;
    }
}
