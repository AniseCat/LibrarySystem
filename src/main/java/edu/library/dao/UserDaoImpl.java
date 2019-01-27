package edu.library.dao;

import edu.library.PO.*;
import edu.library.model.BookType;
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
    * 判断用户登录
    * userId 用户名
    * password 输入密码
    * isAdmin 是否为管理员
    * */
    @ResponseBody
    @PostMapping("/user/login")
    public boolean JudgeUser(String userId, String password,boolean isAdmin) {
        boolean success = false;
        if(isAdmin){//是管理员，查询管理员对应表
            Session session = HibernateUtil.getSession();
            Transaction tx=session.beginTransaction();
            AdminPO adminPO = session.get(AdminPO.class, userId);
            tx.commit();
            session.close();
            //如果查到数据且实际密码与输入密码相同，登录成功
            if(adminPO != null && adminPO.getPassword().equals(password)){
                success = true;
            }
        }
        else{//是普通用户，查询用户对应表
            Session session = HibernateUtil.getSession();
            Transaction tx=session.beginTransaction();
            UserPO userPO = session.get(UserPO.class, userId);
            tx.commit();
            session.close();
            //如果查到数据且实际密码与输入密码相同，登录成功
            if(userPO != null && userPO.getPassword().equals(password)){
                success = true;
            }
        }
        return success;
    }

    /*
     * 创建用户
     * userInfo 包含用户信息的json串
     * */
    @ResponseBody
    @PostMapping("/admin/addUser")
    public boolean addUser(String userId, String username, String password, UserType userType, int authorityId){

        boolean success = true;

        //新建一个UserPO
        UserPO userPO = new UserPO();
        userPO.setUserId(userId);
        userPO.setPassword(password);
        userPO.setName(username);
        userPO.setUserType(userType);
        userPO.setAuthorityId(authorityId);

        Session session = HibernateUtil.getSession();
        Transaction tx=session.beginTransaction();
        //存入UserPO表
        session.save(userPO);
        tx.commit();
        session.close();

        return success;
    }

    /*
     * 删除用户
     * userId 用户名
     * */
    @ResponseBody
    @PostMapping("/admin/deleteUser")
    public boolean deleteUser(String userId) {
        boolean success = true;
        Session session = HibernateUtil.getSession() ;
        Transaction tx=session.beginTransaction();
        //找到用户名对应的PO数据
        UserPO userPO = session.get(UserPO.class,userId);
        //从数据库中删除
        session.delete(userPO);
        tx.commit();
        session.close();
        return success;
    }

    /*
     * 修改用户信息
     * User 用户实体
     * */
    @ResponseBody
    @PostMapping("/user/changeInformation")
    public boolean updateUser(String userId, String username, String password, UserType userType, int authorityId){
        boolean success = true;
        //新建一个UserPO
        UserPO userPO = new UserPO();
        userPO.setUserId(userId);
        userPO.setPassword(password);
        userPO.setName(username);
        userPO.setUserType(userType);
        userPO.setAuthorityId(authorityId);
        //保存修改前信息到修改用户表
        addChangdeUser(userPO.getUserId());

        Session session = HibernateUtil.getSession() ;
        Transaction tx=session.beginTransaction();
        //更新数据库的数据
        session.update(userPO);
        tx.commit();
        session.close();
        return success;
    }

    /*
     * 获取用户
     * userId 用户名
     * */
    @ResponseBody
    @PostMapping("/user/showInformation")
    public User getUser(String userId){
        Session session = HibernateUtil.getSession() ;
        Transaction tx=session.beginTransaction();
        //根据userId获取用户
        UserPO userPO = session.get(UserPO.class,userId);
        tx.commit();
        session.close();
        //将转化为User的UserPO返回
        return getUser(userPO);
    }

    /*
     * 获取修改用户的前后信息
     * */
    @ResponseBody
    @PostMapping("/admin/showChangeInformation")
    public ArrayList getChangedUser() {

        ArrayList changedUserList = new ArrayList();
        User[] beforeAfter = new User[2];

        Session session = HibernateUtil.getSession() ;
        Transaction tx=session.beginTransaction();
        //获取所有信息修改用户的修改前数据
        Query query = session.createQuery("from ChangeduserPO");
        ArrayList afterList = (ArrayList) query.list();
        tx.commit();
        session.close();
        int size = afterList.size();
        for(int i = 0; i < size; i++){
            //根据修改前数据，获取修改后数据
            ChangeduserPO afterUser = (ChangeduserPO)afterList.get(i);
            String userId = afterUser.getUserId();
            beforeAfter[0] = getUser(userId);
            beforeAfter[1] = getUser(afterUser);
            //加入list
            changedUserList.add(beforeAfter);
        }
        //方法被调用，说明该呈现的修改信息已经无用，清空修改后的用户表
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
        //获取对应的UserPO
        Query query = session.createQuery("from UserPO where name like ?1")
                .setParameter(1,"%"+name+"%");
        ArrayList userPOList = (ArrayList) query.list();
        tx.commit();
        session.close();
        for(int i = 0; i < userPOList.size(); i++){
            String userId = ((UserPO)userPOList.get(i)).getUserId();
            //转化为User并存入list
            UserList.add(getUser(userId));
        }
        return UserList;
    }



    //将重复代码提取为子程序

    /*
    * 将User转化为UserPO
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

    /*
     * 将UserPO转化为User
     * */
    public User getUser(UserPO userPO){
        if(userPO == null)
            return null;
        else {
            User u = new User();
            u.setId(userPO.getUserId());
            u.setPassword(userPO.getPassword());
            u.setName(userPO.getName());
            u.setUserType(userPO.getUserType());
            u.setBorrowAuthority(getBorrowAuthority(userPO.getAuthorityId()));
            return u;
        }
    }

    /*
     * 将ChangedUserPO转化为User
     * */
    public User getUser(ChangeduserPO changeduserPO){
        User u = new User();
        u.setId(changeduserPO.getUserId());
        u.setPassword(changeduserPO.getPassword());
        u.setName(changeduserPO.getName());
        u.setUserType(changeduserPO.getUserType());
        u.setBorrowAuthority(getBorrowAuthority(changeduserPO.getAuthorityId()));
        return u;
    }

    /*
     * 根据借书权限Id获取借书权限
     * */
    public BorrowAuthority getBorrowAuthority(int authorityId){
        Session session = HibernateUtil.getSession() ;
        Transaction tx=session.beginTransaction();
        BorrowauthorityPO borrowauthorityPO = (BorrowauthorityPO)session.
                createQuery("from BorrowauthorityPO where authorityId = ?1")
                .setParameter(1,authorityId).uniqueResult();
        tx.commit();
        session.close();
        int maxBorrowNum = borrowauthorityPO.getMaxBorrowNum();
        int maxBorrowTime = borrowauthorityPO.getMaxBorrowTime();
        ArrayList<BookType> bookTypes = new ArrayList<>();
        session = HibernateUtil.getSession() ;
        tx=session.beginTransaction();
        ArrayList borrowtypeList = (ArrayList) session.
                createQuery("select bookType from BorrowtypePO where authorityId = ?1")
                .setParameter(1,authorityId).list();
        tx.commit();
        session.close();
        for(int i = 0; i < borrowtypeList.size(); i++){
            bookTypes.add((BookType)borrowtypeList.get(i));
        }
        BorrowAuthority borrowAuthority = new BorrowAuthority(authorityId,maxBorrowNum,maxBorrowTime,bookTypes);
        return borrowAuthority;
    }

    /*
     * 清空用户修改列表
     * */
    public void clearChangedUser(){
        Session session = HibernateUtil.getSession() ;
        Transaction tx=session.beginTransaction();
        session.createQuery("delete ChangeduserPO").executeUpdate();
        tx.commit();
        session.close();
    }

    /*
     * 向用户修改列表中增加用户修改信息
     * */
    public void addChangdeUser(String userId){
        Session session = HibernateUtil.getSession() ;
        Transaction tx=session.beginTransaction();
        UserPO userPO = session.get(UserPO.class,userId);
        ChangeduserPO changeduserPO = session.get(ChangeduserPO.class,userId);
        if(changeduserPO == null) {//如果没有找到对应的修改前信息，则新建并保存
            changeduserPO = new ChangeduserPO();
            changeduserPO.setUserId(userPO.getUserId());
            changeduserPO.setPassword(userPO.getPassword());
            changeduserPO.setName(userPO.getName());
            changeduserPO.setUserType(userPO.getUserType());
            changeduserPO.setAuthorityId(userPO.getAuthorityId());
            session.save(changeduserPO);
        }
        /*
        else{//如果找到了，不必进行更新，修改前信息是最初信息
            changeduserPO = new ChangeduserPO();
            changeduserPO.setUserId(userPO.getUserId());
            changeduserPO.setPassword(userPO.getPassword());
            changeduserPO.setName(userPO.getName());
            changeduserPO.setUserType(userPO.getUserType());
            changeduserPO.setAuthorityId(userPO.getAuthorityId());
            session.update(changeduserPO);
        }
        */
        tx.commit();
        session.close();
    }

}
