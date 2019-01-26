package edu.library.model;

import java.sql.Time;
import java.util.Date;

public class BorrowInformation {
    String bookId;
    String userId;
    Date borrowDate;
    Date returnDate;
    double totalFine;

    public BorrowInformation(String bookId, String userId, Date borrowDate, Date returnDate, double totalFine) {
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

    public Date getBorrowDate() {
        return borrowDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public double getTotalFine() {
        return totalFine;
    }

    public boolean isReturned() { return returnDate!=null; }

}
