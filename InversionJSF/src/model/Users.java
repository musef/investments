package model;

import java.io.Serializable;



/**
 * Entity implementation class for Entity: Users
 *
 * @author musef
 * 
  * @version 2.3JSF 2013-01-31
 *
 */


public class Users implements Serializable {
	

	/* ******************************************************************************************
	 * File Description
	 * 
	 * id  =====> Identificador de la grabacion del registro usuario USERS
	 * keyUser => Clave general identificativa del usuario (INTERNAL)
	 * name ====> Nombre (y apellidos) del usuario registrado (REQUIRED)
	 * address => Direccion del usuario registrado (REQUIRED) 
	 * city ====> Localidad del usuario registrado (REQUIRED)
	 * codpost => Codigo postal del usuario registrado (REQUIRED) 
	 * nif =====> Nif del usuario registrado (REQUIRED) 
	 * email ===> Email del usuario registrado (NOT REQUIRED) 
	 * phone ===> Telefono del usuario registrado (NOT REQUIRED)
	 * idtf ====> MAPEO: Referencia del usuario registrado hacia su grabacion en IDENTIFY (INTERNAL)
	 ******************************************************************************************** */
	
	private static final long serialVersionUID = 1L;

	private long id;
	private String keyUser;
	private String name;
	private String address;
	private String city;
	private String codpost;
	private String nif;
	private String email;
	private String phone;
	private Identify idtf;
	
	
	
	public Users() {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCodpost() {
		return codpost;
	}

	public void setCodpost(String codpost) {
		this.codpost = codpost;
	}

	public String getNif() {
		return nif;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}




	public String getEmail() {
		return email;
	}




	public void setEmail(String email) {
		this.email = email;
	}




	public String getPhone() {
		return phone;
	}




	public void setPhone(String phone) {
		this.phone = phone;
	}


	

	public Identify getIdtf() {
		return idtf;
	}




	public void setIdtf(Identify idtf) {
		this.idtf = idtf;
	}
   
}
