package com.example.autoacervus.model.entity.id;

import com.example.autoacervus.model.entity.User;

import java.io.Serializable;
import java.util.Objects;

public class BorrowedBookId implements Serializable {
    private User borrower;
    private String title;

    public BorrowedBookId() {
    }

    public BorrowedBookId(User borrower, String title) {
        this.borrower = borrower;
        this.title = title;
    }

    public User getBorrower() {
        return borrower;
    }

    public void setBorrower(User borrower) {
        this.borrower = borrower;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BorrowedBookId that = (BorrowedBookId) o;
        return Objects.equals(borrower, that.borrower) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(borrower, title);
    }
}
