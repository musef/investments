package control;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;

/**
 * 
 *
 * @author musef
 * 
  * @version 2.3JSF 2013-01-31
 *
 */

@ManagedBean (name="listadoBean")
@SessionScoped
public class ListadoBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String delPushed;
	private String modPushed;
	
	public String goToHistorico () {
		return "historico.xhtml";
	}
	
	public String consultar () {
		System.err.println("**"+delPushed+"**");
		
		return "historico.xhtml";
	}

	
	public void selectOperation(ValueChangeEvent e) {
		// registra los cambios en el comboBox de cuentas
		//String source=e.getNewValue().toString();
		
		System.err.println("**"+delPushed+"**");
		 
		
		
	} // end of method selectAccount
	
	
	
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

}
