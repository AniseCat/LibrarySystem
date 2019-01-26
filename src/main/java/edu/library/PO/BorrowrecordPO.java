package edu.library.PO;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "borrowrecord", schema = "library", catalog = "")
public class BorrowrecordPO {
    private Timestamp borrowTime;
    private Timestamp returnTime;
    private Double fine;
    private int recordId;
    private String userId;
    private String bookId;

    @Basic
    @Column(name = "borrowTime", nullable = false)
    public Timestamp getBorrowTime() {
        return borrowTime;
    }

    public void setBorrowTime(Timestamp borrowTime) {
        this.borrowTime = borrowTime;
    }

    @Basic
    @Column(name = "returnTime", nullable = true)
    public Timestamp getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Timestamp returnTime) {
        this.returnTime = returnTime;
    }

    @Basic
    @Column(name = "fine", nullable = true, precision = 0)
    public Double getFine() {
        return fine;
    }

    public void setFine(Double fine) {
        this.fine = fine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BorrowrecordPO that = (BorrowrecordPO) o;

        if (borrowTime != null ? !borrowTime.equals(that.borrowTime) : that.borrowTime != null) return false;
        if (returnTime != null ? !returnTime.equals(that.returnTime) : that.returnTime != null) return false;
        if (fine != null ? !fine.equals(that.fine) : that.fine != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = borrowTime != null ? borrowTime.hashCode() : 0;
        result = 31 * result + (returnTime != null ? returnTime.hashCode() : 0);
        result = 31 * result + (fine != null ? fine.hashCode() : 0);
        return result;
    }

    @Id
    @Column(name = "recordId", nullable = false)
    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    @Basic
    @Column(name = "userId", nullable = false, length = 32)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "bookId", nullable = false, length = 32)
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
