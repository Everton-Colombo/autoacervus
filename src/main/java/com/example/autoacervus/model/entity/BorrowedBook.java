package com.example.autoacervus.model.entity;

import com.example.autoacervus.model.entity.id.BorrowedBookId;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@IdClass(BorrowedBookId.class)
@Table(name = "BorrowedBooks")
public class BorrowedBook {

    @Id
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.DETACH})
    @JoinColumn(name="borrower")
    private User borrower;

    @Id
    @Column(name="title")
    private String title;

    @Column(name="expectedReturnDate")
    private LocalDate expectedReturnDate;

    @Column(name="canRenew")
    private boolean canRenew;

//    private String callNumber;
//    private String inventoryRegistryNumber;
//    private String library;
//    private LocalDate borrowDate;

    public BorrowedBook(User borrower, String title, LocalDate expectedReturnDate) {
        this.borrower = borrower;
        this.title = title;
        this.expectedReturnDate = expectedReturnDate;
    }

    public BorrowedBook() {};

    public User getBorrower() {
        return borrower;
    }

    public void setBorrower(User borrower) {
        this.borrower = borrower;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(LocalDate expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public int canRenew() {
        return canRenew();
    }

    public void setCanRenew(boolean canRenew) {
        this.canRenew = canRenew;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorrowedBook that = (BorrowedBook) o;
        return Objects.equals(this.borrower, that.borrower) && Objects.equals(this.title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(borrower, title, expectedReturnDate, canRenew);
    }
}
