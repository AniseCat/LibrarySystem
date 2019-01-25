package edu.library.PO;

import javax.persistence.*;

@Entity
@Table(name = "admin", schema = "library", catalog = "")
public class AdminPO {
    private String userId;
    private String password;
    private String name;

    @Id
    @Column(name = "userId", nullable = false, length = 32)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "password", nullable = false, length = 32)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "name", nullable = false, length = 32)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdminPO adminPO = (AdminPO) o;

        if (userId != null ? !userId.equals(adminPO.userId) : adminPO.userId != null) return false;
        if (password != null ? !password.equals(adminPO.password) : adminPO.password != null) return false;
        if (name != null ? !name.equals(adminPO.name) : adminPO.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
