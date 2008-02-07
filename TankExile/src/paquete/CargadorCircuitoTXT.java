package paquete;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

// Clase que permite la traducción de un archivo txt a un circuito de juego del programa.
public class CargadorCircuitoTXT {
	private File textFile; // Objeto que permite manipular el archivo txt que contiene al circuito.
	private FileReader textIn; // Objeto que permite manipular específicamente los archivos extesión txt.
	private boolean meta1=false, meta0=false; // Atributos que indican si se ha hallado determinada meta, o no, para luego indicar si hubo error en la carga del circuito.
	public CargadorCircuitoTXT(String nombre){
		try {
			textFile = new File(nombre); // El archivo indicado es abierto.
			textIn = new FileReader(textFile); // Ahora es susceptible de ser leído.
		} catch (FileNotFoundException ex) {
			System.out.println("Error al cargar el circuito (" + textFile.getName()+").");
			Logger.getLogger(CargadorCircuitoTXT.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public static boolean validarCircuito(String nombre){
		File textFile = null;
		FileReader textIn = null;
		try{
			textFile = new File(nombre); // El archivo indicado es abierto.
			textIn = new FileReader(textFile); // Ahora es susceptible de ser leído.
		}catch(Exception e){
			e.printStackTrace();
		}
		
		boolean hayMeta1 = false, hayMeta2 = false;
		boolean correctoLongitud = false;
		try {
			for (int j = 0; j < Finals.BLOQUES_NUM-2; j++) {
				for (int i = 0; i < Finals.BLOQUES_NUM-2; i++) {
					int caracter;
					do {
						caracter = getCaracter(textIn);
						//System.out.println("Caracter leído: '" + (char)caracter + "' (" + caracter +").");
					} while ((((char)caracter) != '0')&&(((char)caracter) != '1')&&(((char)caracter) != '2')&&(((char)caracter) != '3'));
					//System.out.println("Es caracter válido para analizar: (i;j)=("+i+";"+ j+").");
					switch ((char)caracter) {
						case '0': break;
						case '1': break;
						case '2': hayMeta1=true; break;
						case '3': hayMeta2=true; break;
						
					}
					//System.out.println("Variables hM1 y hM2: '" + hayMeta1 + "' y '" +hayMeta2 +"'.");
				}
			}
			//System.out.println("Circuito correcto.");
			correctoLongitud = true;
		} catch (IOException ex) {
			correctoLongitud = false;
		}
		return (correctoLongitud && hayMeta1 && hayMeta2);
	}
	
	
	// Método que secuencialmente brinda los caracteres (como enteros) leídos en el archivo.
	private static int getCaracter(FileReader textIn) throws IOException{
		int caracter;
		caracter = textIn.read(); // Lectura del archivo.
		if (caracter ==-1) { // En caso de finalización del archivo, se produce una excepción.
			// Este método debe ser llamado, al menos, tantas veces como bloques tenga el circuito. 
			// Han de saltarse caracteres de: salto de línea (10) y retorno de carro(13).
			// Si en esas llamadas se llega al fin del archivo, se dice que este archivo de circuito está 'incompleto'.
			throw new IOException("El archivo del circuito finalizó inesperadamente, antes de completar la cantidad de bloques requeridos por el circuito.");
		}
		return caracter;
	}
	
	// Método que cierra el archivo abierto en la construcción de los objetos de esta clase.
	public void cerrarArchivo(){
		try {
			textIn.close(); // El archivo anteriormente abierto ahora es cerrado.
		} catch (IOException ex) {
			Logger.getLogger(CargadorCircuitoTXT.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	// Método que secuencialmente brinda los caracteres (como enteros) leídos en el archivo.
	private int getCaracterLeido() throws IOException{
		int caracter;
		caracter = textIn.read(); // Lectura del archivo.
		if (caracter ==-1) { // En caso de finalización del archivo, se produce una excepción.
			// Este método debe ser llamado, al menos, tantas veces como bloques tenga el circuito. 
			// Han de saltarse caracteres de: salto de línea (10) y retorno de carro(13).
			// Si en esas llamadas se llega al fin del archivo, se dice que este archivo de circuito está 'incompleto'.
			throw new IOException("El archivo del circuito finalizó inesperadamente, antes de completar la cantidad de bloques requeridos por el circuito.");
		}
		return caracter;
	}
	
	// Método que retorna algún tipo de bloque, que corresponda a la lectura del caracter en curso sobre el archivo txt del circuito.
	public Bloque getBloqueLeido(int x, int y)throws IOException{
		int caracter;	// Entero que es resultado de la lectura caracter a caracter del archivo.
		do{
			caracter = getCaracterLeido(); // Lectura del caracter sobre el cursor del archivo, hasta que no sea un salto de línea (10) ni un retorno de carro (13).
		}while((caracter==13) || (caracter == 10)); 
		
		switch ((char)caracter){	// A cada caracter leído corresponde el retorno de un objeto.
									//		'0' -> Sendero (sin bloque, es decir, sin muro ni meta).
									//		'1' -> Muro.
									//		'2' -> Meta 1.
									//		'3' -> Meta 2.
									//		Cualquier otro caracter implica arrojar una excepción por caracter no válido.
			case '0' : return null;
			case '1' : return new Muro(x,y);
			case '2' : meta0=true; return new Meta(x,y,0);
			case '3' : meta1=true; return new Meta(x,y,1);
			default: throw new IOException("El archivo tiene un caracter no válido ("+ caracter +").");		
		}
	}
	
	// Método que indica la existencia o no de ambas metas, requeridas en el circuito.
	public boolean circuitoCorrecto(){
		return (meta0&&meta1);
	}
}
