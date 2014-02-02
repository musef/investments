package control;



import org.hibernate.Query;
import org.hibernate.Session;

import model.Identify;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;


/**
 * Bean para manejar la identificacion del usuario
 * 
 * @author musef
 * 
  * @version 2.3JSF 2013-01-31
 *
 */

public class IdentifyBean {

	public IdentifyBean() {
		// CONSTRUCTOR
                       
	}
	
	

	/**
	 * Este metodo comprueba si el usuario-password suministrado existe, y si
	 * existe carga las variables estaticas necesarias con los datos del usuario.
	 * 
	 * @param user - Es el nombre de usuario para identificarse
	 * @param pass - Es la contraseña del usuario para identificarse
	 * 
	 * @return Devuelve un boolean, TRUE - FALSE como resultado de la identificacion
	 */
	
	public synchronized boolean identifyUser(String user, String pass) {		
		
		/*
		 * Se crea la sesion y se controla la conexion con la base de datos
		 * Primero creamos un objeto Identify para almacenar los datos leidos
		 * Ejecutamos la consulta y obtenemos los datos del usuario.
		 * Si el user-pass no existe, entonces retornamos con un false
		 * Si existe, entonces leemos datos para las variables estaticas:
		 * nombre, keyUser, pestaña activa, datos de las cuentas de inversion y cuenta activa
		 */
		
		// instanciamos el objeto de persistencia
		// al conectarse inicialmente a la base de datos, si la DDBB estuviera apagada o no
		// conectada se registraria un error de conexion.
		// En ese caso, lanzariamos un error y saldriamos de la aplicacion
		Session mySession=null;
        @SuppressWarnings("unused")
		Transaction tx=null;
		try {
			mySession=CreateHbnSession.getSessionFactory().openSession();
            tx=mySession.beginTransaction();
		} catch (HibernateException ex) {
			System.err.println("Error 2.1.1 Error abriendo la base de datos");
			return false;
		}

		// creamos un objeto Identify
		Identify idUser=null;
		
		Query q=null;
		
		try {
			q=mySession.createQuery("from model.Identify as identify where identify.user like :us and identify.password like :pss");
			q.setParameter("us", user);
			q.setParameter("pss", pass);
			
			idUser=(Identify)q.uniqueResult();
			
		} catch (HibernateException e) {
			System.err.println("Error 2.1.2 Error en identificacion de usuario");
			e.printStackTrace();
			return false;
		
		} finally {
			try {
				mySession.close();
			} catch (HibernateException fl) {
				// do nothing
			}
		}
		
		// si el usuario no esta identificado sale con un FALSE
		if (idUser==null) {
			return false;
		}
		
		// si el usuario esta identificado, crea las variables estaticas
		// y sale con un TRUE
			// id del usuario
		InversionJSF.idUsuario=idUser.getId();
			// nombre del usuario
		InversionJSF.nameUsuario=idUser.getUserInfo().getName();
			// key del usuario
		InversionJSF.keyUser=idUser.getKeyUser();
			// pestaña en que se inicia la aplicacion
		InversionJSF.tabbed=-1;
			// matriz de datos de cuentas del usuario
		AccountsBean datosCuentas=new AccountsBean();
		InversionJSF.accountsInv=datosCuentas.getDataAccounts(InversionJSF.keyUser);
		// en el caso de que no tenga cuentas creadas, la variable estatica account tendra null
		if (InversionJSF.accountsInv==null) {
			// numero de cuenta con la que opera, en este caso no hay
			InversionJSF.account=-1;
		} else {
			// si hay alguna cuenta, asignamos el numero inicial a la primera cuenta
			// numero de cuenta con la que opera
			InversionJSF.account=0;
		}

		return true;
		
	} // fin del metodo identifyUser

	
	
	/**
	 * Este metodo unicamente verifica si existe o no el nombre del usuario,
	 * al objeto de evitar nombres de usuarios repetidos.
	 * 
	 * @param nameUser - Es nombre del usuario que se pretende crear. 
	 * 
	 * @return retorna un boolean TRUE (repetido) - FALSE (no repetido) como resultado 
	 * de la existencia de ese nombre de usuario.
	 */
	
	public synchronized boolean existLoginUser(String nameUser) {
		
		/* Buscamos si esta repetido el login del usuario a crear:
		 * Primero creamos OBJETO Identify para almacenar los datos leidos
		 * Creamos una session y ejecutamos la consulta y buscamos por el login del usuario.
		 * Si el user no existe, entonces retornamos con un false, en caso contrario
		 * retornamos true.
		 */
		
		Identify idUser=null;
		
		// crea una nueva sesion
		Session mySession=null;
        @SuppressWarnings("unused")
		Transaction tx=null;     
		Query q=null;
		
		try {
			mySession=CreateHbnSession.getSessionFactory().openSession();
            tx=mySession.beginTransaction();
			// ejecuta la consulta
            q=mySession.createQuery("from model.Identify as idtf where idtf.user like :kUser");
            q.setParameter("kUser", nameUser);
			
            idUser=(Identify)q.uniqueResult();

		} catch (HibernateException e) {
			System.err.println("Error 2.2.1 Error en identificacion de usuario");
			e.printStackTrace();
			return false;
		} finally {
			try {
				mySession.close();
			} catch (HibernateException fl) {
				// do nothing
			}
		}
		
		// si el usuario no existe sale con un FALSE
		if(idUser==null) {
			return false;
		}

		// si el usuario existe, sale con un true
		return true;
		
	} // fin del metodo existUser
	
} // fin de la CLASS
