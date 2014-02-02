package control;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Bean to manage new user creating and recording
 *
 * @author musef
 * 
  * @version 2.3JSF 2013-01-31
 *
 */

@ManagedBean(name="nuevoUsuarioBean")
@SessionScoped
public class nuevoUsuarioBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String goToPage;
    private String result;
    private String login;
    private String password;
    private String name;
    private String address;
    private String city;
    private String codpost;
    private String nif;
    private String email;
    private String phone;
    
    private String message;

    
    
    public nuevoUsuarioBean() {
        // CONSTRUCTOR
         
        goToPage="nuevoUsuario.xhtml";
        result="INCORRECTO";
        login=null;
        password=null;
        name=null;
        address=null;
        city=null;
        codpost=null;
        nif=null;
        email=null;
        phone=null;
        

    } //end of constructor

    
    /**
     * Método que graba en DDBB a un nuevo usuario, con los datos recogidos
     * en el formulario de creación.
     * Se realiza una revisión de la corrección de los datos recibidos.
     * 
     * @return - Retorna un String con la página de destino de la navegación de la aplicación. 
     */
    
    public String createNewUser() {
       	
	/*
        * First, check to avoid duplicating login
        * If not, collect the fields form and check if all is correct
        * Finally, return to new user creating form showing the result
        */    
			
	// instanciate method to checking duplicates logins
	IdentifyBean idtf=new IdentifyBean();
		
	if (!idtf.existLoginUser(login)) {
            // login is not duplicate
            // check the fields form
            if (checkForm(getLogin(), getPassword(), getName(), getAddress(), getCity(), getCodpost(), getNif(), getEmail(), getPhone())) {
		// instance createUser method
            	UsersBean newUser=new UsersBean();
		if (newUser.createUser(getLogin(), getPassword(), getName(), getAddress(), getCity(), getCodpost(), getNif(), getEmail(), getPhone())!=-1) {
                    // success new user creating and go back to creating form
                    setResult("OK");
                    message="alert('GRABACIÓN EFECTUADA CORRECTAMENTE\nIdentifiquese para entrar en la aplicación.');";
		} else {
                       // if any problem to record, it refuse the new user and back to creating form
                        // checking result as wrong
                            setResult("WRONG");
                            message="alert('ERROR EN GRABACIÓN. REVISE EL FORMULARIO\nSi persiste el problema contacte"
                                    + "con su distribuidor.');";
		}	
            } else {
		// incorrect form, there are any incorrect filling inputFields
		setResult("INCORRECT");
                message="alert('ERROR EN FORMULARIO. REVISE EL FORMULARIO\nAlgún campo del formulario"
                                    + "no está relleno o incumple los requisitos.');";
            }
			
        } else {
            // refuse process by duplicate login
            setResult("DUPLICATE");
            message="alert('USUARIO DUPLICADO. LOGIN INCORRECTO\nCambie el login por otro distinto.');";
        }
		

        
	// back to new user creating form to show the result
        return goToPage;
        
    } // end of method createNewUser
    
    
    
    /**
     * Método que comprueba la corrección de los datos recibidos y su adecuación
     * a los requerimientos para ser grabados en la DDBB.
     * 
     * @param login - Login del usuario para conexión
     * @param password - Password del usuario para conexión
     * @param name - Nombre completo del usuario
     * @param address - Dirección del usuario
     * @param city - Localidad de residencia
     * @param codpost - Código postal (5 número obligatorios)
     * @param nif - Nif o pasaporte del usuario (9 dígitos obligatorios)
     * @param email - OPCIONAL: email del usuario
     * @param phone - OPCIONAL: teléfono del usuario
     * 
     * @return - Retorna un boolean (true o false) con el resultado de la 
     * verificación del formulario.
     */
    
    private boolean checkForm(String login,String password,String name,String address,
			String city,String codpost,String nif,String email,String phone) {
		
	boolean correct=true;
		
	// check component form
        // if wrong, then return false
	if (login.length()<6 || login.length()>15) {
		correct=false;
	}

	if (password.length()<6 || password.length()>15) {
		correct=false;
	}
		
	if (name.length()<6 || name.length()>50) {
		correct=false;
	}
		
	if (address.length()<5 || address.length()>50) {
		correct=false;
	}
		
	if (codpost.length()!=5) {
		correct=false;
	} else {
		try {
			@SuppressWarnings("unused")
			int codepost=(int)Integer.parseInt(codpost);
		} catch (NumberFormatException e) {
			correct=false;
		}	
	}
		
	if (city.length()<3 || city.length()>25) {
		correct=false;
	}
		
	if (nif.length()!=9) {
		correct=false;
	}

	if (email.length()>40) {
		correct=false;
	}
		
	if (phone.length()>9) {
		correct=false;
	}
		
	return correct;
    
    } // end of method checkForm

    
    
    /**
     * Este método retorna a la pantalla de entrada para identificación.
     * 
     * @return - Retorna un String con la dirección de navegación de la aplicación.
     */
    
    public String cancelForm() {
        goToPage="index.xhtml";
        return goToPage;
    }
    
    
    
    // GETTERS AND SETTERS
    

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
} // ******* END OF CLASS 
