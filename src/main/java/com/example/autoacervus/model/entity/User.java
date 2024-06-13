package com.example.autoacervus.model.entity;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "Users")
public class User {

    @Id
    @Column(name = "emailDac")
    private String emailDac;

    @Column(name = "sbuPassword")
    private String sbuPassword;

    @OneToMany(mappedBy = "borrower", orphanRemoval = true, cascade = { CascadeType.MERGE, CascadeType.REFRESH,
            CascadeType.PERSIST, CascadeType.DETACH })
    private List<BorrowedBook> borrowedBooks = new LinkedList<>();

    @Column(name="receiveEmails")
    private boolean receiveEmails;

    public User(String emailDac, String sbuPassword) {
        this.emailDac = emailDac;
        this.sbuPassword = sbuPassword;
        this.receiveEmails = true;
    }

    public User() {
    }

    public String getEmailDac() {
        return emailDac;
    }

    public void setEmailDac(String email) {
        this.emailDac = email;
    }

    public String getSbuPassword() {
        return sbuPassword;
    }

    public void setSbuPassword(String password) {
        this.sbuPassword = password;
    }

    public void setBorrowedBooks(List<BorrowedBook> borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }

    public List<BorrowedBook> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void updateBorrowedBooks(Collection<BorrowedBook> borrowedBooks) {
        this.borrowedBooks.clear();
        this.borrowedBooks.addAll(borrowedBooks);
    }

    public boolean isReceiveEmails() {
        return receiveEmails;
    }

    public void setReceiveEmails(boolean receiveEmails) {
        this.receiveEmails = receiveEmails;
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
                ", sbuPassword='" + sbuPassword + '\'' +
                '}';
    }
}
