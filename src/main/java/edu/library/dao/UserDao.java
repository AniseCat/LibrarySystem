package edu.library.dao;

import edu.library.model.User;

import java.util.ArrayList;

public interface UserDao {

    public boolean JudgeUser(String userId, String password,boolean isAdmin);

    public boolean addUser(String userInfo);

    public boolean deleteUser(String userId);

    public boolean updateUser(User user);

    public User getUser(String userId);

    public ArrayList getChangedUser();

    public ArrayList<User> findUsers(String name);

}
