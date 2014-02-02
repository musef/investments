package control;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;

import java.sql.Date;
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

@ManagedBean (name="cuentasBean")
@SessionScoped
public class CuentasBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String accountSelection;
	private List<String> accountsList;
	private String account;
	private String fix;
	private String percent;
	private String tax;
	private String otherCost;
	
	// componentes
	private boolean disabledComponents;
	private boolean disabledAnadir;
	private boolean disabledCambiar;
	private boolean disabledEliminar;
	private boolean disabledCancelar;
	
	private String nameBotonCambiar;
	private String onClickBotonCambiar;
	private String titleBotonCambiar;
        
        private String backGroundComponent1;
        private String backGroundComponent2;
        private String backGroundComponent3;
        private String backGroundComponent4;
        private String backGroundComponent5;
	
        @Transient
	private Utils utils;
	
	public CuentasBean() {
		// CONSTRUCTOR
		
                // instanciamos utilidades
                utils=new Utils();
            
		// recopilamos las cuentas inicialmente
		accountsList=new ArrayList<String>();
		
		if (InversionJSF.accountsInv!=null) {
			accountsList.add("Seleccione...");
                    for (String[] accountsInv : InversionJSF.accountsInv) {
                        accountsList.add(accountsInv[1]);
                    }		
		}
                
                // activando componentes del formulario
		account="";
		accountSelection=account;
		fix="";
		percent="";
		tax="";
		otherCost="";
                
                backGroundComponent1="";
                backGroundComponent2="";
                backGroundComponent3="";
                backGroundComponent4="";
                backGroundComponent5="";
		
		nameBotonCambiar="Cambiar";
		titleBotonCambiar="Pulse para cambiar los datos de la cuenta";
		onClickBotonCambiar="";
		
		disabledComponents=true;
		disabledAnadir=false;
		disabledCambiar=true;
		disabledEliminar=true;
		disabledCancelar=true;
		
	}	// end of constructor

	
	
	public void selectAccount(ValueChangeEvent e) {
		// registra los cambios en el comboBox de cuentas
		String source=e.getNewValue().toString();
		for (int n=0; n<InversionJSF.accountsInv.length; n++) {
			// busca cuenta por cuenta para cambiar los datos
			if (source.equals(InversionJSF.accountsInv[n][1])) {
				// encontrada la cuenta, modifica los datos a mostrar
				account=InversionJSF.accountsInv[n][1];
				fix=InversionJSF.accountsInv[n][2];
				percent=InversionJSF.accountsInv[n][3];
				tax=InversionJSF.accountsInv[n][4];
				otherCost=InversionJSF.accountsInv[n][5];
				accountSelection=account;
				// activacion de componentes
				nameBotonCambiar="Cambiar";
				disabledComponents=true;
				disabledAnadir=false;
				disabledCambiar=false;
				disabledEliminar=false;
				disabledCancelar=true;
                                backGroundComponent1="";
                                backGroundComponent2="";
                                backGroundComponent3="";
                                backGroundComponent4="";
                                backGroundComponent5="";
                                
                                // asignacion de la cuenta por defecto
                                InversionJSF.keyAccount=InversionJSF.accountsInv[n][0];
                                InversionJSF.account=n;
			}
		} 
		
		
	} // end of method selectAccount
	
	
	
	public String anadir() {
		
		disabledComponents=false;
		disabledAnadir=true;
		disabledCambiar=false;
		disabledEliminar=true;
		disabledCancelar=false;
                
                account="";
                accountSelection=account;
                fix="0.00";
                percent="0.00";
                tax="0.00";
                otherCost="0.00";
		
		nameBotonCambiar="Grabar";
		titleBotonCambiar="Pulse para grabar los datos de esta cuenta";
		onClickBotonCambiar="return confirm('¿ Desea grabar los datos de esta cuenta ?');";
		
		return "cuentas.xhtml";
		
	} // end of method anadir
	
	
	
	public String cambiar() {
		
            if (nameBotonCambiar.equals("Grabar")) {
		// ha pulsado grabar, por lo tanto
		// se deshabilitan componentes
		nameBotonCambiar="Cambiar";
		titleBotonCambiar="Pulse para cambiar los datos de la cuenta";
		onClickBotonCambiar="";
		
		disabledComponents=true;
		disabledAnadir=false;
		disabledCambiar=true;
		disabledEliminar=true;
		disabledCancelar=true;
                        
                // procesamos la informacion del formulario antes de grabar
		
                // es necesario comprobar si los valores numericos introducidos en la cuenta
                // relativos a fix, percent, tax y otherCost son numeros o no.
                // Para ello, vamos a utilizar getNumber el cual transformara los supuestos
                // string numeros a numero double o en caso de error a cero.
                // No se informara de los errores al usuario.
                double numbFix=utils.getNumber(fix);
                double numbPercent=utils.getNumber(percent);
                double numbTax=utils.getNumber(tax);
                double numbOther=utils.getNumber(otherCost);

                // String formato dos decimales español
                fix=utils.numeroSp(numbFix);
                percent=utils.numeroSp(numbPercent);
                tax=utils.numeroSp(numbTax);
                otherCost=utils.numeroSp(numbOther);
		
                // Comprobamos que existe nombre de cuenta y que la longitud es adecuada
                if (!(account.trim().isEmpty() || account.length()<3 || account.length()>25)) {
                   
                    // Llegados a este punto, tenemos revisados los campos a grabar 
                    
                    // si existe keyAccount se trata de una modificacion de cuenta, y si no existe
                    // entonces es que estamos creando una nueva cuenta
                    Boolean isNew=true;
                    for (String[] accounts:InversionJSF.accountsInv) {
                        if (account.equals(accounts[1])) {
                            // se trata de una modificacion
                            // instanciamos modifyAccount para cambiar cuenta
                            isNew=false;
                        }
                    }
                    if (isNew) {
                         // instanciamos makeAccount para crear cuenta
                        UsersBean nuevaCuenta=new UsersBean();
                        nuevaCuenta.makeAccount(account, InversionJSF.keyUser, InversionJSF.idUsuario, "BOLSA", fix, percent, tax, otherCost);
                    } else {
                        AccountsBean cambiaCuenta=new AccountsBean();
                        cambiaCuenta.modifyAccount(InversionJSF.keyUser, InversionJSF.keyAccount, "BOLSA", fix, percent, tax, otherCost);
                    }
                    
                    // realizada la grabacion o modificacion, actualizamos variables
                    // obtenemos un array de las cuentas de inversion. Previamente las
                    // actualizamos mediante getDataAccounts
                    AccountsBean datosCuentas=new AccountsBean();
                    InversionJSF.accountsInv=datosCuentas.getDataAccounts(InversionJSF.keyUser);
                    accountsList=new ArrayList<String>();
		
                    if (InversionJSF.accountsInv!=null) {
			accountsList.add("Seleccione...");
                        for (String[] accountsInv : InversionJSF.accountsInv) {
                            accountsList.add(accountsInv[1]);
                        }		
                    }
                    account="";
                    accountSelection=account;
                    fix="0.00";
                    percent="0.00";
                    tax="0.00";
                    otherCost="0.00";
                    
                } else {
                    // LA LONGITUD DEL NOMBRE DE LA CUENTA ES INCORRECTA
                    backGroundComponent1="styleInputBackground";
                    	// se mantienen componentes para modificacion
                    nameBotonCambiar="Grabar";
                    titleBotonCambiar="Pulse para grabar los datos de esta cuenta";
                    onClickBotonCambiar="return confirm('¿ Desea grabar los datos de esta cuenta ?');";
			
                    disabledComponents=false;
                    disabledAnadir=true;
                    disabledCambiar=false;
                    disabledEliminar=true;
                    disabledCancelar=false;
                    
                }                       
			
            } else {
		// ha pulsado cambiar, por lo tanto
		// se habilitan componentes para modificacion
			
		nameBotonCambiar="Grabar";
		titleBotonCambiar="Pulse para grabar los datos de esta cuenta";
		onClickBotonCambiar="return confirm('¿ Desea grabar los datos de esta cuenta ?');";
			
		disabledComponents=false;
		disabledAnadir=true;
		disabledCambiar=false;
		disabledEliminar=true;
		disabledCancelar=false;
            }
			 
		return "cuentas.xhtml";
		
	} // end of method cambiar
	
	
	
	public String eliminar() {
		
		disabledComponents=true;
		disabledAnadir=false;
		disabledCambiar=true;
		disabledEliminar=true;
		disabledCancelar=true;
             
				
		// instanciamos el metodo de borrado de la cuenta removeAccount
		AccountsBean datosCuentas=new AccountsBean();
		// Se comprueba si tiene operaciones de inversion efectuadas, porque si las tiene
		// no puede borrarse la cuenta sin borrar antes las operaciones
		if (compruebaOperaciones()) {
			// si no tiene operaciones puede borrarse
			datosCuentas.removeAccount(InversionJSF.keyUser, InversionJSF.keyAccount);
		} else {
			// redirigimos hacia una pagina de error porque no es posible borrar cuenta
			
		}
		
                    // realizado el borrado de cuenta, actualizamos variables
                    // obtenemos un array de las cuentas de inversion. Previamente las
                    // actualizamos mediante getDataAccounts
                    InversionJSF.accountsInv=datosCuentas.getDataAccounts(InversionJSF.keyUser);
                    accountsList=new ArrayList<String>();
		
                    if (InversionJSF.accountsInv!=null) {
			accountsList.add("Seleccione...");
                        for (String[] accountsInv : InversionJSF.accountsInv) {
                            accountsList.add(accountsInv[1]);
                        }		
                    }
                    account="";
                    accountSelection=account;
                    fix="0.00";
                    percent="0.00";
                    tax="0.00";
                    otherCost="0.00";
                
                
                
		return "cuentas.xhtml";
		
	} // end of method eliminar
	
	
	
	public String cancelar() {
            
            nameBotonCambiar="Cambiar";
            titleBotonCambiar="Pulse para cambiar los datos de la cuenta";
            onClickBotonCambiar="";
            disabledComponents=true;
            disabledAnadir=false;
            disabledCambiar=true;
            disabledEliminar=true;
            disabledCancelar=true;
		
            return "cuentas.xhtml";
		
	} // end of method cancelar
	
	
	
	public String goToCuentas() {
		
		return "cuentas.xhtml";
		
	} // end of method goToCuentas


        
        /**
	 * Este metodo comprueba si se han realizado operaciones en una cuenta de inversion
	 * de un cliente determinado. Es una comprobación previa al borrado de una cuenta.
	 * 
	 * @return Devuelve un boolean TRUE si puede borrarse o FALSE si tiene operaciones 
	 * y por lo tanto no puede eliminarse la cuenta de inversion
	 */
	
	private boolean compruebaOperaciones() {
		
		/*
		 * ESte metodo comprueba si se han realizado operaciones con la cuenta de inversion
		 * seleccionada. Para ello se instancia el metodo OperationsBean.searchAllByAccount
		 * que te devuelve todas las operaciones de la cuenta.
		 * 
		 * Controla y avisa de todos los errores.
		 * 
		 * Finalmente si es posible borrarse devuelve un TRUE, y si no es posible porque tiene
		 * datos devuelve un FALSE
		 */
		
		// creamos un objeto OperationsBean y un List para recoger resultados de la busqueda
		OperationsBean busca=new OperationsBean();
		List<Operations> resultado=new ArrayList<Operations>();
		
		// creamos variables date maxima y minima
		Date fechaIni=Date.valueOf("2000-01-01");
		Date fechaFin=Date.valueOf("2100-12-01");
		// realizamos la busqueda
		try {
			resultado=busca.searchAllByAccount(InversionJSF.keyUser, InversionJSF.keyAccount, fechaIni,fechaFin);
			if (resultado==null) {
				// error en el proceso de busqueda en OperationsBean
				return false;
			}
			if (resultado.isEmpty()) {
				// despues de leer en busca de operaciones no hay ninguna operacion de esta cuenta
				// por lo tanto la cuenta puede borrarse
				return true;
			}
		} catch (Exception er){
			// cualquier error en el proceso de este metodo
			return false;
		}
		
		// si se llega aqui es que no se han producido errores 
		// y que la cuenta tiene operaciones realizadas
		// por lo tanto no puede borrarse
		
		return false;
		
	} // fin del metodo compruebaOperaciones
        
        
        
        
        
	
	// ******* GETTERS AND SETTERS
	
	public String getAccountSelection() {
		return accountSelection;
	}


	public void setAccountSelection(String accountSelection) {
		this.accountSelection = accountSelection;
	}


	public String getAccount() {
		return account;
	}


	public void setAccount(String account) {
		this.account = account;
	}


	public String getFix() {
		return fix;
	}


	public void setFix(String fix) {
		this.fix = fix;
	}


	public String getPercent() {
		return percent;
	}


	public void setPercent(String percent) {
		this.percent = percent;
	}


	public String getTax() {
		return tax;
	}


	public void setTax(String tax) {
		this.tax = tax;
	}


	public String getOtherCost() {
		return otherCost;
	}


	public void setOtherCost(String otherCost) {
		this.otherCost = otherCost;
	}


	public boolean isDisabledComponents() {
		return disabledComponents;
	}


	public void setDisabledComponents(boolean disabledComponents) {
		this.disabledComponents = disabledComponents;
	}


	public boolean isDisabledAnadir() {
		return disabledAnadir;
	}


	public void setDisabledAnadir(boolean disabledAnadir) {
		this.disabledAnadir = disabledAnadir;
	}


	public boolean isDisabledCambiar() {
		return disabledCambiar;
	}


	public void setDisabledCambiar(boolean disabledCambiar) {
		this.disabledCambiar = disabledCambiar;
	}


	public boolean isDisabledEliminar() {
		return disabledEliminar;
	}


	public void setDisabledEliminar(boolean disabledEliminar) {
		this.disabledEliminar = disabledEliminar;
	}


	public boolean isDisabledCancelar() {
		return disabledCancelar;
	}


	public void setDisabledCancelar(boolean disabledCancelar) {
		this.disabledCancelar = disabledCancelar;
	}


	public String getNameBotonCambiar() {
		return nameBotonCambiar;
	}


	public void setNameBotonCambiar(String nameBotonCambiar) {
		this.nameBotonCambiar = nameBotonCambiar;
	}


	public String getOnClickBotonCambiar() {
		return onClickBotonCambiar;
	}


	public void setOnClickBotonCambiar(String onClickBotonCambiar) {
		this.onClickBotonCambiar = onClickBotonCambiar;
	}


	public String getTitleBotonCambiar() {
		return titleBotonCambiar;
	}


	public void setTitleBotonCambiar(String titleBotonCambiar) {
		this.titleBotonCambiar = titleBotonCambiar;
	}

	public List<String> getAccountsList() {
		return accountsList;
	}

	public void setAccountsList(List<String> accountsList) {
		this.accountsList = accountsList;
	}

    public String getBackGroundComponent1() {
        return backGroundComponent1;
    }

    public void setBackGroundComponent1(String backGroundComponent1) {
        this.backGroundComponent1 = backGroundComponent1;
    }

    public String getBackGroundComponent2() {
        return backGroundComponent2;
    }

    public void setBackGroundComponent2(String backGroundComponent2) {
        this.backGroundComponent2 = backGroundComponent2;
    }

    public String getBackGroundComponent3() {
        return backGroundComponent3;
    }

    public void setBackGroundComponent3(String backGroundComponent3) {
        this.backGroundComponent3 = backGroundComponent3;
    }

    public String getBackGroundComponent4() {
        return backGroundComponent4;
    }

    public void setBackGroundComponent4(String backGroundComponent4) {
        this.backGroundComponent4 = backGroundComponent4;
    }

    public String getBackGroundComponent5() {
        return backGroundComponent5;
    }

    public void setBackGroundComponent5(String backGroundComponent5) {
        this.backGroundComponent5 = backGroundComponent5;
    }
}
