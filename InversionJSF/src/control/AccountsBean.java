package control;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Transient;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import model.Accounts;
import org.hibernate.HibernateException;



/**
 * Bean que maneja las cuentas de inversion
 * 
 * @author musef
 * 
  * @version 2.3JSF 2013-01-31
 *
 */


public class AccountsBean {
	
    @Transient
    private Utils utils;
    
	public AccountsBean() {
		// CONSTRUCTOR
            // instanciamos la clase utils
            utils=new Utils();
	}

	
	
	
	/**
	 * Este metodo devuelve la lista de cuentas de inversión y sus datos
	 * correspondientes a un usuario determinado.
	 * 
	 * @param keyUser - Es la clave del usuario cuyas cuentas se buscan.
	 * 
	 * @return Devuelve un String[][] con el key de cuentas[0] y el nombre de cuenta[1]
	 * o null en el caso de error de E/S.
	 */
	
	@SuppressWarnings("unchecked")
	public synchronized String[][] getDataAccounts(String keyUser) {

		/*
		* Primero crea la sesion y variables necesarias y leemos los registros.
	 	* Cogemos del List de con el dato obtenido de la consulta, y renovamos
	 	* la matriz con los datos de la cuenta.
	 	* Finalmente, devolvemos el String o null si algo sale mal
		 */		
		
		// creamos la variable a retornar
		String listaNombre[][]=null;
		List<Accounts> searchKeys=new ArrayList<Accounts>();
		
                Session mySession=null;
                @SuppressWarnings("unused")
				Transaction tx=null;
                Query q=null;
		try {
                    // abrimos una sesion
                    mySession=CreateHbnSession.getSessionFactory().openSession();
                    tx=mySession.beginTransaction();
                    q=mySession.createQuery("from model.Accounts as account where account.keyUser like :keyUser");
                    q.setParameter("keyUser", keyUser);
                    searchKeys=(List<Accounts>)q.list();                    
		
        } catch (HibernateException ex) {
			System.err.println("Error 1.1.1 Se ha producido un error durante la lectura de cuentas");
			ex.printStackTrace();
			return null;
		} finally {
			try {
				mySession.close();
			} catch (HibernateException fl) {
				// do nothing
				System.err.println("Error 1.1.2 Se ha producido un error cerrando database cuenta de inversión");
			}
		}
		
		// guardamos en el string el key de la cuenta y el nombre de la cuenta
		listaNombre=new String[searchKeys.size()][6];
		for (int n=0;n<searchKeys.size();n++) {
			// ver descripcion de InversionAppJPA.accountsInv en el main
			// este array de retorno lo configura
			listaNombre[n][0]=searchKeys.get(n).getKeyAcc();
			listaNombre[n][1]=searchKeys.get(n).getNameAcc();
			listaNombre[n][2]=String.valueOf(searchKeys.get(n).getFix());
			listaNombre[n][3]=String.valueOf(searchKeys.get(n).getPercent());
			listaNombre[n][4]=String.valueOf(searchKeys.get(n).getTax());
			listaNombre[n][5]=String.valueOf(searchKeys.get(n).getOtherCost());
		}
		
		return listaNombre;
		
	}	// fin del metodo getKeyAccounts
	
	
	
	/**
	 * Este metodo modifica los datos de la cuenta de inversion de un
	 * usuario determinado.
	 * 
	 * Solo permite la modificación de los datos economicos de las comisiones
	 * de la cuenta a modificar.
	 * 
	 * @param keyUser es la clave del usuario que va a modificar la cuenta
	 * @param keyAccount es la clave de la cuenta de inversion que se modifica
	 * @param classCom es el nombre del mercado de inversion (BOLSA)
	 * @param fixCom es el valor de los costes fijos (en euros)
	 * @param percentCom son las comisiones porcentuales (en porcentaje)
	 * @param taxCom son los impuestos a pagar (en porcentaje)
	 * @param otherCost son los otros costes a pagar (en euros)
	 * 
	 * @return retorna un boolean TRUE / FALSE con el resultado de la operacion
	 * 
	 */
	
	public synchronized boolean modifyAccount(String keyUser, String keyAccount, String classCom, String fixCom, String percentCom, String taxCom, String otherCost ) {
		
		/*
		* Primero crea la sesion y variables necesarias.
		* Creamos el update de las cuentas
		* 
	 	* Cogemos del List de consulta el unico dato obtenido, el objeto Accounts a modificar
		*   Primero borramos el objeto Accounts y el Commissions grabados
		*   Segundo, se reconstruye un nuevo objeto Commissions con los datos nuevos
		*   Tercero, regrabamos el objeto Accounts
	 	* Hacemos un persist del objeto Accounts, commit y cerramos ficheros.
	 	* Retornamos un true si todo ha ido bien, en caso contrario false.
		 */

		// creamos el objeto a modificar
		Accounts accountToModify=null;
		
                Session mySession=null;
                Transaction tx=null;
                Query q=null;
		try {
                    // abrimos una sesion
                    mySession=CreateHbnSession.getSessionFactory().openSession();
                    tx=mySession.beginTransaction();
			// lectura de la cuenta a modificar
			q=mySession.createQuery("from model.Accounts as c where c.keyUser = :keyUser and c.keyAcc = :keyAccount");
			q.setParameter("keyUser", keyUser);
			q.setParameter("keyAccount", keyAccount);
			
			accountToModify=(Accounts) q.uniqueResult();
			
			// aunque los datos numericos de cuenta deben venir verificados,
			// vamos a realizar una verificacion y/o transformacion para evitar errores en cada dato
			double fixD=utils.getNumber(fixCom);
			double percentD=utils.getNumber(percentCom);	
			double taxesD=utils.getNumber(taxCom);
			double otherCostD=utils.getNumber(otherCost);

			accountToModify.setFix(fixD);
			accountToModify.setPercent(percentD);
			accountToModify.setTax(taxesD);
			accountToModify.setOtherCost(otherCostD);
			
			// actualizamos los datos mediante merge
			mySession.merge(accountToModify);
			tx.commit();                    
		
                } catch (HibernateException ex) {
 
			tx.rollback();
			System.err.println("Error 1.2.1 Se ha producido un error durante la modificación de la cuenta de inversión");
			ex.printStackTrace();
			// sale con false para notificar el error en grabacion
			return false;
		} finally {
			try {
				//mySession.flush();
				mySession.close();
				
			} catch (HibernateException fl) {
				// do nothing
				// para evitar el error del programa
                                System.err.println("Error 1.2.2 Se ha producido un error cerrando database cuenta de inversión");
			}

		}
		
		return true;
		
	}	// fin del metodo modifyAccount
	


	/**
	 * Este metodo borra los datos de la cuenta de inversion
	 * de un cliente determinado.
	 * 
	 * Para ello busca el registro en el cual coincidan la clave de cliente
	 * (keyUser) y la clave de cuenta (keyAccount).
	 * 
	 * @param keyUser es la clave del usuario a borrar la cuenta
	 * @param keyAccount es la clave de la cuenta a borrar
	 * 
	 * @return Devuelve TRUE / FALSE con el resultado del borrado
	 */

	
	public synchronized boolean removeAccount(String keyUser, String keyAccount ) {
		
		/*
		* El proceso es el siguiente:
		* Primero crea la sesion y variables necesarias
		* Recuperamos la cuenta a borrar mediante una consulta
	 	* Acto seguido hacemos un delete del objeto Accounts a borrar, flush y cerramos ficheros.
	 	* Retornamos un true si todo ha ido bien, en caso contrario false.
		 */

		// creamos el objeto a modificar
		Accounts accountSearchedDelete=null;
		
                Session mySession=null;
                Transaction tx=null;
                Query q=null;
		try {
                    // abrimos una sesion
                    mySession=CreateHbnSession.getSessionFactory().openSession();
                    tx=mySession.beginTransaction();
			// recupero el objeto account y para ello introduzco parametros
			q=mySession.createQuery("from model.Accounts as account where account.keyUser like :keyUser and account.keyAcc like :keyAccount");
			q.setParameter("keyUser", keyUser);
			q.setParameter("keyAccount", keyAccount);
			// obtenemos un list de la busqueda con un solo objeto devuelto
			accountSearchedDelete=(Accounts) q.uniqueResult();

			// borramos el objeto Accounts
			mySession.delete(accountSearchedDelete);
			tx.commit();
		} catch (HibernateException e) {
			tx.rollback();
			System.err.println("Error 1.3.1 Se ha producido un error durante el borrado de la cuenta de inversión");
			e.printStackTrace();
			// sale con false para notificar el error en grabacion
			return false;
		} finally {
			try {
                            //	mySession.flush();
				mySession.close();			
			} catch (HibernateException fl) {
				// do nothing
                            System.err.println("Error 1.3.2 Se ha producido un error cerrando database cuenta de inversión");
			}
		}
		
		return true;
		
	}	// fin del metodo removeAccount
	
	
	
	
	
} //******************** FIN DE LA CLASS