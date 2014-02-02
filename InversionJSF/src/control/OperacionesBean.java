package control;


import java.io.Serializable;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;

import model.Operations;



/**
 * 
 *
 * @author musef
 * 
  * @version 2.3JSF 2013-01-31
 *
 */

@ManagedBean (name="operacionesBean")
@SessionScoped

public class OperacionesBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String goToPage="operaciones.xhtml";
	private String goToHistorico="historico.xhtml";
	private String mensaje;
	private boolean showMessage;
	private String modPushed;
	private Utils utils;
	
	private boolean disabledComponents;
	private List<String> accountsList;
	private String accountSelection;
	private String accountName;
	private String nameTit;
	private String dateOper;
	private String typeSelection;
	private String numberTit;
	private String prizeTit;
	private String costOper;
	private String totalOper;
	
	private String resultado;
	private String errores[];
	
	

	public OperacionesBean() {
		// CONSTRUCTOR
		
		// instanciate utils
		utils=new Utils();
		
		// enabling components
		disabledComponents=true;
		mensaje="";
		showMessage=true;
		errores=new String[6];
		typeSelection="COMPRA";
		
		// we will get account list	
		accountsList=new ArrayList<String>();
		if (InversionJSF.accountsInv!=null) {
			accountsList.add("Seleccione...");
			for (int n=0;n<InversionJSF.accountsInv.length;n++ ) {
				accountsList.add(InversionJSF.accountsInv[n][1]);
			}		
		}	
		
	}	// end of constructor
	
	

	/**
	 * Método que gestiona el cambio de cuenta de inversión en el formulario
	 * de operaciones.
	 * 
	 * @param e - Recibe el evento de cambio de cuenta.
	 */
	
	public void changeAccount (ValueChangeEvent e) {
		
		Object source=e.getNewValue().toString();
		
		for (int n=0;n<InversionJSF.accountsInv.length;n++ ) {
			if (source.equals(InversionJSF.accountsInv[n][1])) {
				accountName=InversionJSF.accountsInv[n][1];
			}
		}	
		
	} // end of method changeAccount
	
	
	
	/**
	 * Método que gestiona el cambio de tipo de operación en el formulario
	 * de operaciones.
	 * 
	 * @param e - Recibe el evento de cambio de cuenta.
	 */
	
	public void changeType (ValueChangeEvent e) {
		
		String source=e.getNewValue().toString();
		typeSelection=source;
		
	} // end of method changeType
	
	
	

	
	public String modifyOperation () {
		
		
		// el proceso consistira en tomar el id de la operacion incrustado en el boton
		// y utilizarlo en el metodo searchOppById
		OperationsBean opBean=new OperationsBean();
		Operations opToModify=opBean.searchOppById((long)Long.parseLong(modPushed));
		
		if (opToModify!=null) {
			
			// ha encontrado el objeto a modificar
			
			// obtiene el keyAccount de la opToModifyacion y a partir de ahí
			// busca el nombre de la cuenta
			String keyAcc=opToModify.getKeyAcc();
			for (String[]acc : InversionJSF.accountsInv ) {
				if (keyAcc.equals(acc[0])) {
					accountName=acc[1];
				}
			}
			
			// obtiene el resto de la informacion
			nameTit=opToModify.getInvName();
			dateOper=opToModify.getFecha().toString();
			typeSelection=opToModify.getTypeOper();
			numberTit=String.valueOf(opToModify.getNumberTit());
			prizeTit=utils.numeroSp(opToModify.getPrizeTit());
			totalOper=utils.numeroSp(opToModify.getCostTit());	
			costOper=utils.numeroSp((opToModify.getCostTit())-(opToModify.getNumberTit()*opToModify.getPrizeTit()));
					
		} else {
			// en caso de error retorna al filtro
			return goToHistorico;
		}

		// redirige la navegacion hacia la pagina operations
		return goToPage;
		
		
	} // end of method modifyOperation
	
	
	
	/**
	 * Método que gestiona la grabación de los datos del formulario. Realiza una
	 * comprobación de los datos del cuestionario antes de realizar la grabación.
	 * 
	 * @return - Retorna un String con la redirección de la navegación.
	 */
	
	public String recordForm() {
		
		/*
		 * Este metodo solo verifica que existen los valores para grabar en DDBB y que son adecuados.
		 * Por lo tanto, realizara las comprobaciones y si todo es OK, instanciara el metodo
		 * de grabacion de operaciones.
		 * Devolvera el control al formulario con el resultado de la operacion, que mostrara el 
		 * javascript de la pagina jsp 
		 */
		
		// creamos la variable que informa del resultado de la validacion del formulario
		// inicializamos la variable a correcto
		resultado="RECORDED";
		errores[0]="";
		errores[1]="";
		errores[2]="";
		errores[3]="";
		errores[4]="";
		errores[5]="";
		
		
		// Comprobamos que existe un numero en titulos
		// en numero double. Luego los multiplicamos y obtenemos el coste Neto
		double num=0;
		String number=null;
		if (numberTit!=null) {
			number=numberTit.trim();
			// devuelve el numero en double o cero
			num=getNumber(number);
		} 
		
		if (num==0) {
			// marca como erroneo el formulario y la casilla correspondiente
			resultado="INCORRECTO";
			errores[3]="styleInputBackground";
		}
		
		double pri=0;
		String prize=null;
		
		if (prizeTit!=null) {
			prize=prizeTit.trim();
			// devuelve el numero en double o cero
			pri=getNumber(prize);
		} 
		
		if (pri==0) {
			// marca como erroneo el formulario y la casilla correspondiente
			resultado="INCORRECTO";
			errores[4]="styleInputBackground";
		}
	
		
		// obtenemos el nombre de la cuenta de inversion para poder obtener
		// el keyAccount de esa cuenta
		String account="";
		String keyAccount="";
		if (accountName!=null) {
			account=accountName.trim();
		} else {	
			resultado="INCORRECTO";
			errores[0]="styleInputBackground";
		}
		
		for (int n=0;n<InversionJSF.accountsInv.length;n++) {
			if (InversionJSF.accountsInv[n][1].equals(account)) {
				// si la cuenta se llama igual que la registrada en formulario...
				keyAccount=InversionJSF.accountsInv[n][0];
			}
		}
		
		// recogemos del formulario el resto de datos necesarios y hacemos
		// las comprobaciones si procede
		String select=typeSelection.trim();

		
		// finalmente comprobamos el resto del formulario
		// comprobamos si existe nombre de titulo
		String nameTitle="";
		if (nameTit!=null) {
			nameTitle=nameTit.trim().toUpperCase();
		} 
		
		if (nameTitle.isEmpty() || nameTitle.length()<3 || nameTitle.length()>25 ){
			// marca como erroneo el formulario y la casilla correspondiente
			resultado="INCORRECTO";
			errores[1]="styleInputBackground";
		}
		
		// revisamos la fecha y luego la transformamos a formato DDBB
		Date dateToRecord=null;
		String fechaOp=verificaFecha(dateOper.trim());
		if (fechaOp.isEmpty()) {
			// si la fecha devuelta esta vacia es que ha habido un error
			// marca como erroneo el formulario y la casilla correspondiente
			resultado="INCORRECTO";
			errores[2]="styleInputBackground";
		} else {
			// llegados aqui tenemos una fecha en formato dd-mm-aaaa verificada
			// por lo tanto la cambiamos al formato aaaa-mm-dd
			String fechaDB=fechaOp.substring(6)+fechaOp.substring(2, 6)+fechaOp.substring(0,2);
			dateToRecord=Date.valueOf(fechaDB);
		}
		
		double total=0;
		String importeTotal=null;
		
		if (totalOper!=null) {
			importeTotal=totalOper.trim();
			// devuelve el numero en double o cero
			total=getNumber(importeTotal);
		} 
		
		if (total==0) {
			// marca como erroneo el formulario y la casilla correspondiente
			resultado="INCORRECTO";
			errores[5]="styleInputBackground";
		}
		
		
		// creamos un array para los datos de la operacion y lo rellenamos con los calculos
		// obtenidos anteriormente
		String[] operationData=new String[8];
		operationData[0]=account;
		operationData[1]=nameTitle;
		operationData[2]=fechaOp;
		operationData[3]=select;
		operationData[4]=number;
		operationData[5]=prize;
		operationData[6]=costOper.trim();
		operationData[7]=importeTotal;
		
		//obtenemos un array de las cuentas de inversion existentes
		String cuentas[][]=InversionJSF.accountsInv;
		// creamos un list con los nombres de las cuentas
		List <String> accountsList=new ArrayList<String>();
		for (int n=0;n<cuentas.length;n++) {
			accountsList.add(cuentas[n][1]);
		}
		
		// Una vez realizadas todas las comprobaciones, creados todos los atributos y
		// preparadas todas las variables, hay que grabar si no hay errores detectados
		// en el formulario
		showMessage=true;
		if (resultado=="RECORDED") {
			OperationsBean grabaOperacion=new OperationsBean();
			if (grabaOperacion.recordOperation(keyAccount, "BOLSA", InversionJSF.keyUser, nameTit, number, prize, importeTotal, dateToRecord, select)) {
				mensaje="alert('Operación grabada correctamente');";
			} else {
				mensaje="alert('Error en la grabación del formulario: Revise los datos');";
			}
		} else {
			mensaje="alert('Error en los datos del formulario: Revise la información');";
		}
		
		// retornamos al formulario
		return goToPage;
		
		
	} // fin del metodo recordForm

	
	
	/**
	 * Método que gestiona el c�lculo de los gastos de la operación a grabar. 
	 * Utiliza para el c�lculo los datos de las comisiones correspondiente a la
	 * cuenta de inversión elegida.
	 * 
	 * @return - Retorna un String con la redirección de la navegación.
	 */
	
	public String calcular() {
		
		// create and start message variables
		resultado="OK";
		errores[0]="";
		errores[1]="";
		errores[2]="";
		errores[3]="";
		errores[4]="";
		errores[5]="";
		
		// first we calc net amount of operation
		// for that, we take the form data and we convert it in double number
		// then, we multiply all and we have the net cost
		double num=0;
		String number=null;
		if (numberTit!=null) {
			number=numberTit.trim();
			// devuelve el numero en double o cero
			num=getNumber(number);
		} 
		
		if (num==0) {
			// marca como erroneo el formulario y la casilla correspondiente
			resultado="INCORRECTO";
			errores[3]="styleInputBackground";
		}
		
		double pri=0;
		String prize=null;
		
		if (prizeTit!=null) {
			prize=prizeTit.trim();
			// devuelve el numero en double o cero
			pri=getNumber(prize);
		} 
		
		if (pri==0) {
			// marca como erroneo el formulario y la casilla correspondiente
			resultado="INCORRECTO";
			errores[4]="styleInputBackground";
		}
	
		// net cost
		double costeNeto=num*pri;

		
		// Now, we have to calc cost operations.
		// Para ello, tenemos que coger la cuenta donde se va a hacer la operacion,
		// obtener sus costes y calcular los costes de la operacion
		
		double fix=0;
		double percent=0;
		double tax=0;
		double otherCost=0;
		double totalGastos=0;
		
		// obtenemos el nombre de la cuenta de inversion para poder acceder
		// a los gastos de operar con esa cuenta
		String account="";
		if (accountName!=null) {
			account=accountName.trim();
		} else {	
			resultado="INCORRECTO";
			errores[0]="styleInputBackground";
		}
		
		for (int n=0;n<InversionJSF.accountsInv.length;n++) {
			if (InversionJSF.accountsInv[n][1].equals(account)) {
				// si la cuenta se llama igual que la registrada en formulario...
					// fix es un coste fijo por operacion
				fix=getNumber(InversionJSF.accountsInv[n][2]);
					// percent es un coste porcentual por el coste Neto de operacion
				percent=costeNeto*(getNumber(InversionJSF.accountsInv[n][3]))/100;
					// otherCost son otros costes fijos por operacion correspondientes a terceros
				otherCost=getNumber(InversionJSF.accountsInv[n][5]);
					// tax es un impuesto porcentual sobre el total de los gastos de la operacion
				tax=(fix+percent+otherCost)*(getNumber(InversionJSF.accountsInv[n][4])/100);
					// totalGastos recoge el idem
				totalGastos=fix+percent+otherCost+tax;
			}
		}
		
		// por ultimo, los gastos se sumaran al total si son una compra o se restaran
		// si se trata de una venta
		String select=typeSelection.trim();
		if (!select.equals("COMPRA")) {
			// cambiamos el signo si no es compra
			totalGastos=totalGastos*(-1);
		}
		// el importe total de la operacion
		double importeTotal=costeNeto+totalGastos;
		
		// finalmente comprobamos el resto del formulario
		// comprobamos si existe nombre de titulo
		String nameTitle="";
		if (nameTit!=null) {
			nameTitle=nameTit.trim().toUpperCase();
		} 
		
		if (nameTitle.isEmpty() || nameTitle.length()<3){
			// marca como erroneo el formulario y la casilla correspondiente
			resultado="INCORRECTO";
			errores[1]="styleInputBackground";
		}
		
		// revisamos la fecha y la transformamos a formato español
		String fechaOp=verificaFecha(dateOper.trim());
		if (fechaOp.isEmpty()) {
			// si la fecha devuelta esta vacia es que ha habido un error
			// marca como erroneo el formulario y la casilla correspondiente
			resultado="INCORRECTO";
			errores[2]="styleInputBackground";
		}
		
		// rellenamos los datos con los calculos efectuados
		costOper=nf(totalGastos);
		totalOper=nf(importeTotal);	

		return goToPage;
		
	} // end of method calcular
	
	

	/**
	 * Este método borra los datos del formulario y lo resetea dejandolo
	 * en blanco.
	 * 
	 * @return - Retorna un String con la redirección de la navegación.
	 */
	
	public String cancelar() {
		
		// create and start message variables
		resultado="OK";
		errores[0]="";
		errores[1]="";
		errores[2]="";
		errores[3]="";
		errores[4]="";
		errores[5]="";
		// initialize any components of form
		nameTit="";
		dateOper="";
		numberTit="0,00";
		prizeTit="0,00";
		costOper="0,00";
		totalOper="0,00";
		
		return goToPage;
		
	} // end of method cancelar
	
	
	
	// REDIRECTION PAGE
	
	public String goToOperaciones() {
		return goToPage;
	}
	
	
	
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
	
	
	
	/**
	 * Este metodo transforma un numero double a un numero String en formato español con dos
	 * decimales (#.###,00).
	 * 
	 * @param numberDouble - El numero double a cambiar al formato español #.###,00
	 * @return Retorna un string que representa un numero en formato español
	 */
	
	protected String nf(double numberDouble) {
		
		String number=null;
		
		// formato de numeros a local
		Locale loc=new Locale("es", "ES");
		NumberFormat f = NumberFormat.getInstance(loc);
		if (f instanceof DecimalFormat) {
		     ((DecimalFormat) f).setDecimalSeparatorAlwaysShown(true);
		}
		f.setMaximumFractionDigits(2);
		f.setMinimumFractionDigits(2);
		double db=((double)Math.round(numberDouble*100))/100;
		number=f.format(db);
		
		return number;
		
	} // fin del metodo nf
	
	
	
	/**
	 * Metodo que comprueba que la fecha a verificar corresponde al patrón
	 * necesario de dd-mm-aaaa. Adicionalmente admite el patrón de base de datos
	 * aaaa-mm-dd.
	 * 
	 * @param fecha - Es el string con la fecha a verificar como válida.
	 * @return Devuelve un string con la fecha en formato dd-mm-aaaa o "" si la
	 * fecha comprobada no corresponde al patr�n determinado.
	 */
	
	protected String verificaFecha(String fecha) {
		
		
		String fechaDev="";
		
		// si la cadena no mide 10 no cumple el formato dd-mm-aaaa
		if (fecha.length()!=10) {
			return fechaDev;
			
		}
		
		// primero buscamos la recurrencia de los guiones
		int pos1=fecha.indexOf("-");
		int pos2=-1;
		if (pos1!=-1) {
			pos2=fecha.indexOf("-", pos1+1);
		}
		
		if (pos1==-1 && pos2==-1) {
			// si no hay guiones, comprobaremos si se han 
			// metido barras "/" para separar las fechas
			pos1=fecha.indexOf("/");
			pos2=-1;
			if (pos1!=-1) {
				pos2=fecha.indexOf("/", pos1+1);
			}
			
			if (pos1==-1 && pos2==-1) {
				// si tampoco hay "/" como separadores, damos la
				// cadena como no valida de fechas y retornamos vacio
				return fechaDev;
			}
			
		}
		
		if (pos1==-1 || pos2==-1) {
			// si alguno de ellos es -1 es que no hay dos separadores
			// y por tanto no contiene una fecha
			return fechaDev;
		}
		
		// procedemos a obtener las cadenas
		String dia=fecha.substring(0, pos1);
		String mes=fecha.substring(pos1+1, pos2);
		String anno=fecha.substring(pos2+1);

		try {
			
			if (dia.length()<3 && (int)Integer.parseInt(dia)<32 &&
					mes.length()<3 && (int)Integer.parseInt(mes)<13 &&
					anno.length()==4 && (int)Integer.parseInt(anno)>2000) {
				// la fecha tiene el formato de tamaño y longitud adecuado
				// y esta en formato español
				fechaDev=dia+"-"+mes+"-"+anno;
			} else {
				// y si no, intentamos el formato americano a ver que tal
				if (dia.length()==4 && (int)Integer.parseInt(dia)>2000 &&
						mes.length()<3 && (int)Integer.parseInt(mes)<13 &&
						anno.length()<3 && (int)Integer.parseInt(anno)<32) {
					// la fecha tiene el formato de tamaño y longitud adecuado
					// y pero esta en formato americano, y hay que invertirla
					fechaDev=anno+"-"+mes+"-"+dia;
				}
			}
			
		} catch (Exception ex){
			// si salta el error de conversion de los int
			// es la cadena no es una fecha
			return fechaDev;
		}

		// devuelve sin errores y/o sin excepciones la fecha o un null
		return fechaDev;
	}
	
	
	// GETTERS AND SETTERS
	
	public boolean isDisabledComponents() {
		return disabledComponents;
	}

	public void setDisabledComponents(boolean disabledComponents) {
		this.disabledComponents = disabledComponents;
	}

	public String getAccountSelection() {
		return accountSelection;
	}

	public void setAccountSelection(String accountSelection) {
		this.accountSelection = accountSelection;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getNameTit() {
		return nameTit;
	}

	public void setNameTit(String nameTit) {
		this.nameTit = nameTit;
	}

	public String getDateOper() {
		return dateOper;
	}

	public void setDateOper(String dateOper) {
		this.dateOper = dateOper;
	}

	public String getTypeSelection() {
		return typeSelection;
	}

	public void setTypeSelection(String typeSelection) {
		this.typeSelection = typeSelection;
	}

	public String getNumberTit() {
		return numberTit;
	}

	public void setNumberTit(String numberTit) {
		this.numberTit = numberTit;
	}

	public String getPrizeTit() {
		return prizeTit;
	}

	public void setPrizeTit(String prizeTit) {
		this.prizeTit = prizeTit;
	}

	public String getCostOper() {
		return costOper;
	}

	public void setCostOper(String costOper) {
		this.costOper = costOper;
	}

	public String getTotalOper() {
		return totalOper;
	}

	public void setTotalOper(String totalOper) {
		this.totalOper = totalOper;
	}

	public List<String> getAccountsList() {
		return accountsList;
	}

	public void setAccountsList(List<String> accountsList) {
		this.accountsList = accountsList;
	}


	public String getMensaje() {
		if (showMessage) {
			showMessage=false;
		} else {
			mensaje="";
		}
		return mensaje;
	}


	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}



	public boolean isShowMessage() {
		return showMessage;
	}



	public void setShowMessage(boolean showMessage) {
		this.showMessage = showMessage;
	}



	public String getResultado() {
		return resultado;
	}



	public void setResultado(String resultado) {
		this.resultado = resultado;
	}



	public String[] getErrores() {
		return errores;
	}



	public void setErrores(String[] errores) {
		this.errores = errores;
	}



	public String getModPushed() {
		return modPushed;
	}



	public void setModPushed(String modPushed) {
		this.modPushed = modPushed;
	}
}
