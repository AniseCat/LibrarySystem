package edu.library.model;

public class User {
    String id;
    String name;
    String password;
    UserType userType;
    BorrowAuthority borrowAuthority;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public void setBorrowAuthority(BorrowAuthority borrowAuthority) {
        this.borrowAuthority = borrowAuthority;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public UserType getUserType() {
        return userType;
    }

    public BorrowAuthority getBorrowAuthority() {
        return borrowAuthority;
    }

}
