package model;

import java.io.Serializable;

/**
 * Entity implementation class for Entity: Accounts
 *
 * @author musef
 * 
  * @version 2.3JSF 2013-01-31
 *
 */


public class Accounts implements Serializable {

	/* ********************************************************************************************
	 * File Description
	 * 
	 * id ===========> Identificador de la grabacion del registro usuario ACCOUNTS - Long
	 * keyUser ======> Clave general identificativa del usuario (INTERNAL) - String size15
	 * keyAcc =======> Clave identificativa de cada cuenta de inversion creada (INTERNAL) - String size15
	 * nameAcc ======> Nombre identificador de cada cuenta de inversion (REQUIRED) - String size25
	 * userId =======> MAPEO: Identificador del usuario en la tabla IDENTIFY (INTERNAL) - Long
	 * classCom =====> clase de inversion a la que pertenece esta comisión (BOLSA, RENTA FIJA, ETC) (REQUIRED) - String size15
	 * fix ==========> Cantidad fija cobrada por operacion (REQUIRED) - double
	 * percent ======> Cantidad porcentual s/nominal cobrada por operacion (REQUIRED) - double
	 * tax ==========> Cantidad porcentual de impuestos cobrado s/comision por operación (REQUIRED) - double
	 * otherCost ====> Cantidad fija cobrada por otro tipo de costes (REQUIRED) - double
	 ********************************************************************************************* */
	
	private static final long serialVersionUID = 1L;


	private long id;
	private String keyUser;
	private String keyAcc;
	private String nameAcc;
	private Identify userId;
	private String classCom;
	private double fix;
	private double percent;
	private double tax;
	private double otherCost;
	
	public Accounts() {
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

	public String getKeyAcc() {
		return keyAcc;
	}

	public void setKeyAcc(String keyAcc) {
		this.keyAcc = keyAcc;
	}

	public String getNameAcc() {
		return nameAcc;
	}

	public void setNameAcc(String nameAcc) {
		this.nameAcc = nameAcc;
	}

	public Identify getUserId() {
		return userId;
	}

	public void setUserId(Identify userId) {
		this.userId = userId;
	}

	public String getClassCom() {
		return classCom;
	}

	public double getFix() {
		return fix;
	}

	public double getPercent() {
		return percent;
	}

	public double getTax() {
		return tax;
	}

	public double getOtherCost() {
		return otherCost;
	}

	public void setClassCom(String classCom) {
		this.classCom = classCom;
	}

	public void setFix(double fix) {
		this.fix = fix;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public void setTax(double tax) {
		this.tax = tax;
	}

	public void setOtherCost(double otherCost) {
		this.otherCost = otherCost;
	}


   
}
