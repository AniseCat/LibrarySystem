package edu.library.model;

import java.sql.Timestamp;

public class BorrowInformation {
    String bookId;
    String userId;
    Timestamp borrowDate;
    Timestamp returnDate;
    double totalFine;

    public BorrowInformation(String bookId, String userId, Timestamp borrowDate, Timestamp returnDate, double totalFine) {
        this.bookId = bookId;
        this.userId = userId;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.totalFine = totalFine;
    }

    public String getBookId() {
        return bookId;
    }

    public String getUserId() {
        return userId;
    }

    public Timestamp getBorrowDate() {
        return borrowDate;
    }

    public Timestamp getReturnDate() {
        return returnDate;
    }

    public double getTotalFine() {
        return totalFine;
    }

    public boolean isReturned() { return returnDate!=null; }

}
