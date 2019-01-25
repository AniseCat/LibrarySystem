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

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;


public class BookDaoImpl implements BookDao{

    public ArrayList<Book> findBooks(String keyword){
        ArrayList<Book> BookList = new ArrayList();
        Session session = HibernateUtil.getSession() ;
        Transaction tx=session.beginTransaction();
        Query query = session.createQuery("from BookPO where name like ?1")
                .setParameter(1,"%"+keyword+"%");
        ArrayList bookPOList = (ArrayList) query.list();
        for(int i = 0; i < bookPOList.size(); i++){
            String bookId = ((BookPO)bookPOList.get(i)).getBookId();
            BookList.add(getBook(bookId));
        }
        tx.commit();
        session.close();
        return BookList;
    }

    public boolean modifyBook(String bookInformation){
        boolean success = true;

        JSONObject jsonObject = JSONObject.fromObject(bookInformation);
        String bookId = jsonObject.getString("bookId");
        String name = jsonObject.getString("name");
        BookType bookType = Enum.valueOf(BookType.class,
                jsonObject.getString("bookType"));
        String bookFormat = jsonObject.getString("bookFormat");
        String bookUrl = jsonObject.getString("bookUrl");

        BookPO bookPO = new BookPO();
        bookPO.setBookId(bookId);
        bookPO.setName(name);
        bookPO.setBookType(bookType);
        bookPO.setBookFormat(bookFormat);
        bookPO.setBookUrl(bookUrl);

        Session session = HibernateUtil.getSession();
        Transaction tx=session.beginTransaction();
        session.save(bookPO);
        tx.commit();
        session.close();

        return success;
    }

    public Book getBook(String bookId) {
        Session session = HibernateUtil.getSession() ;
        Transaction tx=session.beginTransaction();
        BookPO bookPO = session.get(BookPO.class,bookId);
        tx.commit();
        session.close();
        return getBook(bookPO);
    }

    public ArrayList<BorrowInformation> checkBorrowRecord(String userId){
        Session session = HibernateUtil.getSession();
        Transaction tx=session.beginTransaction();
        ArrayList borrowRecordPOList = new ArrayList();
        if(userId == null) {
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
            borrowRecordList.add(getBorrowRecord(po));
        }
        return borrowRecordList;
    }

    public ArrayList<BorrowInformation> checkFineRecord(String userId){
        Session session = HibernateUtil.getSession();
        Transaction tx=session.beginTransaction();
        ArrayList borrowRecordPOList = new ArrayList();
        if(userId == null) {
            borrowRecordPOList = (ArrayList) session.createQuery("from BorrowrecordPO where fine > 0").list();
        }
        else{
            borrowRecordPOList = (ArrayList) session.createQuery("from BorrowrecordPO where userId = ?1 and fine > 0")
                    .setParameter(1, userId).list();
        }
        tx.commit();
        session.close();

        ArrayList<BorrowInformation> borrowRecordList = new ArrayList<BorrowInformation>();
        for(int i = 0; i < borrowRecordPOList.size(); i++){
            BorrowrecordPO po = (BorrowrecordPO) borrowRecordPOList.get(i);
            borrowRecordList.add(getBorrowRecord(po));
        }
        return borrowRecordList;
    }

    public boolean borrowBook(String userId, String bookId){
        boolean success = true;
        Session session = HibernateUtil.getSession();
        Transaction tx=session.beginTransaction();
        BorrowrecordPO borrowrecordPO = (BorrowrecordPO) session.createQuery("from BorrowrecordPO where userId = ?1 and bookId = ?2 order by borrowTime desc ")
                .setParameter(1,userId).setParameter(2,bookId).setMaxResults(1).uniqueResult();
        if(borrowrecordPO == null || borrowrecordPO.getReturnTime()!= null) {
            borrowrecordPO = new BorrowrecordPO();
            borrowrecordPO.setUserId(userId);
            borrowrecordPO.setBookId(bookId);
            borrowrecordPO.setBorrowTime(Time.valueOf(LocalTime.now()));
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

    public boolean returnBook(String userId, String bookId){
        boolean success = true;
        Session session = HibernateUtil.getSession();
        Transaction tx=session.beginTransaction();
        BorrowrecordPO borrowrecordPO = (BorrowrecordPO) session.createQuery("from BorrowrecordPO where userId = ?1 and bookId = ?2 order by borrowTime desc ")
                .setParameter(1,userId).setParameter(2,bookId).setMaxResults(1).uniqueResult();
        if(borrowrecordPO != null && borrowrecordPO.getReturnTime()!= null) {
            borrowrecordPO.setReturnTime(Time.valueOf(LocalTime.now()));
            borrowrecordPO.setFine(getFine(userId,borrowrecordPO.getBorrowTime()));
            session.update(borrowrecordPO);
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
    * 暂时没有对应实现
    * */
    public String readBook(String bookId){

        return null;
    }

    public Book getBook(BookPO bookPO){
        Book book = new Book();
        book.setId(bookPO.getBookId());
        book.setName(bookPO.getName());
        book.setBookType((BookType) bookPO.getBookType());
        book.setBookFormat(bookPO.getBookFormat());
        book.setBookUrl(bookPO.getBookUrl());
        return book;
    }

    public BorrowInformation getBorrowRecord(BorrowrecordPO po){
        double fine = 0;
        if(po.getReturnTime() != null)
            fine = po.getFine();
        else
            fine = getFine(po.getUserId(),po.getBorrowTime());
        return new BorrowInformation(po.getBookId(), po.getUserId(), po.getBorrowTime(), po.getReturnTime(),fine);
    }

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
