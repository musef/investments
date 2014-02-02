package control;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * 
 *
 * @author musef
 * 
  * @version 2.3JSF 2013-01-31
 *
 */

@ManagedBean (name="personalBean")
@SessionScoped
public class PersonalBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	// atributos
	private String id;
	private String login;
	private String passw;
	private String name;
	private String address;
	private String city;
	private String codpost;
	private String nif;
	private String email;
	private String phone;
	
	// componentes
	private String nombreBotonModificar;
	private String titleBotonModificar;
	private boolean disabledComponent;
	
	// mensajes
	private String resultado;
	private String[] errores;
	private String mensaje;
	private boolean showMessage;
	
	
	
	public PersonalBean() {
		// CONSTRUCTOR
		
		// inicializamos los componentes
		nombreBotonModificar="Modificar";
		titleBotonModificar="Pulse para modificar los datos personales";
		disabledComponent=true;
		mensaje="";
		showMessage=true;
		// cogemos los valores del usuario y los mostramos
		id=String.valueOf(InversionJSF.thisUser.getId());
		login=InversionJSF.thisUser.getUser();
		passw=InversionJSF.thisUser.getPassword();
		name=InversionJSF.infoUser.getName();
		address=InversionJSF.infoUser.getAddress();
		city=InversionJSF.infoUser.getCity();
		codpost=InversionJSF.infoUser.getCodpost();
		nif=InversionJSF.infoUser.getNif();
		email=InversionJSF.infoUser.getEmail();
		phone=InversionJSF.infoUser.getPhone();
		
	}
	
	
	
	/**
	 * Este metodo se ejecuta al ser llamado por primera vez. Prepara el
	 * formulario para visualización, deshabilitando componentes y poniendo
	 * el boton en Modificar.
	 * 
	 * @return retorna la página del formulario para mostrar los datos.
	 */
	
	public String goToPersonal() {
		
		nombreBotonModificar="Modificar";
		titleBotonModificar="Pulse para modificar los datos personales";
		disabledComponent=true;
		mensaje="";
		
		return "personal.xhtml";
	}
	
	
	
	/**
	 * Este método, en función de su nombre, o bien habilita el formulario para modificacion
	 * o se redirecciona para grabación de datos.
	 * 
	 * Si el botón"Modificar", entonces habilita el formulario para modificación,
	 * permitiendo el acceso a los componentes y cambiando su propio nombre a "Grabar".
	 * Si el botón"Grabar", entonces se ejecuta el método grabar.
	 * 
	 * @return retorna la pagina del formulario para modificar o mostrar el contenido.
	 */
	
	public String modificar() {
		
		if (nombreBotonModificar.equals("Modificar")) {
			// habilitamos para modificacion
			nombreBotonModificar="Grabar";
			titleBotonModificar="Pulse para grabar los nuevos datos";
			disabledComponent=false;
			mensaje="";
		} else {
			// pasamos a grabar el formulario
			grabar();
		}
	
		return "personal.xhtml";
	}
	
	
	
	/**
	 * Este método retorna al formulario a su original, rechazando los cambios
	 * efectuados.
	 * 
	 * @return retorna la página del formulario para mostrar los datos.
	 */
	
	public String cancelar() {
		
		nombreBotonModificar="Modificar";
		titleBotonModificar="Pulse para modificar los datos personales";
		disabledComponent=true;
		showMessage=true;
		mensaje="alert('Modificación cancelada. Los datos no se han grabado.');";
		
		return "personal.xhtml";
	}
	
	
	
	/**
	 * Este método efectúa la grabación de los datos del formulario en la DDBB.
	 * Realiza la comprobación rutinaria de los elementos del formulario, y alerta
	 * de los errores.
	 * Una vez grabado, habilita el formulario para su visualización.
	 * 
	 * @return retorna la página del formulario para mostrar los nuevos datos.
	 */
	
	public String grabar() {
		
		// cogemos los datos, los revisamos y los preparamos para grabacion
		
		// creamos la variable que informa del resultado de la validacion del formulario
		// inicializamos la variable a correcto
		resultado="OK";
		errores=new String[9];
		
		
		// transformamos el id String en long
		long idUser;
		try {
			idUser=(long)Long.parseLong(id);
		} catch (Exception ex) {
			idUser=0;
			resultado="WRONG";
			errores[0]="RED";
		}
		
		// Revisamos el login
		if (login.trim().length()<6 || login.trim().length()>15) {
			resultado="BAD";
			errores[1]="RED";
		}
		// revisamos la password
		if (passw.trim().length()<6 || passw.trim().length()>15) {
			resultado="BAD";
			errores[2]="RED";
		}
		// revisamos el nombre
		if (name.trim().length()<6 || name.trim().length()>50) {
			resultado="BAD";
			errores[3]="RED";
		}
		// revisamos la direccion
		if (address.trim().length()<5 || address.trim().length()>50) {
			resultado="BAD";
			errores[4]="RED";
		}
		// revisamos la poblacion
		if (city.trim().length()<3 || city.trim().length()>25) {
			resultado="BAD";
			errores[5]="RED";
		}
		// revisamos el codigo postal
		if (codpost.trim().length()!=5) {
			// no mide exactamente cinco digitos
			resultado="BAD";
			errores[6]="RED";
		} else {
			try {
				int cpost=(int)Integer.parseInt(codpost.trim());
				codpost=String.valueOf(cpost);
			} catch (Exception ex) {
				//no es un numero
				resultado="BAD";
				errores[6]="RED";
			}
		}
		// revisamos el nif
		if (nif.trim().length()!=9) {
			// no mide exactamente nueve digitos
			resultado="BAD";
			errores[7]="RED";
		}
		// email no obligatorio, pero menor que 41
		if (email.trim().length()>40) {
			resultado="BAD";
			errores[8]="RED";
		}
		// telefono no obligatorio, pero menor que 10
		if (phone.trim().length()>9) {
			resultado="BAD";
			errores[9]="RED";
		}
		
		// finalizado el proceso de comprobacion
			// instanciamos el metodo changeUser para realizar la modificacion
		showMessage=true;
		if (resultado.equals("OK")) {
			UsersBean dataUser=new UsersBean();
			dataUser.changeUser(idUser, login.trim(), passw.trim(), name.trim(), address.trim(), city.trim(), codpost.trim(), nif.trim(), email.trim(), phone.trim());
			mensaje="alert('Operación grabada correctamente');";
		} else {
			mensaje="alert('Error en los datos del formulario: Revise la información');";
		}
	
		// habilitando componentes del formulario
		nombreBotonModificar="Modificar";
		titleBotonModificar="Pulse para modificar los datos personales";
		disabledComponent=true;
		
		return "personal.xhtml";
	}
	
	
	
	
	// ****** GETTERS AND SETTERS

	public String getNombreBotonModificar() {
		return nombreBotonModificar;
	}


	public String getTitleBotonModificar() {
		return titleBotonModificar;
	}


	public boolean isDisabledComponent() {
		return disabledComponent;
	}


	public void setTitleBotonModificar(String titleBotonModificar) {
		this.titleBotonModificar = titleBotonModificar;
	}


	public void setDisabledComponent(boolean disabledComponent) {
		this.disabledComponent = disabledComponent;
	}
	
	public void setNombreBotonModificar(String nombreBotonModificar) {
		this.nombreBotonModificar = nombreBotonModificar;
	}


	public String getId() {
		return id;
	}
	
	public String getLogin() {
		return login;
	}


	public String getPassw() {
		if (disabledComponent) {
			return "**********";
		}
		return passw;
	}


	public String getName() {
		return name;
	}


	public String getAddress() {
		return address;
	}


	public String getCity() {
		return city;
	}


	public String getCodpost() {
		return codpost;
	}


	public String getNif() {
		return nif;
	}


	public String getEmail() {
		return email;
	}


	public String getPhone() {
		return phone;
	}

	public String[] getErrores() {
		return errores;
	}

	public String getMensaje() {
		if (showMessage) {
			showMessage=false;
		} else {
			mensaje="";
		}
		return mensaje;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void setLogin(String login) {
		this.login = login;
	}


	public void setPassw(String passw) {
		if (disabledComponent) {
			passw=this.passw;
		}
		this.passw = passw;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public void setCodpost(String codpost) {
		this.codpost = codpost;
	}


	public void setNif(String nif) {
		this.nif = nif;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setErrores(String[] errores) {
		this.errores = errores;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = "";
	}









	
}  // *********** END OF CLASS
