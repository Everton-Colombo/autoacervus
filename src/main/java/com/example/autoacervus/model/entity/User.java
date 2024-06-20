package com.example.autoacervus.model.entity;

import com.example.autoacervus.encryption.AES256PasswordEncoder;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @Column(name = "emailDac")
    private String emailDac;

    @Column(name = "password")
    private String encodededPassword;

    @OneToMany(mappedBy = "borrower", orphanRemoval = true, cascade = { CascadeType.MERGE, CascadeType.REFRESH,
            CascadeType.PERSIST, CascadeType.DETACH })
    private List<BorrowedBook> borrowedBooks = new LinkedList<>();

    @OneToOne(mappedBy = "user", orphanRemoval = true, cascade = { CascadeType.MERGE, CascadeType.REFRESH,
            CascadeType.PERSIST, CascadeType.DETACH })
    private UserSettings settings;

    @OneToOne(mappedBy = "user", orphanRemoval = true, cascade = { CascadeType.MERGE, CascadeType.REFRESH,
            CascadeType.PERSIST, CascadeType.DETACH })
    private UserStats userStats;

    public User(String emailDac, String sbuPassword) {
        this.emailDac = emailDac;
        this.encodededPassword = AES256PasswordEncoder.getInstance().encode(sbuPassword);
        this.settings = new UserSettings(this);
        this.userStats = new UserStats(this);
    }

    public User() {
    }

    public String getEmailDac() {
        return emailDac;
    }

    public void setEmailDac(String email) {
        this.emailDac = email;
    }

    public String getEncodededPassword() {
        return encodededPassword;
    }

    public void setEncodededPassword(String encodededPassword) {
        this.encodededPassword = encodededPassword;
    }

    public String getSbuPassword() {
        return AES256PasswordEncoder.getInstance().decode(this.encodededPassword);
    }

    public void setBorrowedBooks(List<BorrowedBook> borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }

    public List<BorrowedBook> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void updateBorrowedBooks(Collection<BorrowedBook> borrowedBooks) {
        this.borrowedBooks = new LinkedList<>(borrowedBooks);
    }

    public UserSettings getSettings() {
        return settings;
    }

    public void setSettings(UserSettings settings) {
        this.settings = settings;
    }

    public UserStats getUserStats() {
        return userStats;
    }

    public void setUserStats(UserStats userStats) {
        this.userStats = userStats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(emailDac, user.emailDac);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(emailDac);
    }

    @Override
    public String toString() {
        return "User{" +
                "emailDac='" + emailDac + '\'' +
                ", borrowedBooks=" + borrowedBooks +
                ", settings=" + settings +
                ", userStats=" + userStats +
                '}';
    }
}
