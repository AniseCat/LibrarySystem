package edu.library.dao;

import edu.library.model.Book;
import edu.library.model.BookType;
import edu.library.model.BorrowInformation;

import java.util.ArrayList;

public interface BookDao {

    public ArrayList<Book> findBooks(String userId, String keyword);

    public boolean modifyBook(String bookId, String name, BookType bookType, String bookFormat, String bookUrl);

    public Book getBook(String bookId);

    public ArrayList<BorrowInformation> checkBorrowRecord(String userId);

    public ArrayList<BorrowInformation> checkFineRecord(String userId);

    public boolean borrowBook(String userId, String bookId);

    public boolean returnBook(String userId, String bookId);

    public String readBook(String bookId);
}
