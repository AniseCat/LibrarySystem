package edu.library.PO;

import javax.persistence.*;

@Entity
@Table(name = "borrowauthority", schema = "library", catalog = "")
public class BorrowauthorityPO {
    private int authorityId;
    private int maxBorrowNum;
    private int maxBorrowTime;

    @Id
    @Column(name = "authorityId", nullable = false)
    public int getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(int authorityId) {
        this.authorityId = authorityId;
    }

    @Basic
    @Column(name = "maxBorrowNum", nullable = false)
    public int getMaxBorrowNum() {
        return maxBorrowNum;
    }

    public void setMaxBorrowNum(int maxBorrowNum) {
        this.maxBorrowNum = maxBorrowNum;
    }

    @Basic
    @Column(name = "maxBorrowTime", nullable = false)
    public int getMaxBorrowTime() {
        return maxBorrowTime;
    }

    public void setMaxBorrowTime(int maxBorrowTime) {
        this.maxBorrowTime = maxBorrowTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BorrowauthorityPO that = (BorrowauthorityPO) o;

        if (authorityId != that.authorityId) return false;
        if (maxBorrowNum != that.maxBorrowNum) return false;
        if (maxBorrowTime != that.maxBorrowTime) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = authorityId;
        result = 31 * result + maxBorrowNum;
        result = 31 * result + maxBorrowTime;
        return result;
    }
}
