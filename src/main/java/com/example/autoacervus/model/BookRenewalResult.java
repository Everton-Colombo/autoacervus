package com.example.autoacervus.model;

import com.example.autoacervus.model.entity.BorrowedBook;

import java.util.ArrayList;
import java.util.List;

public class BookRenewalResult {
    private List<BorrowedBook> successfullyRenewedBooks = new ArrayList<>();
    private List<BorrowedBook> notRenewedBooks = new ArrayList<>();

    public BookRenewalResult() {}

    public BookRenewalResult(List<BorrowedBook> successfullyRenewedBooks, List<BorrowedBook> notRenewedBooks) {
        this.successfullyRenewedBooks = successfullyRenewedBooks;
        this.notRenewedBooks = notRenewedBooks;
    }

    public List<BorrowedBook> getSuccessfullyRenewedBooks() {
        return successfullyRenewedBooks;
    }

    public void setSuccessfullyRenewedBooks(List<BorrowedBook> successfullyRenewedBooks) {
        this.successfullyRenewedBooks = successfullyRenewedBooks;
    }

    public List<BorrowedBook> getNotRenewedBooks() {
        return notRenewedBooks;
    }

    public void setNotRenewedBooks(List<BorrowedBook> notRenewedBooks) {
        this.notRenewedBooks = notRenewedBooks;
    }

    /**
     * Returns a list of all the books that were requested to be renewed, regardless of whether they were or were not
     * actually renewed.
     */
    public List<BorrowedBook> getAllBooksRequestedForRenewal() {
        List<BorrowedBook> allBooksRequestedForRenewal = new ArrayList<>();
        allBooksRequestedForRenewal.addAll(successfullyRenewedBooks);
        allBooksRequestedForRenewal.addAll(notRenewedBooks);

        return allBooksRequestedForRenewal;
    }

    /**
     * Returns a list of BorrowedBooks that were successfully renewed for the last time, i.e. books that have just
     * reached the renewal limit due to this very renewal.
     */
    public List<BorrowedBook> getRenewalLimitJustExceededBooks() {
        List<BorrowedBook> renewalLimitJustExceededBooks = new ArrayList<>();

        for (BorrowedBook book : this.successfullyRenewedBooks) {
            if (!book.canRenew()) {
                renewalLimitJustExceededBooks.add(book);
            }
        }

        return renewalLimitJustExceededBooks;
    }

    @Override
    public String toString() {
        return "BookRenewalResult{" +
                "successfullyRenewedBooks=" + successfullyRenewedBooks +
                ", notRenewedBooks=" + notRenewedBooks +
                '}';
    }
}
