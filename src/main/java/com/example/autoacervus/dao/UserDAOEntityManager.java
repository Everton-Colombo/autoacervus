package com.example.autoacervus.dao;

import com.example.autoacervus.model.entity.User;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public class UserDAOEntityManager implements UserDAO {
    private final EntityManager entityManager;

    @Autowired
    public UserDAOEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<User> getUsers() {
        return entityManager.createQuery("SELECT user FROM User user", User.class).getResultList();
    }

    @Override
    public List<User> getUsersWithBooksDueToday() {
        return entityManager.createQuery("SELECT user FROM User user JOIN user.borrowedBooks borrowedBook " +
                "WHERE borrowedBook.expectedReturnDate = :today", User.class)
                .setParameter("today", LocalDate.now())
                .getResultList();
    }

    @Override
    public List<User> getUsersWithNoBorrowedBooks() {
        return entityManager.createQuery("SELECT u FROM User u LEFT JOIN u.borrowedBooks b WHERE b IS NULL",
                User.class).getResultList();
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        entityManager.persist(user);
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        entityManager.merge(user);
    }
}
