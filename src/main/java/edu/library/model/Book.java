package edu.library.model;

public class Book {
    String id;
    String name;
    BookType bookType;
    String bookFormat;
    String bookUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id

                = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name

                = name;
    }

    public BookType getBookType() {
        return bookType;
    }

    public void setBookType(BookType bookType) {
        this.bookType = bookType;
    }

    public String getBookFormat() {
        return bookFormat;
    }

    public void setBookFormat(String bookFormat) {
        this.bookFormat = bookFormat;
    }

    public String getBookUrl() {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }
}


