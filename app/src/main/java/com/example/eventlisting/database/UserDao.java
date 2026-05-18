package com.example.eventlisting.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.eventlisting.database.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void insert(User user);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    User getUserByEmailAndPassword(String email, String password);
}
