package edu.library.PO;

import edu.library.model.BookType;

import javax.persistence.*;

@Entity
@Table(name = "book", schema = "library", catalog = "")
public class BookPO {
    private String bookId;
    private String name;
    private BookType bookType;
    private String bookFormat;
    private String bookUrl;

    @Id
    @Column(name = "bookId", nullable = false, length = 32)
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    @Basic
    @Column(name = "name", nullable = false, length = 32)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "bookType", nullable = true)
    @Enumerated(EnumType.STRING)
    public BookType getBookType() {
        return bookType;
    }

    public void setBookType(BookType bookType) {
        this.bookType = bookType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookPO bookPO = (BookPO) o;

        if (bookId != null ? !bookId.equals(bookPO.bookId) : bookPO.bookId != null) return false;
        if (name != null ? !name.equals(bookPO.name) : bookPO.name != null) return false;
        if (bookType != null ? !bookType.equals(bookPO.bookType) : bookPO.bookType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = bookId != null ? bookId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (bookType != null ? bookType.hashCode() : 0);
        return result;
    }

    @Basic
    @Column(name = "bookFormat", nullable = false, length = 32)
    public String getBookFormat() {
        return bookFormat;
    }

    public void setBookFormat(String bookFormat) {
        this.bookFormat = bookFormat;
    }

    @Basic
    @Column(name = "bookUrl", nullable = true, length = 128)
    public String getBookUrl() {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }
}
