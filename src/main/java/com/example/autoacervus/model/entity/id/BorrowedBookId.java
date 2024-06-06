package com.example.autoacervus.model.entity.id;

import com.example.autoacervus.model.entity.User;

import java.io.Serializable;
import java.util.Objects;

public class BorrowedBookId implements Serializable {
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
