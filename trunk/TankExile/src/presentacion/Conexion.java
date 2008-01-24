package presentacion;

import paquete.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

// Clase cuya función es establecer la comunicación entre los dos hosts.
public class Conexion extends Thread implements Legible{
	private Tanque tanquePropio; // Tanque correspondiente al host propio (o no-remoto).
	private Controlable tanqueRemotoAControlar; // Interfaz con la que se controla el tanque propio remoto (en el host oponente).
	private Legible archivosRemotos; // Interfaz con la que se realiza la lectura de los archivos remotos.
	private String iPOponente; // Ip del host oponente.
	private final int PUERTO_ARCHIVOS = 4051; // Puerto al que se asocia el registro de archivos de circuitos.
	private final int PUERTO_TANQUES = 4050; // Puerto al que se asocia el registro de archivos de circuitos.
	
	// Constructor.
	public Conexion(String iPOponente){
		this.iPOponente = iPOponente;
	}
	
	// Método que pone los circuitos de este host a disposición del host oponente.
	public void bindearMisArchivos(){
		try{
			LocateRegistry.createRegistry(PUERTO_ARCHIVOS); // Es tomado el puerto PUERTO_ARCHIVOS y creado un registro asociado sobre él.
			Legible stub = (Legible) UnicastRemoteObject.exportObject(this, 0); // Es exportado el objeto instancia de Conexion.
			Registry registry = LocateRegistry.getRegistry(PUERTO_ARCHIVOS); // Es tomado el registro recientemente ligado al puerto PUERTO_ARCHIVOS.
			registry.bind("Clave archivos", stub); // El ligado el stub al registro.
			System.out.println("Servidor de archivos de circuito listo.");
			try{Thread.sleep(3000);}catch(Exception InterruptedException){}
		}catch(Exception e){
			System.err.println("Excepción de servidor de archivos de circuito: " + e.toString());
			e.printStackTrace();
		}
	}

	// Método que pone los circuitos remotos a disposición de este host. Luego de este método, es posible realizar la copia de los archivos remotos al host local.
	public void ponerADisposicionArchivosRemotos(){	
		try {
			Registry registry = LocateRegistry.getRegistry(iPOponente,PUERTO_ARCHIVOS);
			archivosRemotos = (Legible) registry.lookup("Clave archivos");    
			System.out.println("Conexión de cliente exitosa. Archivos remotos a disposición local.");
		}catch(Exception e){
			System.err.println("Excepción de cliente: " + e.toString());
			e.printStackTrace();
		}
	}
	
	// Método que permite la copia de archivos. No se espera su uso por parte dle programador.
	// Ver copiarDeHostRemoto().
	public String leer(String archivo){
		try{
			System.out.println("Archivo siendo leido remotamente: "+archivo);
			File textFileALeer = new File(archivo);
			FileReader textIn = new FileReader(textFileALeer);
			char bufferCadena[] = new char[1000];
			int cantidadDeCaracteresLeidos = textIn.read(bufferCadena);
			textIn.close();
			if (cantidadDeCaracteresLeidos != -1){
				return String.valueOf(bufferCadena,0,cantidadDeCaracteresLeidos);
			}else{
				System.out.println("El archivo '"+ archivo +"' está vacío.");
				return null;
			}
		}catch(Exception e){
			System.err.println("Error en el método leer de la clase Conexión.");
			e.printStackTrace();
			return null;
		}
	}
	
	// Método que realiza la copia de un archivo remoto al host actual.
	// Se requiere para su uso haber ejecutado previamente bindearMisArchivos() remotamente.
	// Además se requiere luego de ello haber ejecutado ponerADisposicionArchivosRemotos().
	public void copiarDeHostRemoto(String archivoOrigenRemoto, String archivoDestinoLocal) throws IOException {
		File textFileAEscribir = new File(archivoDestinoLocal);
		FileWriter out = new FileWriter(textFileAEscribir);
		String cadena = archivosRemotos.leer(archivoOrigenRemoto);
		if (cadena != null){
			out.write(cadena);
		}else{
			System.out.println("El archivo que se intentó copiar desde el host remoto estaba vacío.");
		}
		out.close();
	}
	
	// Método que establece la comunicación con el tanque remoto. Utiliza un método privado.
	public void establecerComunicacionTanqueRemoto(){
		this.tanqueRemotoAControlar = llamarTanqueRemoto();
	}
	
	// Método que pone al tanque remoto a disposición del host local, para su control.
	// Es privado, sólo utilizado por el método establecerComunicacionTanqueRemoto().
	private Controlable llamarTanqueRemoto(){
		try {
			System.out.println("Partida llamando a TankRMI en el otro host (IP:"+iPOponente+"): esperando respuesta...");
			Registry registry = LocateRegistry.getRegistry(iPOponente,PUERTO_TANQUES); // *****
			Controlable retorno = (Controlable) registry.lookup("Clave tanques");
			System.out.println("Conexión de cliente exitosa. Tanque a disposición.");
			return retorno;
		} catch (Exception e) {
			System.err.println("Excepción de cliente: " + e.toString());
			e.printStackTrace();
		}
		return null;
	}
	
	// Método utilizado por el hilo de conexión para lograr el control del tanque propio remoto.
	public void manejarTanqueRemoto(){
		try {
			tanqueRemotoAControlar.setX(tanquePropio.getX());
			tanqueRemotoAControlar.setY(tanquePropio.getY());
			tanqueRemotoAControlar.setDireccion(tanquePropio.getDireccion());
		} catch (RemoteException ex) {
			System.out.println("Error en el manejo del tanque remoto, clase Conexion.");
			Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public void setTanquePropio(Tanque tanquePropio){
		this.tanquePropio = tanquePropio;
	}
	
	// Método de manejo del tanque remoto, mediante hilo aparte.
	public void run(){
		while(true){
			try{
				manejarTanqueRemoto();
				Thread.sleep(Finals.PERIODO);
			}catch(InterruptedException ex){
				Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
	// Método que pone a disposición al tanque local oponente, para que sea controlado remotamente.
	public Tanque servirTanqueLocalOponente(Tanque tanqueLocalLigadoOponente){	
		try{
			LocateRegistry.createRegistry(PUERTO_TANQUES);
			Controlable stub = (Controlable) UnicastRemoteObject.exportObject(tanqueLocalLigadoOponente, tanqueLocalLigadoOponente.getID());		
			Registry registry = LocateRegistry.getRegistry(PUERTO_TANQUES);
			registry.bind("Clave tanques", stub);
			System.out.println("Servidor de tanque listo.");
			try{Thread.sleep(3000);}catch(Exception InterruptedException){}
			return tanqueLocalLigadoOponente;
		}catch(Exception e){
			System.err.println("Excepción de servidor: " + e.toString());
			e.printStackTrace();
			return null;
		}
	}
	
}
