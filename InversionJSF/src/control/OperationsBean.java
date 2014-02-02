package control;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;


import model.Operations;
import org.hibernate.HibernateException;



/**
 * Bean para manejar las operaciones de inversion del usuario.
 * 
 * @author musef
 * 
  * @version 2.3JSF 2013-01-31
 *
 */

public class OperationsBean {
	
	public OperationsBean() {
		// CONSTRUCTOR
	}
	

	
	 /**
	  * Este metodo realiza la grabación de una operacion de mercado.
	  *   
	  * @param keyAcc - Es la clave de la cuenta de inversion
	  * @param market - Es el mercado donde se realiza la operacion (BOLSA)
	  * @param keyUser - Es la clave del usuario que realiza la operacion
	  * @param invName - Es el nombre del titulo negociado
	  * @param numtitles - Es el numero de titulos negociados
	  * @param prizeTitle - Es el importe individual de cada titulo
	  * @param costOper - Son los costes de la operacion
	  * @param fecha - Es la fecha de la operacion
	  * @param typeOper - Es el tipo de operacion (COMPRA / VENTA)
	  * 
	  * @return Devuelve un boolean TRUE / FALSE segun el resultado de la grabacion
	  */
	 
	public synchronized boolean recordOperation(String keyAcc, String market, String keyUser, String invName, String numtitles, String prizeTitle, String costOper, Date fecha, String typeOper) {
		
		// creamos un objeto operations para grabar la operacion
		Operations newOperation=new Operations();
		
		// transformamos los posibles numeros formato español al formato DDBB
		double precioTitulo=0;
		try {
			// intenta transformar, y si da error por formato lo captura y lo trata
			precioTitulo=(double)Float.parseFloat(prizeTitle);
		} catch (NumberFormatException e) {
			// elimina los puntos de los miles y cambia la coma decimal por punto decimal
			CharSequence s=".";
			while (prizeTitle.contains(s)) {
				int position=prizeTitle.indexOf('.');
				prizeTitle=prizeTitle.substring(0, position)+prizeTitle.substring(position+1);
			}
			prizeTitle=prizeTitle.replace(',', '.');
			precioTitulo=(double)Float.parseFloat(prizeTitle);
		}
		
		double costeOperacion=0;
		try {
			// intenta transformar, y si da error por formato lo captura y lo trata
			costeOperacion=(double)Double.parseDouble(costOper);
		} catch (NumberFormatException e) {
			// elimina los puntos de los miles y cambia la coma decimal por punto decimal
			CharSequence s=".";
			while (costOper.contains(s)) {
				int position=costOper.indexOf('.');
				costOper=costOper.substring(0, position)+costOper.substring(position+1);
			}
			costOper=costOper.replace(',', '.');
			costeOperacion=(double)Double.parseDouble(costOper);
		}	
		
		// proceso para redondear a dos decimales el coste de operacion
		Double operBis=costeOperacion*100;
		Long operBis2=(Math.round((operBis)));
		costeOperacion=(double)operBis2/100;
		
		float numerotitulos=0;
		try {
			// intenta transformar, y si da error por formato lo captura y lo trata
			numerotitulos=(float)Float.parseFloat(numtitles);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			}
		
		
		// obtenemos los valores para grabar
		newOperation.setKeyAcc(keyAcc);
		newOperation.setMarket(market);
		newOperation.setKeyUser(keyUser);
		newOperation.setInvName(invName);
		newOperation.setNumberTit((Math.round(numerotitulos)));
		newOperation.setPrizeTit(precioTitulo);
		newOperation.setCostTit(costeOperacion);
		newOperation.setFecha(fecha);
		newOperation.setTypeOper(typeOper);
		
		
		// creamos una sesion
                Session mySession=null;
                Transaction tx=null;
		try {
			mySession=CreateHbnSession.getSessionFactory().openSession();
                        tx=mySession.beginTransaction();
			mySession.save(newOperation);
			tx.commit();    
                        
		} catch (HibernateException ex) {

			tx.rollback();
			System.err.println("Error 3.1.1 Error en el proceso de grabación de operaciones");
			// TODO Auto-generated catch block
			ex.printStackTrace();
			return false;
                        
		} finally {
			try {
			//	mySession.flush();
				mySession.close();				
			} catch (Exception fl) {
				// do nothing
			}
		}
		
		return true;
		
	} // fin del metodo recordOperation

	

	/**
	 * Este metodo busca las operaciones grabadas con una serie de parametros, pudiendo
	 * utilizarse una serie de filtros.
	 * 
	 * @param keyUser - Es la clave del usuario que opera
	 * @param keyAccount - Es la clave de la cuenta de operaciones
	 * @param nameValor - Es el nombre del titulo negociado a buscar
	 * @param tipoOperacion - Es el tipo de operacion realizada (COMPRA o VENTA)
	 * @param mercado - Tipo de mercado de la operacion (BOLSA)
	 * @param fechaIni - Fecha a partir de la cual se efectua la busqueda
	 * @param fechaFin - Fecha hasta la cual se efectua la busqueda
         * 
	 * @return Retorna un List con todas la o las operaciones solicitadas o null en caso de error
	 */
	
	@SuppressWarnings("unchecked")
	public synchronized List<Operations> searchRecord (String keyUser, String keyAccount, String nameValor, String tipoOperacion, String mercado, Date fechaIni, Date fechaFin) {
		
		// creamos la list de retorno
		List<Operations> resultado=new ArrayList<Operations>();
		
                // creamos una sesion
                Session mySession=null;
                @SuppressWarnings("unused")
				Transaction tx=null;
                Query q=null;
		try {
			mySession=CreateHbnSession.getSessionFactory().openSession();
                        tx=mySession.beginTransaction();
                        // consulta
			q=mySession.createQuery("from model.Operations as c where c.keyUser= :keyUser and c.keyAcc= :keyAccount " +
					"and c.invName= :invName and c.market= :market and c.typeOper= :tipoOper and c.fecha>= :fIni " +
					"and c.fecha<= :fFin order by c.fecha");
			q.setParameter("keyUser", keyUser);
			q.setParameter("keyAccount", keyAccount);
			q.setParameter("invName", nameValor);
			q.setParameter("market", mercado);
			q.setParameter("tipoOper", tipoOperacion);
			q.setParameter("fIni", fechaIni);
			q.setParameter("fFin", fechaFin);
			// guardamos el resultado
			resultado=(List<Operations>)q.list();
                        
		} catch (HibernateException ex) {
                
			System.err.println("Error 3.2.1 Error en el proceso de búsqueda de la operación");
			// TODO Auto-generated catch block
			ex.printStackTrace();
			return null;
		} finally {
			try {
				mySession.close();
			} catch (HibernateException fl) {
				// do nothing
			}
		}
		
		return resultado;
		
	} // fin del metodo searchRecords

	
	
	/**
	 * Este metodo busca todas las operaciones grabadas de un usuario concreto, 
	 * en el intervalo de fechas
	 *
	 * @param keyUser - Es la clave del usuario que opera    
	 * @param fechaIni - Fecha a partir de la cual se efectua la busqueda
	 * @param fechaFin - Fecha hasta la cual se efectua la busqueda
	 *
	 * @return Retorna un List con todas la o las operaciones solicitadas o null en caso de error
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	public synchronized List<Operations> searchAllRecords (String keyUser, Date fechaIni, Date fechaFin) {
		
            	// creamos la list de retorno
		List<Operations> resultado=new ArrayList<Operations>();
		
                // creamos una sesion
            	Session mySession=null;
            	 @SuppressWarnings("unused")
                Transaction tx=null;
                Query q=null;
		try {
			mySession=CreateHbnSession.getSessionFactory().openSession();
                        tx=mySession.beginTransaction();
			// consulta
			q=mySession.createQuery("from model.Operations as c where c.keyUser= :keyUser " +
					"and c.fecha>= :fIni and c.fecha<= :fFin order by c.fecha");
			q.setParameter("keyUser", keyUser);
			q.setParameter("fIni", fechaIni);
			q.setParameter("fFin", fechaFin);
						
			// guardamos el resultado
			resultado=(List<Operations>)q.list();
                        
		} catch (HibernateException ex) {
                    
			System.err.println("Error 3.3.1 Error en el proceso de búsqueda de la operación");
			// TODO Auto-generated catch block
			ex.printStackTrace();
			return null;
                        
		} finally {
                        try {
				mySession.close();
			} catch (HibernateException fl) {
				// do nothing
			}
		}
		
		return resultado;
		
	} // fin del metodo searchAllRecords

	
	
	/**
	 * Este metodo busca todas las operaciones grabadas de una cuenta concreta, 
	 * en el intervalo de fechas, seleccionadas por tipo operación (COMPRA O VENTA)
	 *
	 * @param keyUser - Es la clave del usuario que opera    
	 * @param operation - Es la operacion a buscar (COMPRA o VENTA)  
	 * @param fechaIni - Fecha a partir de la cual se efectua la busqueda
	 * @param fechaFin - Fecha hasta la cual se efectua la busqueda
	 *
	 * @return Retorna un List con todas la o las operaciones solicitadas o null en caso de error
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	public synchronized List<Operations> searchAllByType (String keyUser, String operation, Date fechaIni, Date fechaFin) {
		
             	// creamos la list de retorno
		List<Operations> resultado=new ArrayList<Operations>();
		
                // creamos una sesion
            	Session mySession=null;
            	 @SuppressWarnings("unused")
                Transaction tx=null;
                Query q=null;
		try {
			mySession=CreateHbnSession.getSessionFactory().openSession();
                        tx=mySession.beginTransaction();
			// consulta
			q=mySession.createQuery("from model.Operations as c where c.keyUser= :keyUser and c.typeOper= :tipoOper " +
					"and c.fecha>= :fIni and c.fecha<= :fFin order by c.fecha");
			q.setParameter("keyUser", keyUser);
			q.setParameter("tipoOper", operation);
			q.setParameter("fIni", fechaIni);
			q.setParameter("fFin", fechaFin);
						
			// guardamos el resultado
			resultado=(List<Operations>)q.list();
                        
		} catch (HibernateException ex) {

			System.err.println("Error 3.4.1 Error en el proceso de búsqueda de la operación");
			// TODO Auto-generated catch block
			ex.printStackTrace();
			return null;
		} finally {
			try {
				mySession.close();
			} catch (HibernateException fl) {
				// do nothing
			}
		}

		return resultado;
		
	} // fin del metodo searchAllByType
	
	
	
	/**
	 * Este metodo busca todas las operaciones grabadas de una cuenta concreta, 
	 * en el intervalo de fechas, seleccionadas por el titulo de la inversion buscado (nombre)
	 *
	 * @param keyUser - Es la clave del usuario que opera    
	 * @param title - Es el nombre del titulo a buscar 
	 * @param fechaIni - Fecha a partir de la cual se efectua la busqueda
	 * @param fechaFin - Fecha hasta la cual se efectua la busqueda
	 *
	 * @return Retorna un List con todas la o las operaciones solicitadas o null en caso de error
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	public synchronized List<Operations> searchAllByTitle (String keyUser, String title, Date fechaIni, Date fechaFin) {
		
             	// creamos la list de retorno
		List<Operations> resultado=new ArrayList<Operations>();
		
                // creamos una sesion
            	Session mySession=null;
            	 @SuppressWarnings("unused")
                Transaction tx=null;
                Query q=null;
		try {
			mySession=CreateHbnSession.getSessionFactory().openSession();
                        tx=mySession.beginTransaction();
			// consulta
			q=mySession.createQuery("from model.Operations as c where c.keyUser= :keyUser and c.invName= :title " +
					"and c.fecha>= :fIni and c.fecha<= :fFin order by c.fecha");
			q.setParameter("keyUser", keyUser);
			q.setParameter("title", title);
			q.setParameter("fIni", fechaIni);
			q.setParameter("fFin", fechaFin);
						
			// guardamos el resultado
			resultado=(List<Operations>)q.list();
                        
		} catch (HibernateException ex) {

			System.err.println("Error 3.5.1 Error en el proceso de lectura");
			// TODO Auto-generated catch block
			ex.printStackTrace();
			return null;
		} finally {
			try {
				mySession.close();
			} catch (HibernateException fl) {
				// do nothing
			}
		}

		return resultado;
		
	} // fin del metodo searchAllByTitle
	
	
	
	/**
	 * Este metodo busca todas las operaciones grabadas de una cuenta concreta, 
	 * en el intervalo de fechas, seleccionadas por cuenta de inversion (sociedad de valores)
	 *
	 * @param keyUser - Es la clave del usuario que opera    
	 * @param keyAccount - Es la cuenta de inversion a buscar 
	 * @param fechaIni - Fecha a partir de la cual se efectua la busqueda
	 * @param fechaFin - Fecha hasta la cual se efectua la busqueda
	 *
	 * @return Retorna un List con todas la o las operaciones solicitadas o null en caso de error
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	public synchronized List<Operations> searchAllByAccount (String keyUser, String keyAccount, Date fechaIni, Date fechaFin) {
		
             	// creamos la list de retorno
		List<Operations> resultado=new ArrayList<Operations>();
		
                // creamos una sesion
            	Session mySession=null;
            	 @SuppressWarnings("unused")
                Transaction tx=null;
                Query q=null;
		try {
			mySession=CreateHbnSession.getSessionFactory().openSession();
                        tx=mySession.beginTransaction();
			// consulta
			q=mySession.createQuery("from model.Operations as c where c.keyUser= :keyUser and c.keyAcc= :keyAccount " +
					"and c.fecha>= :fIni and c.fecha<= :fFin order by c.fecha");
			q.setParameter("keyUser", keyUser);
			q.setParameter("keyAccount", keyAccount);
			q.setParameter("fIni", fechaIni);
			q.setParameter("fFin", fechaFin);
						
			// guardamos el resultado
			resultado=(List<Operations>)q.list();
                        
		} catch (HibernateException ex) {
		
			System.err.println("Error 3.6.1 Error en el proceso de lectura");
			// TODO Auto-generated catch block
			ex.printStackTrace();
			return null;
		} finally {
			try {
				mySession.close();
			} catch (HibernateException fl) {
				// do nothing
			}
		}

		return resultado;
		
	} // fin del metodo searchAllByAccount
	
	
	
	/**
	 * Este metodo busca todas las operaciones grabadas de una cuenta concreta, y de un tipo determinado 
	 * en el intervalo de fechas, seleccionadas por cuenta de inversion (sociedad de valores)
	 *
	 * @param keyUser - Es la clave del usuario que opera    
	 * @param keyAccount - Es la cuenta de inversion a buscar 
	 * @param typeOper - Es la operacion a buscar (COMPRA o VENTA) 
	 * @param fechaIni - Fecha a partir de la cual se efectua la busqueda
	 * @param fechaFin - Fecha hasta la cual se efectua la busqueda
	 *
	 * @return Retorna un List con todas la o las operaciones solicitadas o null en caso de error
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	public synchronized List<Operations> searchAllBothAccountType (String keyUser, String keyAccount, String typeOper,Date fechaIni, Date fechaFin) {
		
             	// creamos la list de retorno
		List<Operations> resultado=new ArrayList<Operations>();
		
                // creamos una sesion
            	Session mySession=null;
            	 @SuppressWarnings("unused")
                Transaction tx=null;
                Query q=null;
		try {
			mySession=CreateHbnSession.getSessionFactory().openSession();
                        tx=mySession.beginTransaction();
			// consulta
			q=mySession.createQuery("from model.Operations as c where c.keyUser= :keyUser and c.keyAcc= :keyAccount " +
					"and c.typeOper= :tipoOper and c.fecha>= :fIni and c.fecha<= :fFin order by c.fecha");

			q.setParameter("keyUser", keyUser);
			q.setParameter("keyAccount", keyAccount);
			q.setParameter("tipoOper", typeOper);
			q.setParameter("fIni", fechaIni);
			q.setParameter("fFin", fechaFin);
						
			// guardamos el resultado
			resultado=(List<Operations>)q.list();
                        
		} catch (HibernateException ex) {

			System.err.println("Error 3.7.1 Error en el proceso de lectura");
			// TODO Auto-generated catch block
			ex.printStackTrace();
			return null;
		} finally {
			try {
				mySession.close();
			} catch (HibernateException fl) {
				// do nothing
			}
		}

		return resultado;
		
	} // fin del metodo searchAllBothAccountType
	
	
	
	/**
	 * Este metodo busca todas las operaciones grabadas de una cuenta concreta, de un tipo determinado y de
	 * un titulo o valor determinado, en el intervalo de fechas.
	 *
	 * @param keyUser - Es la clave del usuario que opera    
	 * @param typeOper - Es la operacion a buscar (COMPRA o VENTA) 
	 * @param title - Es el nombre del titulo a buscar
	 * @param fechaIni - Fecha a partir de la cual se efectua la busqueda
	 * @param fechaFin - Fecha hasta la cual se efectua la busqueda
	 *
	 * @return Retorna un List con todas la o las operaciones solicitadas o null en caso de error
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	public synchronized List<Operations> searchAllBothTypeTitle (String keyUser, String typeOper, String title, Date fechaIni, Date fechaFin) {
		
             	// creamos la list de retorno
		List<Operations> resultado=new ArrayList<Operations>();
		
                // creamos una sesion
            	Session mySession=null;
            	 @SuppressWarnings("unused")
                Transaction tx=null;
                Query q=null;
		try {
			mySession=CreateHbnSession.getSessionFactory().openSession();
                        tx=mySession.beginTransaction();
			// consulta
			q=mySession.createQuery("from model.Operations as c where c.keyUser= :keyUser and c.invName= :title " +
					"and c.typeOper= :tipoOper and c.fecha>= :fIni and c.fecha<= :fFin order by c.fecha");

			q.setParameter("keyUser", keyUser);
			q.setParameter("title", title);
			q.setParameter("tipoOper", typeOper);
			q.setParameter("fIni", fechaIni);
			q.setParameter("fFin", fechaFin);
						
			// guardamos el resultado
			resultado=(List<Operations>)q.list();
                        
		} catch (HibernateException ex) {

			System.err.println("Error 3.8.1 Error en el proceso de lectura");
			// TODO Auto-generated catch block
			ex.printStackTrace();
			return null;
		} finally {
			try {
				mySession.close();
			} catch (HibernateException fl) {
				// do nothing
			}
		}

		return resultado;
		
	} // fin del metodo searchAllBothTypeTitle
	
	
	
	/**
	 * Este metodo busca todas las operaciones grabadas de una cuenta concreta, de un tipo determinado y de
	 * un titulo o valor determinado, en el intervalo de fechas.
	 *
	 * @param keyUser - Es la clave del usuario que opera    
	 * @param keyAccount - Es la clave de la cuenta de operaciones 
	 * @param title - Es el nombre del titulo a buscar
	 * @param fechaIni - Fecha a partir de la cual se efectua la busqueda
	 * @param fechaFin - Fecha hasta la cual se efectua la busqueda
	 *
	 * @return Retorna un List con todas la o las operaciones solicitadas o null en caso de error
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	public synchronized List<Operations> searchAllBothTitleAccount (String keyUser, String keyAccount, String title, Date fechaIni, Date fechaFin) {
		
             	// creamos la list de retorno
		List<Operations> resultado=new ArrayList<Operations>();
		
                // creamos una sesion
            	Session mySession=null;
            	 @SuppressWarnings("unused")
                Transaction tx=null;
                Query q=null;
		try {
			mySession=CreateHbnSession.getSessionFactory().openSession();
                        tx=mySession.beginTransaction();
			// consulta
			q=mySession.createQuery("from model.Operations as c where c.keyUser= :keyUser and c.keyAcc= :keyAccount " +
					"and c.invName= :title and c.fecha>= :fIni and c.fecha<= :fFin order by c.fecha");

			q.setParameter("keyUser", keyUser);
			q.setParameter("keyAccount", keyAccount);
			q.setParameter("title", title);
			q.setParameter("fIni", fechaIni);
			q.setParameter("fFin", fechaFin);
						
			// guardamos el resultado
			resultado=(List<Operations>)q.list();
                        
		} catch (HibernateException ex) {
		
			System.err.println("Error 3.9.1 Error en el proceso de lectura");
			// TODO Auto-generated catch block
			ex.printStackTrace();
			return null;
		} finally {
			try {
				mySession.close();
			} catch (HibernateException fl) {
				// do nothing
			}
		}

		return resultado;
		
	} // fin del metodo searchAllBothTitleAccount
	
	
	
	/**
	 * Este metodo devuelve una lista de nombres de titulos negociados
	 *
	 * @param keyUser - Es la clave del usuario que opera    
	 *
	 * @return Devuelve un Set con los nombres o null en caso de error
	 * 
	 */
	
	@SuppressWarnings("unchecked")
	public synchronized Set<String> searchTitles (String keyUser) {
		
		// creamos la lista de retorno
		Set<String> resultado=new HashSet<String>();
		// creamos la lista de retorno
		List<Operations> resultadoBusq=new ArrayList<Operations>();
		
                // creamos una sesion
            	Session mySession=null;
            	 @SuppressWarnings("unused")
                Transaction tx=null;
                Query q=null;
		try {
			mySession=CreateHbnSession.getSessionFactory().openSession();
                        tx=mySession.beginTransaction();
			// consulta
			q=mySession.createQuery("from model.Operations as c where c.keyUser= :keyUser");
			q.setParameter("keyUser", keyUser);
			resultadoBusq=(List<Operations>)q.list();
                        
		} catch (HibernateException ex) {

			System.err.println("Error 3.10.1 Error en el proceso de lectura");
			// TODO Auto-generated catch block
			ex.printStackTrace();
			return null;
		} finally {
			try {
				mySession.close();
			} catch (HibernateException fl) {
				// do nothing
			}
		}
		
		if (resultadoBusq.size()>0) {
			for (int n=0;n<resultadoBusq.size();n++){
				resultado.add(resultadoBusq.get(n).getInvName());
			}
		} else resultado=null;

		return resultado;
		
	} //**** fin del metodo searchTitles
	
	
	
	/**
	 * Este metodo devuelve un List de operaciones a realizar
	 * Se devuelven solo dos tipos prefijados: COMPRA Y VENTA
	 *
	 * @return Devuelve un List de Strings con COMPRA y VENTA como
	 * valores prefijados
	 */
	
	public synchronized List<String> searchTipo () {
		
		// creamos un objeto emf para efectuar la consulta

		List<String> resultado=new ArrayList<String>();
		resultado.add("COMPRA");
		resultado.add("VENTA");
		
		return resultado;
		
	} // fin del metodo searchTipo
	
	
	
	/** 
	 * Este metodo realiza el borrado de una operacion de mercado previamente listada.
	  *   
	  * @param keyAccount - Es la clave de la cuenta de inversion
	  * @param keyUser - Es la clave del usuario que realiza la operacion
	  * @param invName - Es el nombre del titulo negociado
	  * @param numtitles - Es el numero de titulos negociados
	  * @param costOper - Son los costes de la operacion
	  * @param fecha - Es la fecha de la operacion
	  * @param typeOper - Es el tipo de operacion (COMPRA / VENTA)
	  * 
	  * @return Devuelve un boolean TRUE / FALSE segun el resultado del borrado
	 * 
	 */
	
	public synchronized boolean deleteOperation(String keyAccount, String keyUser, String invName, String numtitles, String costOper, Date fecha, String typeOper) {
	
		
		// creamos un objeto operations para borrar la operacion
		Operations operationToDelete=null;
		// invocamos el metodo searchRecord para obtener un list con un unico resultado	
		List<Operations> valores=searchRecord (keyUser,keyAccount,invName,typeOper,"BOLSA",fecha,fecha);	
		
		// el list devuelto puede tener varios resultados del mismo valor en la misma fecha:
		// hay que discriminar por importe y titulos
		double total=getNumber(costOper);
		int titulos=(int)getNumber(numtitles);
		
		for (int n=0;n<valores.size();n++) {
			// busca en el list en las operaciones, y si coincide con el numero de titulos 
			// negociados y con el importe total de la operacion
			if (valores.get(n).getCostTit()==total && valores.get(n).getNumberTit()==titulos) {
				operationToDelete=valores.get(n);
				break;
			}
		}
		
		// si no se hubiera encontrado ninguna operacion coincidente con los datos, sale con false
		if (operationToDelete==null) {
			return false;
		}
		
                // creamos una sesion
            	Session mySession=null;
                Transaction tx=null;
                // comienzo del borrado
		try {
			mySession=CreateHbnSession.getSessionFactory().openSession();
                        tx=mySession.beginTransaction();
			// borramos el objeto Accounts
			mySession.delete(operationToDelete);
			tx.commit();
		} catch (HibernateException ex) {
			tx.rollback();
			System.err.println("Error 3.12.1 Error en el proceso de borrado de operaciones");
			// TODO Auto-generated catch block
			ex.printStackTrace();
			return false;
		} finally {
			try {
				mySession.close();
			} catch (HibernateException fl) {
				// do nothing
			}
		}
		
		return true;
		
	} // fin del metodo deleteOperation
	
	
	
	/** 
	 * Este metodo realiza el borrado de una operacion de mercado previamente listada.
	  *   
	  * @param keyUser - Es la clave del usuario que realiza la operacion
	  * @param idSearched - Es la id de la operación a borrar
	  * 
	  * @return Devuelve un boolean TRUE / FALSE segun el resultado del borrado
	 * 
	 */
	
	public synchronized boolean deleteOperation(String keyUser, long idSearched) {
	
            	// creamos un objeto operations para borrar la operacion
		Operations operationToDelete=null;
            
		// creamos una sesion
            	Session mySession=null;
                Transaction tx=null;
                Query q=null;
		try {
			mySession=CreateHbnSession.getSessionFactory().openSession();
                        tx=mySession.beginTransaction();
			// consulta
			q=mySession.createQuery("from model.Operations as c where c.keyUser= :keyUser and c.id= :idSearched");
			q.setParameter("keyUser", keyUser);
			q.setParameter("idSearched", idSearched);
		
			operationToDelete=(Operations)q.uniqueResult();
		
			// empezamos la operacion de borrado 
			// borramos el objeto
			mySession.delete(operationToDelete);
			tx.commit();
                        
		} catch (HibernateException ex) {
                    
			tx.rollback();
			System.err.println("Error 3.13.1 Error en el proceso de borrado de operaciones");
			ex.printStackTrace();
			return false;
		} finally {
			try {
				mySession.close();
			} catch (HibernateException fl) {
				// do nothing
			}
		}
		
		return true;
		
	} // fin del metodo deleteOperation
	
	
	
	/**
	 * Este metodo devuelve una operacion concreta buscada por su id.
	 *
	 * @param idSearched - Es la id de DDBB correspondiente al dato buscado.   
	 *
	 * @return Devuelve un objeto Operations de los datos buscados o null en caso de error.
	 * 
	 */
	
	public synchronized Operations searchOppById (long idSearched) {
		
		// creamos el atributo de retorno
		Operations resultado=null;
		
                		// creamos una sesion
            	Session mySession=null;
            	 @SuppressWarnings("unused")
                Transaction tx=null;
                Query q=null;
		try {
			mySession=CreateHbnSession.getSessionFactory().openSession();
                        tx=mySession.beginTransaction();
			// consulta
			q=mySession.createQuery("from model.Operations as c where c.id= :idS");
			q.setParameter("idS", idSearched);
			resultado=(Operations)q.uniqueResult();
                        
		} catch (HibernateException ex) {

			System.err.println("Error 3.14.1 Error en el proceso de lectura");
			// TODO Auto-generated catch block
			ex.printStackTrace();
			return null;
		} finally {
			try {
				mySession.close();
			} catch (HibernateException fl) {
				// do nothing
			}
		}
		

		return resultado;
		
	} //**** fin del metodo searchOppById
	
	
	
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

	
} // ****************** FIN DE LA CLASS
