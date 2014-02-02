package control;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.Transient;

import org.hibernate.HibernateException;
import org.hibernate.Session;



/**
 * 
 *
 * @author musef
 * 
  * @version 2.3JSF 2013-01-31
 *
 */

@ManagedBean
@SessionScoped
public class EnterSandMan implements Serializable {

	private static final long serialVersionUID = 1L;
    @Transient
    private CreateHbnSession mySessionFactory;
	
        // definicion de componentes
	private String user;
	private String passw;
	private String introducing;
	private String colorBackground1;
    private String colorBackground2;
	
        // redirecciones de navegacion
	private final String goToWrongIdentify="index.xhtml";
	private final String goToMainPage="personal.xhtml";
	private final String goToNewUser="nuevoUsuario.xhtml";
	private final String goToPersonal="personal.xhtml";
	private final String goToCuentas="cuentas.xhtml";
	private final String goToOperaciones="operaciones.xhtml";
	private final String goToHistorico="historico.xhtml";
	
	
        
        public EnterSandMan() {
            // CONSTRUCTOR
                // color de los inputs del formulario

        }
	
        
        
	/**
	 * Este método procesa la identificación del usuario, cogiendo el login-password
	 * introducidos en el formulario de entrada, verifica la identificación. En caso de
	 * no identificarse remite a una pagina de error.
	 * 
	 * Si se produce la identificación, recoge los datos del usuario, crea la frase de 
	 * bienvenida, y redirige la navegación hacia la aplicación principal. 
	 * 
	 * @return Devuelve la direccion de la pagina a donde redirige la navegación
	 * de la pagina.
	 */

	public String processIdentify () {
		
		/*
		 * Procesamos el formulario de identificacion en la aplicacion.
		 *   
		 *  Primeramente, recogemos el usuario y el password, e instanciamos el metodo identifyUser,
		 *  el cual nos devolvera si el usuario esta creado o no.
		 *  Si no esta creado devolvemos el error de identificacion.
		 *  Si esta creado e identificado, entonces procedemos a obtener sus datos de
		 *  usuario y nos pasamos a pantalla de menus.
		 *  
		 */

		// fijamos provisionalmente la direccion de salto
		String destinyPage=goToMainPage;
                
		// crea el formato de fecha a mostrar
		SimpleDateFormat fechaESP=new SimpleDateFormat("EEEE',' dd-MM-yyyy ",new Locale("es"));
		// pasa el formato a string, cogiendo la fecha del sistema
		String today=fechaESP.format(new Date());
		
		// recogemos los valores del formulario de identificacion
		String userLogin=user;
		String userPass=passw;
                
                // Creamos el objeto que generará la sessionFactory y permitira
                // la creacion de sesiones Hibernate
                mySessionFactory=new CreateHbnSession();
		
		// comprobamos idoneidad de datos recibidos
		if ( userLogin==null || userLogin.isEmpty() ) {
                    colorBackground1="styleInputBackground";
                    destinyPage=goToWrongIdentify;
        } else if ( userPass==null || userPass.isEmpty()) {
			// si alguno de los datos esta vacio redirigimos
			// hacia la pagina de no identificado
                    colorBackground2="styleInputBackground";
                    destinyPage=goToWrongIdentify;		
		} else {
			colorBackground1="";
            colorBackground2="";
			// si los datos de login y password no estan vacios,
			// creamos una sesion hibernate e instanciamos el bean
			IdentifyBean userSearched=new IdentifyBean();
			
			// instanciamos el metodo de identificacion para comprobar
			// el usuario y el login
			if (userSearched.identifyUser(userLogin, userPass)) {
				// identificado el usuario, lo remitimos a la pagina principal
				UsersBean dataUser=new UsersBean();
					// creamos una sesion especifica solo para obtener la info
                                try {
					// instanciamos un objeto identify y el Users para conseguir la informacion
					// la variable estatica de usuario se ha creado en la identificacion                                    
                                    Session mySession1=CreateHbnSession.getSessionFactory().openSession();
                                    mySession1.beginTransaction();
                                    InversionJSF.thisUser=dataUser.getUser(InversionJSF.idUsuario, mySession1);
                                    InversionJSF.infoUser=InversionJSF.thisUser.getUserInfo();
                                    mySession1.close();
                            
                                } catch (HibernateException ex) {
                                    // error de conexion
                                    // hacia la pagina de no identificado
                                    colorBackground1="styleInputBackground";
                                    colorBackground2="styleInputBackground";
                                    destinyPage=goToWrongIdentify;
                                    return destinyPage;
                                }
	
				// construimos la presentacion del usuario que ira en la cabecera de las
				// pantallas de la aplicacion
				//introducing="";
				String name=InversionJSF.infoUser.getName();
				if (name.length()<15) {
					introducing=name+", hoy es "+today;
				} else if (name.indexOf(" ")!=-1 && name.indexOf(" ")<15) {
					introducing=name.substring(0,name.indexOf(" "))+", hoy es "+today;
				} else {
					introducing=name.substring(0,15)+", hoy es "+today;
				}
				
					// por ultimo, transferimos el control
				destinyPage=goToMainPage;
				
			} else {
				// si no se produce la identificacion redirigimos
				// hacia la pagina de no identificado
				user="BAD LOGIN";
				passw="BAD PASSWORD";
                colorBackground1="styleInputBackground";
                colorBackground2="styleInputBackground";
				destinyPage=goToWrongIdentify;
			}

		}			
		
		return destinyPage;
		
	} // end method processIdentify
	
	
	
	// *********** REDIRECCIONES DE PAGINAS
	
	public String createNewUser () {
		
		// simplemente redirecciona hacia la pagina de nuevo usuario
		return goToNewUser;
		
	} // end method createNewUser
	
	public String goToPersonal () {
		
		// simplemente redirecciona hacia la pagina de personal
		return goToPersonal;
		
	} // end method goToPersonal

	public String goToCuentas () {
		
		// simplemente redirecciona hacia la pagina de cuentas
		return goToCuentas;
		
	} // end method goToCuentas
	
	public String goToOperaciones () {
		
		// simplemente redirecciona hacia la pagina de operaciones
		return goToOperaciones;
		
	} // end method goToOperaciones
	
	public String goToHistorico () {
		
		// simplemente redirecciona hacia la pagina de historico
		return goToHistorico;
		
	} // end method goToHistorico
        
        public String getOut () {
		
            // cierra la sessionFactory desconectandose
            CreateHbnSession.close();
		// sale de la aplicación
		return "index.xhtml";
		
	} // end method getOut
	
	
	
	// ************ GETTERS AND SETTERS
	
	public String getUser() {
		return user;
	}
	public String getPassw() {
		return passw;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public void setPassw(String passw) {
		this.passw = passw;
	}
	public String getIntroducing() {
		return introducing;
	}
	public void setIntroducing(String introducing) {
		this.introducing = introducing;
	}
        
        public String getColorBackground1() {
            System.err.println("------"+colorBackground1);
            return colorBackground1;
        }

        public void setColorBackground1(String colorBackground1) {
            this.colorBackground1 = colorBackground1;
        }

        public String getColorBackground2() {
            return colorBackground2;
        }

        public void setColorBackground2(String colorBackground2) {
            this.colorBackground2 = colorBackground2;
        }




	
	
	
} //**************** END OF CLASS
