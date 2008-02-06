package presentacion;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import paquete.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

// Clase cuya función es establecer la comunicación entre los dos hosts.
public class Conexion extends Thread implements Legible, Conectable{
	private Conectable conexionRemoto;
	private Tanque tanquePropio; // Tanque correspondiente al host propio (o no-remoto).
	private Controlable tanqueRemotoAControlar; // Interfaz con la que se controla el tanque propio remoto (en el host oponente).
	private Legible archivosRemotos; // Interfaz con la que se realiza la lectura de los archivos remotos.
	private VentanaControlable ventanaRemota; // Interfaz con la que se hace la manipulación de la ventana remota de selección de circuitos.
	private String iPOponente; // Ip del host oponente.
	private final int PUERTO_VENTANA = 4052; // Puerto al que se asocia el registro de la ventana de selección de circuitos.
	private final int PUERTO_ARCHIVOS = 4051; // Puerto al que se asocia el registro de archivos de circuitos.
	private final int PUERTO_TANQUES = 4050; // Puerto al que se asocia el registro de tanques.
	private final int PUERTO_CONEXION = 4049; // Puerto al que se asocia el registro de la instancia de Conexion.
	private boolean archivosListo = false; // Indicador de la disponibilidad o no de los archivos remotos para el host local.
	private boolean tanqueListo = false; // Indicador de la disponibilidad o no del tanque remoto para el host local.
	private boolean ventanaLista = false; // Indicador de la disponibilidad o no de la ventana (de selección de circuito) remota para el host local.
	private boolean conexionLista = false; //Indicador de la conexión con el host remoto.
	private double clavePropia; // Valor numérico generado localmente para la inicialización del turno.
	private double claveOponente; // Valor numérico enviado desde el oponente para iniciar el turno.
	private boolean claveOponenteRecibida = false; // Indicador de la llegada de la clave del oponente.
	private boolean miTurno = false; // Indicador de turno de este host.
	private int miID = 0;
	
	// Constructor.
	public Conexion(String iPOponente){
		this.iPOponente = iPOponente;
		
		try{
			LocateRegistry.createRegistry(PUERTO_CONEXION); // Es tomado el puerto PUERTO_CONEXION y creado un registro asociado sobre él.
		}catch(Exception e){
			System.out.println("Registro de la conexión realizado anteriormente.");
		}
		try{
			Conectable stub = (Conectable) UnicastRemoteObject.exportObject(this, PUERTO_CONEXION); // Es exportado el objeto instancia de Conexion.
			Registry registry = LocateRegistry.getRegistry(PUERTO_CONEXION); // Es tomado el registro recientemente ligado al puerto PUERTO_CONEXION.
			registry.rebind("Clave conexion", stub); // El ligado el stub al registro.
			System.out.println("Servidor de conexion listo.");
		}catch(Exception e){
			System.err.println("Excepción de servidor de conexion: " + e.toString());
			e.printStackTrace();
		}
		
	}
	
	public void conectar() throws Exception{
		
			Registry registry = LocateRegistry.getRegistry(iPOponente, PUERTO_CONEXION);
			this.conexionRemoto = (Conectable) registry.lookup("Clave conexion");
			System.out.println("Conexión exitosa.");
			conexionLista = true;
		
		
	}
	
	public boolean conexionLista(){
		return conexionLista;
	}
	// Método que pone la ventana de selección de circuitos de este host a disposición del host oponente.
	public void bindearMiVentana(Escenografia ventana){
		
		try{
			LocateRegistry.createRegistry(PUERTO_VENTANA); // Es tomado el puerto PUERTO_VENTANA y creado un registro asociado sobre él.
		}catch(Exception e){
			System.out.println("Registro de la ventana realizado anteriormente.");
		}
		try{
			presentacion.VentanaControlable stub = (VentanaControlable) UnicastRemoteObject.exportObject(ventana, PUERTO_VENTANA); // Es exportado el objeto instancia de Conexion.
			Registry registry = LocateRegistry.getRegistry(PUERTO_VENTANA); // Es tomado el registro recientemente ligado al puerto PUERTO_VENTANA.
			registry.rebind("Clave ventana", stub); // El ligado el stub al registro.
			System.out.println("Servidor de ventana de circuito listo.");
		}catch(Exception e){
			System.err.println("Excepción de servidor de ventana de circuito: " + e.toString());
			e.printStackTrace();
		}
	}

	// Método que pone la ventana de selección de circuitos remota a disposición de este host.
	public void ponerADisposicionVentanaRemota() throws Exception{	
		Registry registry = LocateRegistry.getRegistry(iPOponente,PUERTO_VENTANA);
		this.ventanaRemota = (VentanaControlable) registry.lookup("Clave ventana");    
		//System.out.println("Conexión de cliente exitosa. Ventana remota a disposición local.");
		ventanaLista = true;
	}
	
	public boolean ventanaLista(){
		return ventanaLista;
	}
	
	// Método que pone los circuitos de este host a disposición del host oponente.
	public void bindearMisArchivos(){
		try{
			LocateRegistry.createRegistry(PUERTO_ARCHIVOS); // Es tomado el puerto PUERTO_ARCHIVOS y creado un registro asociado sobre él.
		}catch(Exception e){
			System.out.println("Registro de los archivos realizado anteriormente.");
		}		
		try{	
			Legible stub = (Legible) UnicastRemoteObject.exportObject(this, 0); // Es exportado el objeto instancia de Conexion.
			Registry registry = LocateRegistry.getRegistry(PUERTO_ARCHIVOS); // Es tomado el registro recientemente ligado al puerto PUERTO_ARCHIVOS.
			registry.rebind("Clave archivos", stub); // El ligado el stub al registro.
			System.out.println("Servidor de archivos de circuito listo.");
		}catch(Exception e){
			System.err.println("Excepción de servidor de archivos de circuito: " + e.toString());
			e.printStackTrace();
			
		}
	}

	// Método que pone los circuitos remotos a disposición de este host. Luego de este método, es posible realizar la copia de los archivos remotos al host local.
	public void ponerADisposicionArchivosRemotos() throws Exception{	
		Registry registry = LocateRegistry.getRegistry(iPOponente,PUERTO_ARCHIVOS);
		archivosRemotos = (Legible) registry.lookup("Clave archivos");    
		System.out.println("Conexión de cliente exitosa. Archivos remotos a disposición local.");
		archivosListo = true;
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
	
	// Método que realiza el envío de un archivo local hacia el host remoto.
	// Se requiere para su uso haber ejecutado previamente bindearMisArchivos() remotamente.
	// Además se requiere luego de ello haber ejecutado ponerADisposicionArchivosRemotos().
	public void enviarAHostRemoto(String archivoOrigenLocal, String archivoDestinoRemoto) throws IOException {
		archivosRemotos.copiarDeHostRemoto(archivoOrigenLocal,archivoDestinoRemoto);
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
			//System.out.println("El archivo que se intentó copiar desde el host remoto estaba vacío.");
		}
		out.close();
	}
	
	public boolean archivosListo(){
		return archivosListo;
	}
	
	// Método que establece la comunicación con el tanque remoto. Utiliza un método privado.
	public void establecerComunicacionTanqueRemoto() throws Exception{
		this.tanqueRemotoAControlar = llamarTanqueRemoto();
	}
	
	// Método que pone al tanque remoto a disposición del host local, para su control.
	// Es privado, sólo utilizado por el método establecerComunicacionTanqueRemoto().
	private Controlable llamarTanqueRemoto() throws Exception{
		//System.out.println("Conexión llamando a TankRMI en el otro host (IP:" + iPOponente + "): esperando respuesta...");
		Registry registry = LocateRegistry.getRegistry(iPOponente, PUERTO_TANQUES); // *****
		Controlable retorno = (Controlable) registry.lookup("Clave tanques");
		System.out.println("Conexión de cliente exitosa. Tanque a disposición.");
		this.tanqueListo = true;
		return retorno;
	}
	
	// Método utilizado por el hilo de conexión para lograr el control del tanque propio remoto.
	public void manejarTanqueRemoto(){
		try {
			
			tanqueRemotoAControlar.setTodo(tanquePropio.getX(), tanquePropio.getY(), tanquePropio.getDireccion(), tanquePropio.getMovimientoTrama(), tanquePropio.getChoqueTrama());
		} catch (RemoteException ex) {
			System.out.println("Error en el manejo del tanque remoto, clase Conexion. El oponente ha finalizado la sesión.");
			Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public void setTanquePropio(Tanque tanquePropio){
		this.tanquePropio = tanquePropio;
	}
	
	public Runnable getHiloTanqueRemoto(){
		return new Runnable(){
			public void run() {
				while(true){
					try{
						manejarTanqueRemoto();
						Thread.sleep(Finals.PERIODO);
					}catch(InterruptedException ex){
						Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
		};
	
	}
	
	// Método que pone a disposición al tanque local oponente, para que sea controlado remotamente.
	public Tanque servirTanqueLocalOponente(Tanque tanqueLocalLigadoOponente){	
		try{
			LocateRegistry.createRegistry(PUERTO_TANQUES);
		}catch(Exception e){
			System.out.println("Registro de los tanques realizado anteriormente.");
		}
		try{
			
			Controlable stub = (Controlable) UnicastRemoteObject.exportObject(tanqueLocalLigadoOponente, tanqueLocalLigadoOponente.getID());		
			Registry registry = LocateRegistry.getRegistry(PUERTO_TANQUES);
			registry.rebind("Clave tanques", stub);
			System.out.println("Servidor de tanque local listo.");
			return tanqueLocalLigadoOponente;
		}catch(Exception e){
			System.err.println("Excepción de servidor de tanque local: " + e.toString());
			e.printStackTrace();
			return null;
		}
	}
	public boolean tanqueListo(){
		return tanqueListo;
	}

	public void setClaveOponente(double clave) throws RemoteException {
		claveOponente = clave;
		claveOponenteRecibida = true;
	}
	
	public synchronized void darTurno() throws RemoteException {
		miTurno = true;
		this.notifyAll();
		System.out.print("1");

		//System.out.println("Cambio de turno...");

	}
	public void run(){
		clavePropia = (new Random()).nextDouble();
		try{conexionRemoto.setClaveOponente(clavePropia);}catch(RemoteException e){e.printStackTrace();}
		while (!claveOponenteRecibida){
			try{this.sleep(Finals.PERIODO_DE_TURNO);}catch(InterruptedException e){e.printStackTrace();}
		}
		if (clavePropia >= claveOponente){
			miTurno = true;
			miID = 1;
		}
		
		while(true){
				if (miTurno){
					try{this.sleep(Finals.PERIODO_DE_TURNO);}catch(InterruptedException e){e.printStackTrace();}		
						miTurno=false;
						try{
							conexionRemoto.darTurno();
							System.out.print("0");
						}catch(RemoteException e){
							e.printStackTrace();
						}
				}else{
					synchronized(this){
						try{this.wait();}catch(InterruptedException e){e.printStackTrace();}
					}
				}
			
		}
	}
	public int getID(){
		return this.miID;
	}
}
