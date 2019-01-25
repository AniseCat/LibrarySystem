package edu.library.PO;

import edu.library.model.BookType;

import javax.persistence.*;

@Entity
@Table(name = "borrowtype", schema = "library", catalog = "")
public class BorrowtypePO {
    private int id;
    private int authorityId;
    private BookType bookType;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "authorityId", nullable = false)
    public int getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(int authorityId) {
        this.authorityId = authorityId;
    }

    @Basic
    @Column(name = "bookType", nullable = false)
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

        BorrowtypePO that = (BorrowtypePO) o;

        if (id != that.id) return false;
        if (authorityId != that.authorityId) return false;
        if (bookType != null ? !bookType.equals(that.bookType) : that.bookType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + authorityId;
        result = 31 * result + (bookType != null ? bookType.hashCode() : 0);
        return result;
    }
}
