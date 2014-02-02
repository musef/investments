package model;

import java.io.Serializable;
import java.sql.Date;


/**
 * Entity implementation class for Entity: Operations
 *
 * @author musef
 * 
  * @version 2.3JSF 2013-01-31
 *
 */




public class Operations implements Serializable {

	/* ********************************************************************************************
	 * File Description
	 * 
	 * id ========> Identificador de la grabacion del registro usuario ACCOUNTS
	 * keyAcc ====> Clave identificativa de la cuenta de inversion de la operacion (INTERNAL)
	 * market ====> Nombre del mercado donde se realiza la operacion (REQUIRED)
	 * keyUser ====> Identificador del usuario que realiza la operacion (REQUIRED)
	 * invName ===> Nombre del activo objeto de la operacion (REQUIRED)
	 * numberTit => Numero de titulos de la operacion (REQUIRED)
	 * prizeTit ==> Coste individual por titulo
	 * costTit ===> Coste total, comisiones incluidas, de la operacion (REQUIRED)
	 * fecha =====> Fecha en que se realiza la operacion (REQUIRED)
	 * typeOper ==> Tipo de operación (COMPRA, VENTA) (REQUIRED)
	 ********************************************************************************************* */
	
	private static final long serialVersionUID = 1L;
	
	private long id;
	private String keyAcc;
	private String market;
	private String keyUser;
	private String invName;
	private int numberTit;
	private double prizeTit;
	private double costTit;
	private Date fecha;
	private String typeOper;

	
	
	public Operations() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getKeyAcc() {
		return keyAcc;
	}

	public void setKeyAcc(String keyAcc) {
		this.keyAcc = keyAcc;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getKeyUser() {
		return keyUser;
	}

	public void setKeyUser(String keyUser) {
		this.keyUser = keyUser;
	}

	public String getInvName() {
		return invName;
	}

	public void setInvName(String invName) {
		this.invName = invName;
	}

	public int getNumberTit() {
		return numberTit;
	}

	public void setNumberTit(int numberTit) {
		this.numberTit = numberTit;
	}

	public double getPrizeTit() {
		return prizeTit;
	}

	public void setPrizeTit(double prizeTit) {
		this.prizeTit = prizeTit;
	}
	
	public double getCostTit() {
		return costTit;
	}

	public void setCostTit(double costTit) {
		this.costTit = costTit;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getTypeOper() {
		return typeOper;
	}

	public void setTypeOper(String typeOper) {
		this.typeOper = typeOper;
	}
   
}
