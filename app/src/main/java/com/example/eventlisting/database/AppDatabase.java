package com.example.eventlisting.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.eventlisting.Event;

@Database(entities = {Event.class, User.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract EventDao eventDao();
    public abstract UserDao userDao();

    // Singleton oluştur
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "event_db"
            ).allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
