package com.example.eventlisting.database;

public class SampleUserData {

    public static void insertUsers(UserDao userDao) {
        userDao.insert(new User(
                "test@example.com",
                "password123",
                "Ali",
                "Yılmaz",
                "2000-01-01"
        ));

        userDao.insert(new User(
                "demo@example.com",
                "demo123",
                "Ayşe",
                "Demir",
                "1998-05-12"
        ));

        userDao.insert(new User(
                "can@example.com",
                "can2024",
                "Can",
                "Kaya",
                "1995-07-08"
        ));
    }
}
