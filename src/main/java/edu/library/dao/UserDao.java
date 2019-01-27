package edu.library.dao;

import edu.library.model.User;
import edu.library.model.UserType;

import java.util.ArrayList;

public interface UserDao {

    public boolean JudgeUser(String userId, String password,boolean isAdmin);

    public boolean addUser(String userId, String username, String password, UserType userType, int authorityId);

    public boolean deleteUser(String userId);

    public boolean updateUser(String userId, String username, String password, UserType userType, int authorityId);

    public User getUser(String userId);

    public ArrayList getChangedUser();

    public ArrayList<User> findUsers(String name);

}
