package control;

import java.io.Serializable;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Clase con algunas herramientas implementadas
 * 
 * @author musef
 * 
  * @version 2.3JSF 2013-01-31
 *
 */
public class Utils implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Utils() {
        //constructor
    }
    
    /**
	 * Este metodo transforma numeros en formato String a numeros double. Su utilidad
	 * es transformar numero con la coma decimal en numeros en formato punto decimal.
	 * 
	 * @param number - Es un String a transformar en un numero double
	 * @return Retorna un numero en formato double o 0 si hay un error
	 *  o el parametro introducido no es un numero
	 */
	
	public double getNumber(String number) {
	
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
	
	public String numeroSp(double numberDouble) {
		
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
	 * fecha comprobada no corresponde al patrón determinado.
	 */
	
	public String verificaFecha(String fecha) {
		
		
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
		
	} // end of method verificaFecha        
        
        /**
         * Método que transforma un String con fecha en formato dd-mm-aaaa o aaaa-mm-dd
         * en un objeto Date apto para grabar en DDBB. Cualquier otro String recibido lo
         * trata como error y devuelve un null.
         * 
         * @param fecha - String con la fecha, que debería ser en formato dd-mm-aaaa para
         * que su conversión sea posible.
         * 
         * @return Un objeto Date sql con la fecha o null si ha sido imposible su conversión.
         */
        
        public Date convertToDate(String fecha) {
            
            	Date fecha1=null;
		String fec1 = null;
		try {
			// la fecha es transformable directamente
			fecha1=Date.valueOf(fecha);
		} catch (IllegalArgumentException e) {
			int sep1=fecha.indexOf("-");
			int sep2=fecha.indexOf("-", sep1+1);
			if (sep1>-1 && sep2>-1) {
				fec1=fecha.substring(sep2+1)+"-"+fecha.substring(sep1+1, sep2)+"-"+fecha.substring(0, sep1);
				try {
					fecha1=Date.valueOf(fec1);
				} catch (Exception ex){
					// error: fecha mal construida
					return null;
				}
			} else {
				// error: esto no es una fecha ni nada que se le parezca
				return null;
			}
		}
                
                // devuelve la fecha en formato Date sql
                return fecha1;
            
        }
        
        
        
} // *********************************** END OF CLASS
