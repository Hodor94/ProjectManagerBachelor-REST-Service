package util;

import java.util.logging.Level;
import entity.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * With this instance the system can connect to the database and use the
 * framework hibernate. It registers all needed entities and handles the
 * information of the configuration file for hibernate.
 *
 * @author Raphael Grum
 * @version 1.0
 * @since 1.0
 */
public class HibernateUtil {

	// A SessionFactory is used within Hibernate to manage the connections to
	// the database.
    private static SessionFactory sessionFactory ;

    /*
    Creates a new SessionFactory for the used database. Loads all needed
    information from the config file.
     */
    private static SessionFactory buildSessionFactory() {
        try {
            // Shut down the logger.
            java.util.logging.Logger.getLogger("org.hibernate")
					.setLevel(Level.OFF);
            // Load the configuration
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            // Add the entities to the configuration
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

            System.out.println("Hibernate Configuration loaded");

            ServiceRegistry serviceRegistry = new
					StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
            return configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

	/**
	 * Here it is checked if the SessionFactory for the connection to the
	 * database already was created. If it was not done yet, it'll be done
	 * right here.
	 *
	 * @return The SessionFactory managing the connections to the database.
	 */
	public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
			sessionFactory = buildSessionFactory();
		}
        return sessionFactory;
    }
} 
