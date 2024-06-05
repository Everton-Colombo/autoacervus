package com.example.autoacervus.dao;

import com.example.autoacervus.model.entity.User;

import java.util.List;

public interface UserDAO {
    List<User> getUsers();
    List<User> getUsersWithBooksDueToday();
    List<User> getUsersWithNoBorrowedBooks();
    void saveUser(User user);
    void updateUser(User user);
}
