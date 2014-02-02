package control;


import java.io.Serializable;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.Transient;


import model.Operations;



/**
 * 
 *
 * @author musef
 * 
  * @version 2.3JSF 2013-01-31
 *
 */

@ManagedBean (name="historicoBean")
@SessionScoped
public class HistoricoBean implements Serializable{

	private static final long serialVersionUID = 1L;
	private String goToPage="historico.xhtml";
	private String goToList="listado.xhtml";
	
	private String initialDate;
	private String finalDate;
	private String account;
	private String title;
	private String[] type;
	private List<String>accountsList;
	private List<String>titlesList;
	private List<String>typesList;
	private List<Operations>consultaOper;
	private List<String[]>resultado;
    private String colorBackground;
	
	private String thisYear;
	DecimalFormat formatoDecimal=new DecimalFormat("#,###.00");
	private String delPushed;
	private String modPushed;
        @Transient
        private Utils utils;
	
        
        
	public HistoricoBean() {
		// CONSTRUCTOR
		
        // instanciamos la clase de herramientas utils
        utils=new Utils();
            
		// crea el formato de fecha a mostrar
		SimpleDateFormat fechaESP=new SimpleDateFormat("yyyy",new Locale("es"));
		// pasa el formato a string, cogiendo la fecha del sistema
		thisYear=fechaESP.format(new java.util.Date());
		initialDate="01-01-"+thisYear;
		finalDate="31-12-"+thisYear;
		
		// recopilamos las cuentas
		accountsList=new ArrayList<String>();
		if (InversionJSF.accountsInv!=null) {
			accountsList.add("Seleccionar todas...");
			for (int n=0;n<InversionJSF.accountsInv.length;n++ ) {
				accountsList.add(InversionJSF.accountsInv[n][1]);
			}		
		}
		// recopilamos los titulos negociados
		Set<String> titlesSet=new HashSet<String>();
		OperationsBean tit=new OperationsBean();
		titlesSet=tit.searchTitles(InversionJSF.keyUser);
		titlesList=new ArrayList<String>();
		titlesList.add("Seleccionar todos...");
                if (titlesSet!=null) {
                    Iterator<String>iter=titlesSet.iterator();
                    while (iter.hasNext()) {
                    titlesList.add(iter.next());
                    }
                }
		
		// recopilamos los tipos de operaciones
		typesList=new ArrayList<String>();
		typesList=tit.searchTipo();
			// activamos los checked
			// OJO: El tratamiento de este componente no es
			// dinamico, esta implementado de forma manual
        type=new String[2];        
        type[0]="COMPRA";
        type[1]="VENTA";
        
        //activamos el color de casillas del formulario
        colorBackground="";
		
	} // end method
	
	
	
	/**
	 * Este método prepara un listado List<Operations> de las operaciones efectuadas por el usuario,
	 * en función de los filtros recibidos en el formulario de consultas.
	 * 
	 * El listado lo almacena en una variable de instancia List<Operations>, y según tenga resultados
	 * o no en la búsqueda, redirecciona la navegación.
	 * 
	 * @param keyAccount - String con el keyAccount de la cuenta si se ha empleado el filtro en consultas.
	 * @param titulos - String con el titulo buscado si se ha empleado el filtro en consultas.
	 * @param fechaIni - String con la fecha inicial del filtro en consultas.
	 * @param fechaFin - String con la fecha final del el filtro en consultas.
	 * @param tipo - String con el tipo de operación (COMPRA O VENTA) si se ha empleado el filtro en consultas.
	 * 
	 * @return Retorna un String con la direccion a la que se redirige la página.
	 */
	
	protected String showInfo ( String keyAccount, String titulos, String fechaIni, String fechaFin, String tipoSelect[]) {
	
		// crea un nuevo objeto para busqueda de operaciones segun solicitud de formulario
		OperationsBean busqueda=new OperationsBean();
		consultaOper=null;
		
		// ordena las fechas de forma adecuada para busqueda sql
		Date fecha1=utils.convertToDate(fechaIni);		
		Date fecha2=utils.convertToDate(fechaFin);
                
                // si alguna de la fechas es null, se ignora la busqueda pero no avisa !!
                if (fecha1==null || fecha2==null) {
                    colorBackground="styleInputBackground";
                    return goToPage;
                }

        // seleccion del tipo en funcion de lo seleccionado en formulario
        String tipo="";
        int choice=tipoSelect.length;
        switch (choice) {
        	case 0: tipo="COMPRA";
        			break;
        	case 1: tipo=tipoSelect[0];
        			break;
        	default:tipo="Seleccionar todas...";
        }

		
		// se efectua una discriminacion por filtro utilizado mediante una valoracion
		// binaria, empleando un switch. Cada filtro tiene un valor bit posicional
		// de arriba abajo 4-2-1
		int eleccion=0;
		if (keyAccount.equals("Seleccionar todas...")) {
			eleccion+=4;
		}
		if (titulos.equals("Seleccionar todos...")) {
			eleccion+=2;
		}
		if (tipo.equals("Seleccionar todas...")) {
			eleccion+=1;
		}
		
		switch (eleccion) {
		
		case 7:
			// ****************** ningun filtro aplicado, se ven todas las operaciones
			consultaOper=busqueda.searchAllRecords(InversionJSF.keyUser, fecha1, fecha2);
			break;
		case 6:
			// ****************** dos filtros aplicados (compras o ventas de un usuario)
			consultaOper=busqueda.searchAllByType(InversionJSF.keyUser, tipo, fecha1, fecha2);
			break;
		case 5:
			// ****************** dos filtros aplicados (operaciones de un valor del usuario)
			consultaOper=busqueda.searchAllByTitle(InversionJSF.keyUser, titulos, fecha1, fecha2);			
			break;
		case 4:
			// ****************** un filtro aplicado (compras o ventas de un titulos del usuario)
			consultaOper=busqueda.searchAllBothTypeTitle(InversionJSF.keyUser, tipo, titulos, fecha1, fecha2);
			break;
		case 3:
			// ****************** dos filtros aplicados (operaciones de una cuenta de inversion de un usuario)
			consultaOper=busqueda.searchAllByAccount(InversionJSF.keyUser,keyAccount, fecha1, fecha2);
			break;
		case 2:
			// ****************** un filtro aplicado (compras o ventas de una cuenta de inversion del usuario)
			consultaOper=busqueda.searchAllBothAccountType(InversionJSF.keyUser, keyAccount, tipo, fecha1, fecha2);
			break;
		case 1:
			// ****************** un filtro aplicado (operaciones de un valor en una cuenta de inversion concreta)
			consultaOper=busqueda.searchAllBothTitleAccount(InversionJSF.keyUser, keyAccount, titulos, fecha1, fecha2);
			break;
		case 0:
			// ****************** todos los filtros aplicados (compras o ventas de un valor en una cuenta inversion concreta)
			consultaOper=busqueda.searchRecord(InversionJSF.keyUser, keyAccount, titulos, tipo, "BOLSA", fecha1, fecha2);
			break;
		} // fin del switch


		//****** despues de la consulta, si hay un null hay que salir del metodo
		
		if (consultaOper!=null) {
			// paso de atributos
			
			resultado=new ArrayList<String[]>();
			for (int k=0;k<consultaOper.size();k++) {
				String[] res=new String[7];
				res[0]=String.valueOf(consultaOper.get(k).getId());
				res[1]=utils.verificaFecha(String.valueOf(consultaOper.get(k).getFecha()));
				res[2]=consultaOper.get(k).getInvName();
				res[3]=consultaOper.get(k).getTypeOper();
				res[4]=utils.numeroSp(consultaOper.get(k).getNumberTit());
				res[5]=utils.numeroSp(consultaOper.get(k).getPrizeTit());;
				res[6]=utils.numeroSp(consultaOper.get(k).getCostTit());
				resultado.add(res);
			}
			
			return goToList;
		}
			
		return goToPage;
		

	} // fin del metodo showInfo
	
	
	

	
	
	
	public String deleteOp () {
		
		// creamos los filtros de seleccion con la informacion de la ddbb
					
		// el proceso consistira en tomar el id de la operacion incrustado en el boton

		OperationsBean ib=new OperationsBean();
		
		if (ib.deleteOperation(InversionJSF.keyUser,(long)Long.parseLong(delPushed))) {
			// todo correcto
		}

		return goToPage;
		
	} // fin del metodo deleteOp
	
	
	
	/**
	 * Este método llama al método showInfo y construye una consulta de operaciones
	 * efectuadas, con los datos del formulario de consultas.
	 * 
	 * En función del resultado, redirecciona a la pagina del listado o de nuevo
	 * a la de consultas.
	 * 
	 * @return Retorna un String con la direccion a la que se redirige la página.
	 */
	
	public String consultar() {
		
		// Se recibe el nombre de la cuenta, y el metodo necesita el keyAccount de la cuenta
		// Pero si no se ha filtrado por cuenta, hay que mantener el texto de account.
		String keyAccount=account;
		for (int n=0;n<InversionJSF.accountsInv.length;n++) {
			// solo si se ha filtrado por cuenta en el formulario de consultas
			// se obtendra el keyAccount
			if (account.equals(InversionJSF.accountsInv[n][1])) {
				keyAccount=InversionJSF.accountsInv[n][0];
			}
		}
		// instancia la consulta, la cual es guardada en una variable de instancia
		// devuelve la direccion a la cual se redirige la navegacion
		String go=showInfo(keyAccount,title,initialDate,finalDate,type);

		return go;
		
	} // end of method consultar
	
	
	
	/**
	 * Este método repone en sus valores iniciales a los filtros de consulta
	 * 
	 * @return Retorna un String con la redirección de la navegación.
	 */
	
	public String cancelar() {
		
		// ponemos las variables iniciales
		initialDate="01-01-"+thisYear;
		finalDate="31-12-"+thisYear;

		return goToPage;
	}
	
	
	
	/**
	 * Este método redirige la navegación hacia la página de consultas
	 * "historico.xhtml"
	 * 
	 * @return Retorna un String con la redirección de la navegación.
	 */
	
	public String goToHistorico() {
		return "historico.xhtml";
	}
	
	
	
	
	
	// ******* GETTERS AND SETTERS
	
	public String getInitialDate() {
		return initialDate;
	}
	public void setInitialDate(String initialDate) {
		this.initialDate = initialDate;
	}
	public String getFinalDate() {
		return finalDate;
	}
	public void setFinalDate(String finalDate) {
		this.finalDate = finalDate;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}



	public List<String> getAccountsList() {
		return accountsList;
	}

	public void setAccountsList(List<String> accountsList) {
		this.accountsList = accountsList;
	}

	public List<String> getTitlesList() {
		return titlesList;
	}

	public void setTitlesList(List<String> titlesList) {
		this.titlesList = titlesList;
	}

	public List<String> getTypesList() {
		return typesList;
	}

	public void setTypesList(List<String> typesList) {
		this.typesList = typesList;
	}

	public List<Operations> getConsultaOper() {
		return consultaOper;
	}

	public void setConsultaOper(List<Operations> consultaOper) {
		this.consultaOper = consultaOper;
	}

	public List<String[]> getResultado() {
		return resultado;
	}

	public void setResultado(List<String[]> resultado) {
		this.resultado = resultado;
	}

	public String getGoToPage() {
		return goToPage;
	}

	public void setGoToPage(String goToPage) {
		this.goToPage = goToPage;
	}

	public String getGoToList() {
		return goToList;
	}

	public void setGoToList(String goToList) {
		this.goToList = goToList;
	}



	public String getDelPushed() {
		return delPushed;
	}



	public void setDelPushed(String delPushed) {
		this.delPushed = delPushed;
	}



	public String getModPushed() {
		return modPushed;
	}



	public void setModPushed(String modPushed) {
		this.modPushed = modPushed;
	}

    public String getColorBackground() {
        return colorBackground;
    }

    public void setColorBackground(String colorBackground) {
        this.colorBackground = colorBackground;
    }



	public String[] getType() {
		return type;
	}



	public void setType(String[] type) {
		this.type = type;
	}


	
}	// ******* END OF CLASS
