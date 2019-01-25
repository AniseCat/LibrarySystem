package edu.library.dao;

import edu.library.PO.AdminPO;
import edu.library.PO.ChangeduserPO;
import edu.library.PO.UserPO;
import edu.library.model.BorrowAuthority;
import edu.library.model.User;
import edu.library.model.UserType;
import net.sf.json.JSONObject;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.ArrayList;

@Controller
@RequestMapping("/LibrarySystem")
public class UserDaoImpl implements UserDao{

    @Autowired
    public UserDaoImpl() {

    }
    /*
    * 判断用户名与密码是否匹配
    * isAdmin 用于判断是否是管理员账号
    * */
    @ResponseBody
    @PostMapping("/user/login")
    public boolean JudgeUser(String userId, String password,boolean isAdmin) {
        boolean success = false;
        if(isAdmin){
            Session session = HibernateUtil.getSession();
            Transaction tx=session.beginTransaction();
            AdminPO adminPO = session.get(AdminPO.class, userId);
            tx.commit();
            session.close();
            if(adminPO != null && adminPO.getPassword().equals(password)){
                success = true;
            }
        }
        else{
            Session session = HibernateUtil.getSession();
            Transaction tx=session.beginTransaction();
            UserPO userPO = session.get(UserPO.class, userId);
            tx.commit();
            session.close();
            if(userPO != null && userPO.getPassword().equals(password)){
                success = true;
            }
        }
        return success;
    }

    @ResponseBody
    @PostMapping("/admin/addUser")
    public boolean addUser(String userInfo) {

        boolean success = true;

        JSONObject jsonObject = JSONObject.fromObject(userInfo);
        String userId = jsonObject.getString("userId");
        String password = jsonObject.getString("password");
        String name = jsonObject.getString("name");
        UserType userType = Enum.valueOf(UserType.class,
                jsonObject.getString("userType"));
        int authorityId = jsonObject.getInt("authorityId");

        UserPO userPO = new UserPO();
        userPO.setUserId(userId);
        userPO.setPassword(password);
        userPO.setName(name);
        userPO.setUserType(userType);
        userPO.setAuthorityId(authorityId);

        Session session = HibernateUtil.getSession();
        Transaction tx=session.beginTransaction();
        session.save(userPO);
        tx.commit();
        session.close();

        return success;
    }

    @ResponseBody
    @PostMapping("/admin/deleteUser")
    public boolean deleteUser(String userId) {
        boolean success = true;
        Session session = HibernateUtil.getSession() ;
        Transaction tx=session.beginTransaction();
        UserPO userPO = session.get(UserPO.class,userId);
        session.delete(userPO);
        tx.commit();
        session.close();
        return success;
    }

    @ResponseBody
    @PostMapping("/user/changeInformation")
    public boolean updateUser(User user) {
        boolean success = true;
        UserPO userPO = getUserPO(user);
        //保存修改前信息到修改用户表
        addChangdeUser(userPO.getUserId());
        Session session = HibernateUtil.getSession() ;
        Transaction tx=session.beginTransaction();
        session.update(userPO);
        tx.commit();
        session.close();
        return success;
    }

    @ResponseBody
    @PostMapping("/user/showInformation")
    public User getUser(String userId){
        Session session = HibernateUtil.getSession() ;
        Transaction tx=session.beginTransaction();
        UserPO userPO = session.get(UserPO.class,userId);
        tx.commit();
        session.close();
        return getUser(userPO);
    }

    @ResponseBody
    @PostMapping("/admin/showChangeInformation")
    public ArrayList getChangedUser() {
        ArrayList changedUserList = new ArrayList();
        User[] beforeAfter = new User[2];
        Session session = HibernateUtil.getSession() ;
        Transaction tx=session.beginTransaction();
        Query query = session.createQuery("from ChangeduserPO");
        ArrayList afterList = (ArrayList) query.list();
        int size = afterList.size();
        for(int i = 0; i < size; i++){
            ChangeduserPO afterUser = (ChangeduserPO)afterList.get(i);
            String userId = afterUser.getUserId();
            beforeAfter[0] = getUser(userId);
            beforeAfter[1] = getUser(afterUser);
            changedUserList.add(beforeAfter);
        }
        tx.commit();
        session.close();
        //清空修改后的用户表
        clearChangedUser();
        return changedUserList;
    }

    /*
    * 寻找所有名字中包含 name 的用户
    * */
    @ResponseBody
    @PostMapping("/admin/searchUser")
    public ArrayList<User> findUsers(String name) {
        ArrayList<User> UserList = new ArrayList();
        Session session = HibernateUtil.getSession() ;
        Transaction tx=session.beginTransaction();
        Query query = session.createQuery("from UserPO where name like ?1")
                .setParameter(1,"%"+name+"%");
        ArrayList userPOList = (ArrayList) query.list();
        for(int i = 0; i < userPOList.size(); i++){
            String userId = ((UserPO)userPOList.get(i)).getUserId();
            UserList.add(getUser(userId));
        }
        tx.commit();
        session.close();
        return UserList;
    }


    /*
    * 将重复代码提取为子程序
    *
    * */
    public UserPO getUserPO(User user){
        UserPO userPO = new UserPO();
        userPO.setUserId(user.getId());
        userPO.setPassword(user.getPassword());
        userPO.setName(user.getName());
        userPO.setUserType(user.getUserType());
        userPO.setAuthorityId(user.getBorrowAuthority().getAuthorityId());
        return userPO;
    }

    public User getUser(UserPO userPO){
        User u = new User();
        u.setId(userPO.getUserId());
        u.setPassword(userPO.getPassword());
        u.setName(userPO.getName());
        u.setUserType((UserType) userPO.getUserType());
        u.setBorrowAuthority(getBorrowAuthority(userPO.getAuthorityId()));
        return u;
    }

    public User getUser(ChangeduserPO changeduserPO){
        User u = new User();
        u.setId(changeduserPO.getUserId());
        u.setPassword(changeduserPO.getPassword());
        u.setName(changeduserPO.getName());
        u.setUserType((UserType) changeduserPO.getUserType());
        u.setBorrowAuthority(getBorrowAuthority(changeduserPO.getAuthorityId()));
        return u;
    }

    public BorrowAuthority getBorrowAuthority(int authorityId){
        Session session = HibernateUtil.getSession() ;
        Transaction tx=session.beginTransaction();
        BorrowAuthority borrowAuthority = (BorrowAuthority)session.
                createQuery("from BorrowauthorityPO where authorityId = ?1")
                .setParameter(1,authorityId).uniqueResult();
        tx.commit();
        session.close();
        return borrowAuthority;
    }

    public void clearChangedUser(){
        Session session = HibernateUtil.getSession() ;
        Transaction tx=session.beginTransaction();
        session.createQuery("delete from ChangeduserPO where authorityId != ?1")
                .setParameter(1,null);
        tx.commit();
        session.close();
    }

    public void addChangdeUser(String userId){
        Session session = HibernateUtil.getSession() ;
        Transaction tx=session.beginTransaction();
        UserPO userPO = session.get(UserPO.class,userId);
        ChangeduserPO changeduserPO = session.get(ChangeduserPO.class,userId);
        if(changeduserPO == null) {
            changeduserPO = new ChangeduserPO();
            changeduserPO.setUserId(userPO.getUserId());
            changeduserPO.setPassword(userPO.getPassword());
            changeduserPO.setName(userPO.getName());
            changeduserPO.setUserType(userPO.getUserType());
            changeduserPO.setAuthorityId(userPO.getAuthorityId());
            session.save(changeduserPO);
        }
        else{
            changeduserPO = new ChangeduserPO();
            changeduserPO.setUserId(userPO.getUserId());
            changeduserPO.setPassword(userPO.getPassword());
            changeduserPO.setName(userPO.getName());
            changeduserPO.setUserType(userPO.getUserType());
            changeduserPO.setAuthorityId(userPO.getAuthorityId());
            session.update(changeduserPO);
        }
        tx.commit();
        session.close();
    }

}
