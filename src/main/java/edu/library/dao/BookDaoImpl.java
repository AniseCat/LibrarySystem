package edu.library.dao;

import edu.library.PO.BookPO;
import edu.library.PO.BorrowrecordPO;
import edu.library.model.Book;
import edu.library.model.BookType;
import edu.library.model.BorrowInformation;
import net.sf.json.JSONObject;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;

@Controller
@RequestMapping("/LibrarySystem")
public class BookDaoImpl implements BookDao{

    @Autowired
    public BookDaoImpl() {

    }

    /*
     * 搜索书籍
     * keyword 关键字（目前只支持书名）
     * */
    @ResponseBody
    @PostMapping("book/search")
    public ArrayList<Book> findBooks(String keyword){
        ArrayList<Book> BookList = new ArrayList();
        Session session = HibernateUtil.getSession() ;
        Transaction tx=session.beginTransaction();
        //构造hql语句，获取对应的list
        Query query = session.createQuery("from BookPO where name like ?1")
                .setParameter(1,"%"+keyword+"%");
        ArrayList bookPOList = (ArrayList) query.list();
        for(int i = 0; i < bookPOList.size(); i++){
            //将转化后的Book加入list
            BookList.add(getBook((BookPO)bookPOList.get(i)));
        }
        tx.commit();
        session.close();
        return BookList;
    }

    /*
     * 修改书籍信息
     * bookInformation json格式的书籍信息
     * */
    @ResponseBody
    @PostMapping("book/changeBookInformation")
    public boolean modifyBook(String bookInformation){
        boolean success = true;

        //根据jsonString获取Book数据
        JSONObject jsonObject = JSONObject.fromObject(bookInformation);
        String bookId = jsonObject.getString("bookId");
        String name = jsonObject.getString("name");
        BookType bookType = Enum.valueOf(BookType.class,
                jsonObject.getString("bookType"));
        String bookFormat = jsonObject.getString("bookFormat");
        String bookUrl = jsonObject.getString("bookUrl");

        //构造BookPO
        BookPO bookPO = new BookPO();
        bookPO.setBookId(bookId);
        bookPO.setName(name);
        bookPO.setBookType(bookType);
        bookPO.setBookFormat(bookFormat);
        bookPO.setBookUrl(bookUrl);

        Session session = HibernateUtil.getSession();
        Transaction tx=session.beginTransaction();
        //存入数据库
        session.save(bookPO);
        tx.commit();
        session.close();

        return success;
    }

    /*
     * 获取书籍
     * bookId
     * */
    @ResponseBody
    @PostMapping("book/showBookInformation")
    public Book getBook(String bookId) {
        Session session = HibernateUtil.getSession() ;
        Transaction tx=session.beginTransaction();
        //根据Id获取BookPO
        BookPO bookPO = session.get(BookPO.class,bookId);
        tx.commit();
        session.close();
        //将bookPO转化为Book并返回
        return getBook(bookPO);
    }

    /*
     * 查询用户借阅记录
     * userId
     * */
    @ResponseBody
    @PostMapping("user/showBorrowInformation")
    public ArrayList<BorrowInformation> checkBorrowRecord(String userId){
        Session session = HibernateUtil.getSession();
        Transaction tx=session.beginTransaction();
        ArrayList borrowRecordPOList = new ArrayList();
        if(userId == null) {//用户名为空时，默认查找所有记录
            borrowRecordPOList = (ArrayList) session.createQuery("from BorrowrecordPO").list();
        }
        else{
            borrowRecordPOList = (ArrayList) session.createQuery("from BorrowrecordPO where userId = ?1")
                    .setParameter(1, userId).list();
        }
        tx.commit();
        session.close();

        ArrayList<BorrowInformation> borrowRecordList = new ArrayList<BorrowInformation>();
        for(int i = 0; i < borrowRecordPOList.size(); i++){
            BorrowrecordPO po = (BorrowrecordPO) borrowRecordPOList.get(i);
            //将转化后的借阅信息加入list
            borrowRecordList.add(getBorrowRecord(po));
        }
        return borrowRecordList;
    }

    /*
     * 查询用户罚款记录
     * userId
     * */
    @ResponseBody
    @PostMapping("user/showFineInformation")
    public ArrayList<BorrowInformation> checkFineRecord(String userId){
        Session session = HibernateUtil.getSession();
        Transaction tx=session.beginTransaction();
        ArrayList borrowRecordPOList = new ArrayList();
        if(userId == null) {//用户名为空时，默认查找所有记录
            borrowRecordPOList = (ArrayList) session.createQuery("from BorrowrecordPO").list();
        }
        else{
            borrowRecordPOList = (ArrayList) session.createQuery("from BorrowrecordPO where userId = ?1")
                    .setParameter(1, userId).list();
        }
        tx.commit();
        session.close();
        ArrayList<BorrowInformation> borrowRecordList = new ArrayList<BorrowInformation>();
        for(int i = 0; i < borrowRecordPOList.size(); i++){
            BorrowrecordPO po = (BorrowrecordPO) borrowRecordPOList.get(i);
            double fine = po.getFine();
            //如果罚款项为0且未归还，及时计算可能的罚款并赋值
            if(fine == 0 && po.getReturnTime() == null) {
                fine = getFine(userId,po.getBorrowTime());
                po.setFine(fine);
            }
            borrowRecordList.add(getBorrowRecord(po));
        }
        return borrowRecordList;
    }

    /*
     * 借书
     * userId
     * bookId
     * */
    @ResponseBody
    @PostMapping("user/borrowBook")
    public boolean borrowBook(String userId, String bookId){
        boolean success = true;
        Session session = HibernateUtil.getSession();
        Transaction tx=session.beginTransaction();
        //查询该用户对于该书的最近一次借阅
        BorrowrecordPO borrowrecordPO = (BorrowrecordPO) session.createQuery("from BorrowrecordPO where userId = ?1 and bookId = ?2 order by borrowTime desc ")
                .setParameter(1,userId).setParameter(2,bookId).setMaxResults(1).uniqueResult();
        //如果不存在或者已归还，则可以借
        if(borrowrecordPO == null || borrowrecordPO.getReturnTime()!= null) {
            borrowrecordPO = new BorrowrecordPO();
            borrowrecordPO.setUserId(userId);
            borrowrecordPO.setBookId(bookId);
            borrowrecordPO.setBorrowTime(Time.valueOf(LocalTime.now()));
            //生成一条新的借阅信息并保存到数据库
            session.save(borrowrecordPO);
            tx.commit();
            session.close();
        }
        //有正在借的同本书，不能继续借
        else{
            tx.commit();
            session.close();
            success = false;
        }
        return success;
    }

    /*
     * 还书
     * userId
     * bookId
     * */
    @ResponseBody
    @PostMapping("user/returnBook")
    public boolean returnBook(String userId, String bookId){
        boolean success = true;
        Session session = HibernateUtil.getSession();
        Transaction tx=session.beginTransaction();
        //查询最近一次借阅
        BorrowrecordPO borrowrecordPO = (BorrowrecordPO) session.createQuery("from BorrowrecordPO where userId = ?1 and bookId = ?2 order by borrowTime desc ")
                .setParameter(1,userId).setParameter(2,bookId).setMaxResults(1).uniqueResult();
        //如果有且未归还，那么可以归还，设置对应的归还时间和罚款金额
        if(borrowrecordPO != null && borrowrecordPO.getReturnTime()!= null) {
            borrowrecordPO.setReturnTime(Time.valueOf(LocalTime.now()));
            borrowrecordPO.setFine(getFine(userId,borrowrecordPO.getBorrowTime()));
            session.update(borrowrecordPO);
            tx.commit();
            session.close();
        }
        //没有借过这本书，不能归还
        else{
            tx.commit();
            session.close();
            success = false;
        }
        return success;
    }

    /*
    * 阅读书籍,暂时没有对应实现
    * */
    @ResponseBody
    @PostMapping("/book/readBook")
    public String readBook(String bookId){
        return null;
    }

    //将重复代码提取为子程序

    /*
     * 将BookPO转化为Book
     * */
    public Book getBook(BookPO bookPO){
        Book book = new Book();
        book.setId(bookPO.getBookId());
        book.setName(bookPO.getName());
        book.setBookType(bookPO.getBookType());
        book.setBookFormat(bookPO.getBookFormat());
        book.setBookUrl(bookPO.getBookUrl());
        return book;
    }

    /*
     * 将BorrowrecordPO转化为BorrowInformation
     * */
    public BorrowInformation getBorrowRecord(BorrowrecordPO po){
        double fine = 0;
        if(po.getReturnTime() != null)
            fine = po.getFine();
        else
            fine = getFine(po.getUserId(),po.getBorrowTime());
        return new BorrowInformation(po.getBookId(), po.getUserId(), po.getBorrowTime(), po.getReturnTime(),fine);
    }

    /*
     * 根据用户Id,当前时间和借阅时间计算罚款金额
     * */
    public double getFine(String userId,Time borrowTime){
        double fine = 0;
        Session session = HibernateUtil.getSession();
        Transaction tx=session.beginTransaction();
        int authorityId = (int)session.createQuery("select authorityId from UserPO where userId = ?1")
                .setParameter(1,userId).uniqueResult();
        int maxBorrowTime = (int)session.createQuery("select maxBorrowTime from BorrowauthorityPO where authorityId = ?1")
                .setParameter(1,authorityId).uniqueResult();
        tx.commit();
        session.close();
        long timeInterval = Time.valueOf(LocalTime.now()).getTime() - borrowTime.getTime() - maxBorrowTime*60*60*24;
        if(timeInterval > 0)
            fine = 0.1*(timeInterval/(60*60*24));
        return fine;
    }

}
