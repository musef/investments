package model;

import java.io.Serializable;
import java.util.List;

/**
 * Entity implementation class for Entity: Identify
 *
 * @author musef
 * 
  * @version 2.3JSF 2013-01-31
 *
 */

public class Identify implements Serializable {

	/* ************************************************************************************************************
	 * File Description
	 * 
	 * id =======> Identificador de la grabacion del registro usuario identity
	 * keyUser ==> Clave general identificativa del usuario
	 * user =====> Login del usuario para identificarse
	 * password => Contraseña del usuario para identificarse
	 * userInfo => MAPEO (PERSIST): Datos generales del usuario registrados en la tabla USERS
	 * account ==> MAPEO (PERSIST): Datos de las cuentas de operación del usuario registrados en la tabla ACCOUNTS 
	 ************************************************************************************************************* */
	
	private static final long serialVersionUID = 1L;
	

	private long id;
	private String keyUser;
	private String user;
	private String password;
	private Users userInfo;
	private List <Accounts> account;
	
	
	

	
	public Identify() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getKeyUser() {
		return keyUser;
	}

	public void setKeyUser(String keyUser) {
		this.keyUser = keyUser;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public Users getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(Users userInfo) {
		this.userInfo = userInfo;
	}

	public List<Accounts> getAccount() {
		return account;
	}

	public void setAccount(List<Accounts> account) {
		this.account = account;
	}


   
}
