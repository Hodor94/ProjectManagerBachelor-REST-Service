package util;

import java.util.logging.Level;

import entity.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.jasypt.hibernate5.type.EncryptedDoubleAsStringType;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.hibernate5.encryptor.HibernatePBEEncryptorRegistry;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.hibernate5.encryptor.HibernatePBEEncryptorRegistry;




public class HibernateUtil {

    private static SessionFactory sessionFactory ;

    private static SessionFactory buildSessionFactory() {
        try {
            // Logger ausschalten
            java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.OFF);
            // Create the SessionFactory from hibernate.cfg.xml
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            configuration.addAnnotatedClass(UserEntity.class);
            configuration.addAnnotatedClass(AppointmentEntity.class);
            configuration.addAnnotatedClass(ChatEntity.class);
            configuration.addAnnotatedClass(MessageEntity.class);
            configuration.addAnnotatedClass(StatisticEntity.class);
            configuration.addAnnotatedClass(ProjectEntity.class);
            configuration.addAnnotatedClass(RegisterEntity.class);
            configuration.addAnnotatedClass(TaskEntity.class);
            configuration.addAnnotatedClass(TeamEntity.class);
            configuration.addAnnotatedClass(UserEntity.class);

            /* Set sql syntax
            configuration.setProperty("hibernate.dialect",
                    "org.hinernate.dialect.MySQLInnoDBDialect");
                    */
            /*
            StandardPBEStringEncryptor strongEncryptor = new StandardPBEStringEncryptor();
            strongEncryptor.setPassword(configuration.getProperty("encryption.password"));
            HibernatePBEEncryptorRegistry registry = HibernatePBEEncryptorRegistry.getInstance();
            registry.registerPBEStringEncryptor("stringEncryptor",
            strongEncryptor); */


            System.out.println("Hibernate Configuration loaded");

            ServiceRegistry serviceRegistry =
					new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
            System.out.println("Hibernate serviceRegistry created");

            return configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null)
            sessionFactory = buildSessionFactory();
        return sessionFactory;
    }
} 
