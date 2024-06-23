package com.example.autoacervus.model.entity;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@IdClass(BorrowedBookId.class)
@Table(name = "BorrowedBooks")
public class BorrowedBook {

    @Id
    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "borrower")
    private User borrower;

    // The following attributes, code and registryCode, correspond with the fields "código" and "códigoRegistro", which
    // are present in the json responses of acervus' internal api. Little is known about them and about the differences
    // between them, but at least one of them is used as an internal id by the api.
    @Id
    @Column(name = "code")
    private int code;

    @Id
    @Column(name = "registryCode")
    private int registryCode;

    @Column(name = "title")
    private String title;

    @Column(name = "expectedReturnDate")
    private LocalDate expectedReturnDate;

    @Column(name = "canRenew")
    private boolean canRenew = true;

    public BorrowedBook(User borrower, String title, int code, int registryCode, LocalDate expectedReturnDate) {
        this.borrower = borrower;
        this.title = title;
        this.code = code;
        this.registryCode = registryCode;
        this.expectedReturnDate = expectedReturnDate;
    }

    public BorrowedBook() {
    }

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

    public int getCode() {
        return code;
    }

    public int getRegistryCode() {
        return registryCode;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(LocalDate expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public boolean canRenew() {
        return canRenew;
    }

    public void setCanRenew(boolean canRenew) {
        this.canRenew = canRenew;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setRegistryCode(int registryCode) {
        this.registryCode = registryCode;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + "', " +
                "expectedReturnDate=" + expectedReturnDate + ", " +
                "canRenew=" + canRenew + ", " +
                "code=" + code + ", " +
                "registryCode=" + registryCode +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BorrowedBook that = (BorrowedBook) o;
        return Objects.equals(this.borrower, that.borrower) && Objects.equals(this.code, that.code)
                && Objects.equals(this.registryCode, that.registryCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(borrower, title, expectedReturnDate, canRenew, code, registryCode);
    }
}

// ID class, required by the ORM libraries
class BorrowedBookId implements Serializable {
    private User borrower;
    private int code;
    private int registryCode;

    public BorrowedBookId() {
    }

    public BorrowedBookId(User borrower, int code, int registryCode) {
        this.borrower = borrower;
        this.code = code;
        this.registryCode = registryCode;
    }

    public User getBorrower() {
        return borrower;
    }

    public void setBorrower(User borrower) {
        this.borrower = borrower;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getRegistryCode() {
        return registryCode;
    }

    public void setRegistryCode(int registryCode) {
        this.registryCode = registryCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BorrowedBookId that = (BorrowedBookId) o;
        return Objects.equals(borrower, that.borrower) && Objects.equals(code, that.code)
                && Objects.equals(registryCode, that.registryCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(borrower, code, registryCode);
    }
}