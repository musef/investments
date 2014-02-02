package control;

import java.io.Serializable;

import org.hibernate.*;
import org.hibernate.cfg.*;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 * Esta clase crea la configuracion de sessionFactory de Hibernate.
 * 
 * @author musef
 * 
  * @version 2.3JSF 2013-01-31
 *
 */

public class CreateHbnSession implements Serializable {

	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static SessionFactory sessionFactory;
    private static ServiceRegistry serviceRegistry;


    
    public CreateHbnSession()  {
        sessionFactory =configureSessionFactory();
    }

    
    
    private static SessionFactory configureSessionFactory() throws HibernateException {
        
        Configuration configuration = new Configuration();
        configuration.configure();
        serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();        
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        return sessionFactory;
    }
        
    
    
    public static SessionFactory getSessionFactory() {
	return sessionFactory;
    }
    
    public static void close() throws HibernateException  {
        // cierra la sessionFactory y liquida la conexion
        sessionFactory.close();
    }
	
} // ******* END OF CLASS
