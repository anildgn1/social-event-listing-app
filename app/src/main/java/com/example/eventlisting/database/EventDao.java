package com.example.eventlisting.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;

import com.example.eventlisting.Event;

import java.util.List;

@Dao
public interface EventDao {

    @Insert
    void insert(Event event);

    @Insert
    void insertAll(List<Event> eventList);

    @Delete
    void delete(Event event);

    @Query("SELECT * FROM events WHERE createdBy = :email")
    List<Event> getEventsByUserEmail(String email);

    @Query("SELECT * FROM events")
    List<Event> getAllEvents();

    @Query("SELECT * FROM events WHERE id = :eventId LIMIT 1")
    Event getEventById(int eventId);

    @Update
    void update(Event event);

}
