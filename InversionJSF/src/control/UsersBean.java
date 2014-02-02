package control;

import java.util.List;
import org.hibernate.*;


import model.Accounts;
import model.Identify;
import model.Users;


/**
 * Bean para manejar las de cuentas de usuario.
 * 
 * @author musef
 * 
  * @version 2.3JSF 2013-01-31
 *
 */

public class UsersBean {
	
	public static int MAX_USERS=3;
	
	public UsersBean() {
	// CONSTRUCTOR
		
	}

		
	
	/**
	 * Este metodo crea un usuario nuevo, grabando los datos correspondientes en el 
	 * fichero Identify (identidades) y Users (datos de usuarios).
	 *  
	 * @param usuario - Es el login del usuario creado
	 * @param passw - Es el password del usuario 
	 * @param nombre - Es el nombre del usuario (el que se visualizara en la app)
	 * @param direccion - La direccion donde reside el usuario
	 * @param localidad - La localidad de residencia
	 * @param codigo - El codigo postal de residencia
	 * @param nif - El NIF o CIF del usuario
	 * @param email - (opcional) Es la direccion de email para posibles comunicaciones
	 * @param telefono - (opcional) El el telefono de contacto del usuario
	 * 
	 * @return Devuelve un long como identificador del usuario creado o -1 si hay algun error E/S
	 * 
	 */
	
	public synchronized long createUser(String usuario, String passw, String nombre, String direccion, String localidad, 
			String codigo, String nif, String email, String telefono) {

		// El metodo inicialmente comprueba el numero de usuarios, y si ya ha
		// alcanzado el maximo de usuarios a crear, lanza un mensaje y retorna con un null
		// Si puede crear mas usuarios, primero genera el keyUser del usuario a crear.
		// Para ello emplea numeros aleatorios.
		// Una vez creados esos datos,crea el objeto Identify de grabacion y el Users
		// Crea la instancia del objeto de Persistencia.
		// Se realiza la grabacion del objeto Identify y por CASCADE el Users
		// Llegado al final sin errores, retorna un long con el id del User
		// y si hay algun error de E/S retorna -1

		// para generar el keyUser utilizamos parte del login, parte del password y parte aleatorio
		// primero generamos un aleatorio entre 1000 y 9999 que sea un numero de 4 digitos
		int numAleatorio=(int)Math.floor((Math.random()*(8999))+1001);
		String keyUser=usuario.substring(0, 4)+passw.substring(0, 4)+String.valueOf(numAleatorio);
		
		// creacion datos DDBB identify
		Identify idNew=new Identify();
		idNew.setKeyUser(keyUser);
		idNew.setUser(usuario);
		idNew.setPassword(passw);
		
		// creación datos DDBB usuarios
		Users userNew=new Users();
		userNew.setAddress(direccion);
		userNew.setCity(localidad);
		userNew.setCodpost(codigo);
		userNew.setName(nombre);
		userNew.setNif(nif);
		userNew.setPhone(telefono);
		userNew.setEmail(email);
		userNew.setKeyUser(keyUser);

                Session mySession=null;
                Transaction tx=null;
                try {
                    // creamos una sesion
                    mySession=CreateHbnSession.getSessionFactory().openSession();
                    tx=mySession.beginTransaction();
		
                    // grabacion nuevo usuario	
			// graba el objeto Identify y el Users por cascade
			idNew.setUserInfo(userNew);
			mySession.save(idNew);
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			System.err.println("Error 4.1.1 Se ha producido un error durante la grabación de un nuevo usuario");
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} finally {
			try {
                            //mySession.flush();
                            mySession.close();				
			} catch (HibernateException fl) {
				// do nothing
			}
		}

		return idNew.getId();

	} // fin del metodo create User

	
		
	/**  
	 * Este metodo crea una cuenta de inversion del usuario.
	 * 
	 * @param nombreAcc - es el nombre de la cuenta
	 * @param keyUser - es la clave del usuario que crea la cuenta
	 * @param idUser - es el id de grabacion del usuario
	 * @param typeMarket - es el nombre del mercado de inversion (BOLSA)
	 * @param fix - es el valor de los costes fijos (en euros)
	 * @param percent - son las comisiones porcentuales (en porcentaje)
	 * @param taxes - son los impuestos a pagar (en porcentaje)
	 * @param otherCost - son los otros costes a pagar (en euros)
	 * 
	 * @return retorna un boolean TRUE/FALSE con el resultado de la grabacion
	 */
	
	public synchronized boolean makeAccount(String nombreAcc, String keyUser, long idUser, String typeMarket, String fix, String percent, String taxes, String otherCost) {		
	
		/*
	 	* Primero, construye el keyAcc, la key de la cuenta mediante una combinacion.
	 	* Luego fabrica el string con los campos a grabar.
	 	* Finalmente fabrica el objeto FileWriter y realiza la grabacion.
	 	* Si todo ha ido bien sale con un TRUE.
		*/
	
		// asignamos inicialment el valor de retorno
		boolean resultado=true;
		
                Session mySession=null;
                Transaction tx=null;
                try {
                    // creamos una sesion
                    mySession=CreateHbnSession.getSessionFactory().openSession();
                    tx=mySession.beginTransaction();
                } catch (HibernateException e) {
			tx.rollback();
			System.err.println("Error 4.1.1 Se ha producido un error durante la grabación de un nuevo usuario");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		// obtenemos primero el usuario donde queremos dar de alta la cuenta
		Identify usuarioActivo=getUser(idUser, mySession);
		
		// creamos la cuenta
		int numAleatorio1=(int)Math.floor((Math.random()*(89))+10);
		
		Accounts newAccount=new Accounts();
		
		newAccount.setKeyUser(keyUser);	// clave identificativa del usuario
		// clave identificativa de la cuenta de inversion, formada por clave user+nombre cuenta + numero aleatorio
		newAccount.setKeyAcc(keyUser.substring(0, 5)+nombreAcc.substring(0, 5)+String.valueOf(numAleatorio1));
		newAccount.setNameAcc(nombreAcc);	// nombre de la cuenta de bolsa, obtenida del formulario
		newAccount.setUserId(usuarioActivo); // identificador del usuario (para mapeo)
		newAccount.setClassCom(typeMarket); // mercado para el cual pertenece la comision
		
		// aunque los datos numericos de cuenta deben venir verificados,
		// vamos a realizar una verificacion y/o transformacion para evitar errores en cada dato
		double fixD=getNumber(fix);
		double percentD=getNumber(percent);	
		double taxesD=getNumber(taxes);
		double otherCostD=getNumber(otherCost);

		newAccount.setFix(fixD); 				// comision fija
		newAccount.setPercent(percentD); 		//comision porcentual sobre el nominal
		newAccount.setTax(taxesD); 				// impuesto sobre la comision
		newAccount.setOtherCost(otherCostD);	// otros gastos a aplicar a la operacion
		
		// grabacion de la nueva cuenta
		try {
			mySession.save(newAccount);
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			System.err.println("Error 4.2.1 Se ha producido un error durante la grabación de una nueva cuenta");
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultado=false;
		} finally {
			try {
                            //mySession.flush();
                            mySession.close();				
			} catch (HibernateException fl) {
				// do nothing
			}
		}
		
		return resultado;
		
	} // fin del metodo makeAccount
		
	

	/**
	 * Este metodo sirve para recuperar los datos relativos a un usuario determinado.
	 * 
	 * @param idUser - id del usuario a recuperar la informacion.
	 * 
	 * @return Devuelve como parametros un String[] con los datos del usuario o null si hay 
	 * error de E/S 
	 */	

	public String[] getUsuario (long idUser) {
		
		/*
		 * Leemos el fichero users y el fichero identify
		 * Instanciamos el metodo getUser para obtener un objeto Identify y a traves
		 * de el acceder tambien al objeto Users
		 * En el Identify obtenemos user[0] y pass[1], en el user obtendremos los datos 
		 * desde [2] a [8]
		 * Luego creamos el List de las cuentas de inversion del usuario  que se
		 * almacena en el List estatico InversionAppJPA.idCuentas. Obtenemos esa informacion
		 * a traves del objeto identify por mapeo.
		 * 
		 * Finalmente, retorna el String[] de datos de usuario o null si hay error E/S
		 */
		
        Session mySession=null;
        @SuppressWarnings("unused")
        Transaction tx=null;
		
		// creamos el String receptor de los datos
		String datosUsuario[]=new String[9];
		// creamos el objeto receptor de la lectura
		Identify usuarioActivo=null;
		
		// llamamos a getUser para obtener la lectura de DDBB
		try {
            // creamos una sesion
            mySession=CreateHbnSession.getSessionFactory().openSession();
            tx=mySession.beginTransaction();			
			usuarioActivo=getUser(idUser, mySession);
			if (usuarioActivo==null) {
				// si esto es asi es que se ha producido un error
				// y salimos devolviendo un null
				System.err.println("Error 4.3.1 no hay lecturas");
				return null;
			}
		} catch (HibernateException ex) {
			// Exception controlada en getUser
			ex.printStackTrace();
		} finally {
			try {
                            //mySession.flush();
                            mySession.close();				
			} catch (HibernateException fl) {
				// do nothing
			}
		}
		
		// llegados aqui es que existe informacion de usuario
		// guarda los datos en la matriz a devolver
		datosUsuario[0]=usuarioActivo.getUser();
		datosUsuario[1]=usuarioActivo.getPassword();
		datosUsuario[2]=usuarioActivo.getUserInfo().getName();
		datosUsuario[3]=usuarioActivo.getUserInfo().getAddress();
		datosUsuario[4]=usuarioActivo.getUserInfo().getCodpost();
		datosUsuario[5]=usuarioActivo.getUserInfo().getCity();
		datosUsuario[6]=usuarioActivo.getUserInfo().getNif();
		datosUsuario[7]=usuarioActivo.getUserInfo().getEmail();
		datosUsuario[8]=usuarioActivo.getUserInfo().getPhone();
		
		
		// Recupera las variables estaticas a partir del usuario

			// nombre del usuario
		InversionJSF.nameUsuario=usuarioActivo.getUserInfo().getName();
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

		return datosUsuario;
		
	} // fin del metodo getUsuario
	
	
	
	/**
	 * Este metodo sirve para obtener un objeto Identify con los datos
	 * del usuario. Con ese objeto podremos obtener la información necesaria del usuario 
	 * identificado con su id "idUser".
	 * 
	 * @param idUser - Es la id del usuario buscado
	 * @param mySession - Es la sesion que hay activa actualmente
	 * 
	 * @return Devuelve un objeto Identify que corresponde al usuario o null si hay error E/S
	 */
	
	public synchronized Identify getUser(long idUser, Session mySession) {
		
		Query q=null;
		
		Identify identidad=null;
	
		// busqueda del usuario mediante id
		try {
			q=mySession.createQuery("from model.Identify as identify where identify.id like :id");
			q.setParameter("id", idUser);

			identidad=(Identify)q.uniqueResult();
			
		} catch (HibernateException e) {
			System.err.println("Error 4.4.1 Se ha producido un error durante la busqueda de información de usuario");
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return identidad;
		
	} // fin del metodo getUser
	
	
	
	/**
	 * Este metodo sirve para modificar los datos de un usuario ya existente
	 * en la base de datos. Modificaremos un objeto Identify y un objeto Users.
	 * 
	 * @param idUsuario - Es la clave id del usuario a modificar.
	 * @param usuario - Es el login del usuario.
	 * @param passw - Es el password del usuario. 
	 * @param nombre - Es el nombre del usuario (el que se visualizara en la app)
	 * @param direccion - La direccion donde reside el usuario
	 * @param localidad - La localidad de residencia
	 * @param codigo - El codigo postal de residencia
	 * @param nif - El NIF o CIF del usuario
	 * @param email - (opcional) Es la direccion de email para posibles comunicaciones
	 * @param telefono - (opcional) El el telefono de contacto del usuario
	 * @return Devuelve un boolean con TRUE si el cambio se ha efectuado y
	 * 		FALSE si ha sucedido algun error de E/S
	 */
	
	public synchronized boolean changeUser(Long idUsuario, String usuario, String passw, String nombre, String direccion, String localidad, 
				String codigo, String nif, String email, String telefono) {
			
		/*
		 * Inicialmente creamos las variables y los objetos a usar, y abrimos una sesion
		 * Abrimos una consulta al objeto Identify mediante id, del cual obtenemos un solo
		 * objeto o null.
		 * A traves del Identify obtenemos por persistencia el objeto Users
		 * Luego hacemos set a todas las variables involucradas en el modify
		 * Para grabar hacemos un merge, ya que el objeto existe en persistencia
		 * Finalmente hacemos un commit para enviar los datos
		 * Por precaucion, y dentro de un try intentamos un flush y un close. Ambos son inutiles
		 * porque commit hace el flush y el close, asi pues capturamos el error en blanco.
		 */
		
		Identify chgIdtf=null;
		Users chgUser=null;
		
                Session mySession=null;
                Transaction tx=null;
                Query q=null;
                try {
                    // creamos una sesion
                    mySession=CreateHbnSession.getSessionFactory().openSession();
                    tx=mySession.beginTransaction();
                    // lectura del usuario a modificar
			q=mySession.createQuery("from model.Identify as c where c.id = :id");
			q.setLong("id", idUsuario);
				
			chgIdtf=(Identify)q.uniqueResult();
			chgUser=chgIdtf.getUserInfo();
				
			// set de los datos de identificarse (login - pass)
			chgIdtf.setUser(usuario);
			chgIdtf.setPassword(passw);
				
			// set de los datos del user
			chgUser.setName(nombre);
			chgUser.setNif(nif);
			chgUser.setAddress(direccion);
			chgUser.setCodpost(codigo);
			chgUser.setCity(localidad);
			chgUser.setEmail(email);
			chgUser.setPhone(telefono);				

			// actualizamos los datos: por persistencia se graba tambien el
			// objeto Users chgUser
			mySession.merge(chgIdtf);
			tx.commit();

		} catch (HibernateException e) {
			tx.rollback();
			System.err.println("Error 4.5.1 Se ha producido un error durante la modificación de un nuevo usuario");
			e.printStackTrace();
			return false;
		} finally {
			try {
				// por precaucion: esto dara error porque se solapan con el commit
				//mySession.flush();
				mySession.close();
			} catch (HibernateException ex) {
					// do nothing
					// para evitar el error del programa
			}

		}
			
		return true;
		
	} // fin del metodo changeUser
	
	
	
	/**
	 * Este metodo devuelve el numero de usuarios creados en la aplicacion.
	 * 
	 * La aplicacion permite un numero MAX_USERS de usuarios para crear. Este metodo
	 * cuenta los usuarios creados para su comprobacion.
	 * 
	 * @return Devuelve un int, con el numero de usuarios creados y -1 si se produce un error
	 */
	
	@SuppressWarnings("unchecked")
	public int howManyUsers () {
		
		// creamos las variables a utilizar
		List<Users> numUsers=null;
		
                Session mySession=null;
                
                @SuppressWarnings("unused")
				Transaction tx=null;
                try {
                    // creamos una sesion
                    mySession=CreateHbnSession.getSessionFactory().openSession();
                    tx=mySession.beginTransaction();
                    numUsers=(List<Users>)mySession.createQuery("from model.Users as users").list();
                } catch (HibernateException e) {
                    // instanciamos el objeto de persistencia
                    // si se produce error en la consulta
                    System.err.println("Error 4.6.1 Error procesando lecturas de usuarios");
                    e.printStackTrace();
                    return -1;
		} finally {
			try {
				mySession.close();
			} catch (HibernateException ex) {
					// do nothing
					// para evitar el error del programa
			}
		}
		
		if (numUsers==null) {
			// en el caso de que no existan datos
			return -1;
		}
		// retorna el numero de usuarios grabados
		return numUsers.size();
		
	} // fin del metodo howManyUsers
	
	
	
	/**
	 * Este metodo transforma numeros en formato String a numeros double. Su utilidad
	 * es transformar numero con la coma decimal en numeros en formato punto decimal.
	 * 
	 * @param number - Es un String a transformar en un numero double
	 * @return Retorna un numero en formato double o 0 si hay un error
	 *  o el parametro introducido no es un numero
	 */
	
	private double getNumber(String number) {
	
		/*
		 * Se intenta transformar un supuesto numero en formato String a un numero
		 * en formato double punto decimal.
		 * Primero se intenta la transformacion directa mediante ParseDouble.
		 * Si no funciona, se hace una transformacion del String:
		 *   Primero elimina los posibles puntos del String.
		 *   Luego transforma las posibles comas en punto
		 *   Asi, si teniamos un 3.123,25 ahora tendremos un 3123.25
		 * Procedemos a un nuevo intento de ParseDouble.
		 * Si sale bien, retornamos el double y si sale mal, un 0d
		 */
		
		// transformamos los posibles numeros formato español al formato DDBB
		double numeroCal=0;
		try {
			// intenta transformar, y si da error por formato lo captura y lo trata
			numeroCal=(double)Double.parseDouble(number);
		} catch (NumberFormatException f) {
			// elimina los puntos de los miles y cambia la coma decimal por punto decimal
			CharSequence s=".";
			while (number.contains(s)) {
				int position=number.indexOf('.');
				number=(number.substring(0, position)+number.substring(position+1));
			}
			number=(number.replace(',', '.'));
			try {
				// intenta la segunda transformacion de String a double
				numeroCal=(double)Double.parseDouble(number);
			} catch (NumberFormatException f2){
				// definitivamente no es un numero
				return 0d;
			}
			
		}
		
		return numeroCal;
	} // fin del metodo getNumber
	
	
} // ************ FIN DE LA CLASS
