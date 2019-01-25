package edu.library.dao;

import edu.library.PO.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        Configuration config = new Configuration().configure();
        config.addAnnotatedClass(AdminPO.class);
        config.addAnnotatedClass(BookPO.class);
        config.addAnnotatedClass(BorrowauthorityPO.class);
        config.addAnnotatedClass(BorrowrecordPO.class);
        config.addAnnotatedClass(BorrowtypePO.class);
        config.addAnnotatedClass(UserPO.class);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(config.getProperties()).build();
        sessionFactory = config.buildSessionFactory(serviceRegistry);
        return sessionFactory;
    }

    public static Session getSession(){
        return getSessionFactory().getCurrentSession();
    }

}
