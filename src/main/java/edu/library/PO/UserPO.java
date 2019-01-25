package edu.library.PO;

import edu.library.model.UserType;

import javax.persistence.*;

@Entity
@Table(name = "user", schema = "library", catalog = "")
public class UserPO {
    private String userId;
    private String password;
    private String name;
    private UserType userType;
    private int authorityId;

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

    @Basic
    @Column(name = "userType", nullable = false)
    @Enumerated(EnumType.STRING)
    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserPO userPO = (UserPO) o;

        if (userId != null ? !userId.equals(userPO.userId) : userPO.userId != null) return false;
        if (password != null ? !password.equals(userPO.password) : userPO.password != null) return false;
        if (name != null ? !name.equals(userPO.name) : userPO.name != null) return false;
        if (userType != null ? !userType.equals(userPO.userType) : userPO.userType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (userType != null ? userType.hashCode() : 0);
        return result;
    }

    @Basic
    @Column(name = "authorityId", nullable = false)
    public int getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(int authorityId) {
        this.authorityId = authorityId;
    }
}
