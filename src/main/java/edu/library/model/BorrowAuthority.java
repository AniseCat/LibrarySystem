package edu.library.model;

import java.util.ArrayList;

public class BorrowAuthority {
    int authorityId;
    int maxBorrowNum;
    int maxBorrowTime;
    ArrayList<BookType> bookTypes;

    public BorrowAuthority(int authorityId,int maxBorrowNum,int maxBorrowTime,ArrayList<BookType> bookTypes){
        this.authorityId = authorityId;
        this.maxBorrowNum = maxBorrowNum;
        this.maxBorrowTime = maxBorrowTime;
        this.bookTypes = bookTypes;
    }

    public int getAuthorityId() {
        return authorityId;
    }

    public int getMaxBorrowNum() {
        return maxBorrowNum;
    }

    public int getMaxBorrowTime() {
        return maxBorrowTime;
    }

    public ArrayList<BookType> getBookTypes() {
        return bookTypes;
    }
}
