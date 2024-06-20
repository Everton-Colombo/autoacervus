package com.example.autoacervus.dao;

import com.example.autoacervus.model.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDAO extends CrudRepository<User, String> {

    User findByEmailDac(String emailDac); // Automagically implemented by Spring Data JPA

    @Query("SELECT user FROM User user JOIN user.borrowedBooks borrowedBook " +
            "WHERE borrowedBook.expectedReturnDate = CURRENT_DATE")
    List<User> getUsersWithBooksDueToday();

    @Query("SELECT user FROM User user LEFT JOIN user.borrowedBooks borrowedBooks WHERE borrowedBooks IS NULL")
    List<User> getUsersWithNoBorrowedBooks();
}
