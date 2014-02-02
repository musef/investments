package control;

import java.io.Serializable;

import model.Identify;
import model.Users;


/**
 * Esta clase contiene las variables estaticas de la aplicacion
 * 
 * @author musef
 * 
  * @version 2.3JSF 2013-01-31
 *
 */


public class InversionJSF implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static String nameUsuario;		// nombre del usuario activo
	protected static long idUsuario=0;			// identificacion del usuario en el fichero identify (identify==>users)
	protected static String keyAccount;			// keyAcc de la cuenta en la que esta operando el usuario (accounts==>operations)
	protected static String keyUser;			// keyUser del usuario activo
	protected static int tabbed=0;				// pestaña en la que estamos operativos (0-3)
	protected static int account;				// cuenta de inversion con la que estamos trabajando
	protected static String[][] accountsInv;	// datos de las cuentas de inversion del usuario activo (accounts==>operations)
												// [0] key de cuenta [1] nombre de cuenta [2] comision fija
												// [3] comision porc [4] impuestos        [5] otros gastos
	
	protected static Identify thisUser;			// Objeto Identify con los datos del usuario
	protected static Users infoUser;			// Objeto Users con los datos del usuario
	
	
}	// ******** FIN DE LA CLASE *************
