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
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

// Clase cuya función es establecer la comunicación entre los dos hosts.



import javax.swing.JOptionPane;
public class Conexion extends Thread implements Conectable{

	private Conectable conexionRemoto;
	private Tanque tanquePropio; // Tanque correspondiente al host propio (o no-remoto).
	private Controlable tanqueRemotoAControlar; // Interfaz con la que se controla el tanque propio remoto (en el host oponente).
	private BolaControlable bolaBuenaAControlar;
	private BolaControlable bolaMalaAControlar;
	private Bola bolaBuenaLocal;
	private Bola bolaMalaLocal;
	private VentanaControlable ventanaRemota; // Interfaz con la que se hace la manipulación de la ventana remota de selección de circuitos.
	private String iPOponente; // Ip del host oponente.
	private final int PUERTO = 4049; // Puerto al que se asocian todos los registros.
	private boolean tanqueListo = false; // Indicador de la disponibilidad o no del tanque remoto para el host local.
	private boolean ventanaLista = false; // Indicador de la disponibilidad o no de la ventana (de selección de circuito) remota para el host local.
	private boolean conexionLista = false; //Indicador de la conexión con el host remoto.
	private boolean bolasListo = false; //Indicador de la disponibilidad de las bolas remotas.
	
	private double clavePropia; // Valor numérico generado localmente para la inicialización del turno.
	private double claveOponente; // Valor numérico enviado desde el oponente para iniciar el turno.
	private boolean claveOponenteRecibida = false; // Indicador de la llegada de la clave del oponente.
	private boolean miTurno = false; // Indicador de turno de este host.
	private int miID = 0;
	private boolean correrHilos = true;
	private boolean circuitoListo = false;
	private CircuitoControlable circuitoRemotoAControlar;
	private Circuito circuitoPropio;
	
	/* Formato de presentación:
			1. Bindeo.
			2. Puesta a disposición.
			3. El getListo(...).
	 		4. Demás.
	 */
	
	// Constructor.
	public Conexion(String iPOponente){
		this.iPOponente = iPOponente;
		
		try{
			LocateRegistry.createRegistry(PUERTO); // Es tomado el puerto PUERTO y creado un registro asociado sobre él.
		}catch(Exception e){
			System.out.println("Registro de la conexión realizado anteriormente.");
		}
		try{
			Conectable stub = (Conectable) UnicastRemoteObject.exportObject(this, PUERTO); // Es exportado el objeto instancia de Conexion.
			Registry registry = LocateRegistry.getRegistry(PUERTO); // Es tomado el registro recientemente ligado al puerto PUERTO.
			registry.rebind("Clave conexion", stub); // El ligado el stub al registro.
			System.out.println("Servidor de conexion listo.");
		}catch(Exception e){
			System.err.println("Excepción de servidor de conexion, probablemente otra instancia del juego se encuentre en curso: " + e.toString());
			e.printStackTrace();
		}
		
	}
	
	public void conectar() throws Exception{
		Registry registry = LocateRegistry.getRegistry(iPOponente, PUERTO);
		this.conexionRemoto = (Conectable) registry.lookup("Clave conexion");
		System.out.println("Conexión exitosa.");
		conexionLista = true;	
	}
	
	public boolean conexionLista(){
		return conexionLista;
	}
	
	// Método que pone la ventana de selección de circuitos de este host a disposición del host oponente.
	public void bindearMiVentana(PrePartida1 ventana){
		
		try{
			LocateRegistry.createRegistry(PUERTO); // Es tomado el puerto PUERTO y creado un registro asociado sobre él.
		}catch(Exception e){
			System.out.println("Registro de la ventana realizado anteriormente.");
		}
		try{
			presentacion.VentanaControlable stub = (VentanaControlable) UnicastRemoteObject.exportObject(ventana, PUERTO); // Es exportado el objeto instancia de Conexion.
			Registry registry = LocateRegistry.getRegistry(PUERTO); // Es tomado el registro recientemente ligado al puerto PUERTO.
			registry.rebind("Clave ventana", stub); // El ligado el stub al registro.
			System.out.println("Servidor de ventana de circuito listo.");
		}catch(Exception e){
			System.err.println("Excepción de servidor de ventana de circuito: " + e.toString());
			e.printStackTrace();
		}
	}

	// Método que pone la ventana de selección de circuitos remota a disposición de este host.
	public void ponerADisposicionVentanaRemota() throws Exception{	
		Registry registry = LocateRegistry.getRegistry(iPOponente,PUERTO);
		this.ventanaRemota = (VentanaControlable) registry.lookup("Clave ventana");    
		//System.out.println("Conexión de cliente exitosa. Ventana remota a disposición local.");
		ventanaLista = true;
	}
	
	public boolean ventanaLista(){
		return ventanaLista;
	}
	
	// Método que permite la copia de archivos. No se espera su uso por parte dle programador.
	// Ver copiarDeHostRemoto().
	public String leer(String archivo){
		try{
			System.out.println("Archivo siendo leido remotamente: " + archivo);
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
		conexionRemoto.copiarDeHostRemoto(archivoOrigenLocal,archivoDestinoRemoto);
	}
	
	// Método que realiza la copia de un archivo remoto al host actual.
	// Se requiere para su uso haber ejecutado previamente bindearMisArchivos() remotamente.
	// Además se requiere luego de ello haber ejecutado ponerADisposicionArchivosRemotos().
	public void copiarDeHostRemoto(String archivoOrigenRemoto, String archivoDestinoLocal) throws IOException {
		File textFileAEscribir = new File(archivoDestinoLocal);
		FileWriter out = new FileWriter(textFileAEscribir);
		String cadena = conexionRemoto.leer(archivoOrigenRemoto);
		if (cadena != null){
			out.write(cadena);
		}else{
			//System.out.println("El archivo que se intentó copiar desde el host remoto estaba vacío.");
		}
		out.close();
	}
	
	// Método que pone a disposición al tanque local oponente, para que sea controlado remotamente.
	public Tanque bindearTanqueLocalOponente(Tanque tanqueLocalLigadoOponente){	
		try{
			LocateRegistry.createRegistry(PUERTO);
		}catch(Exception e){
			System.out.println("Registro de los tanques realizado anteriormente.");
		}
		try{
			
			Controlable stub = (Controlable) UnicastRemoteObject.exportObject(tanqueLocalLigadoOponente, PUERTO);		
			Registry registry = LocateRegistry.getRegistry(PUERTO);
			registry.rebind("Clave tanques", stub);
			System.out.println("Servidor de tanque local listo.");
			return tanqueLocalLigadoOponente;
		}catch(Exception e){
			System.err.println("Excepción de servidor de tanque local: " + e.toString());
			e.printStackTrace();
			return null;
		}
	}
	
	// Método que establece la comunicación con el tanque remoto. Utiliza un método privado.
	// Pone al tanque remoto a disposición del host local, para su control.
	
	public void ponerADisposicionTanqueRemoto() throws Exception{
		//System.out.println("Conexión llamando a TankRMI en el otro host (IP:" + iPOponente + "): esperando respuesta...");
		Registry registry = LocateRegistry.getRegistry(iPOponente, PUERTO);
		Controlable retorno = (Controlable) registry.lookup("Clave tanques");
		System.out.println("Conexión de cliente exitosa. Tanque a disposición.");
		this.tanqueListo = true;
		this.tanqueRemotoAControlar = retorno;
	}
	
	public boolean tanqueListo(){
		return tanqueListo;
	}
	
	// Método utilizado por el hilo de conexión para lograr el control del tanque propio remoto.
	public void manejarTanqueRemoto(){
		try {
			tanqueRemotoAControlar.setTodo(tanquePropio.getX(), tanquePropio.getY(), tanquePropio.getDireccion(), tanquePropio.getMovimientoTrama(), tanquePropio.getChoqueTrama());
		} catch (RemoteException ex) {
			System.out.println("Error en el manejo del tanque remoto, clase Conexion. El oponente ha finalizado la sesión.");
			Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);

			JOptionPane.showMessageDialog(null, "El oponente abandono conexión");
			System.exit(0);

		}
	}
	
	public void setTanquePropio(Tanque tanquePropio){
		this.tanquePropio = tanquePropio;
	}
	
	public Runnable getHiloManejadorDeTanqueRemoto(){
		correrHilos = true;
		return new Runnable(){
			public void run() {
				while(correrHilos){
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
	
	
	
	
	// Método que pone a disposición las bolas locales, para que sean controladas remotamente.
	public void bindearBolasLocales(){	
		try{
			LocateRegistry.createRegistry(PUERTO);
		}catch(Exception e){
			System.out.println("Registro de las bolas realizado anteriormente.");
		}
		try{
			Registry registry;
			BolaControlable stub;
			
			stub = (BolaControlable) UnicastRemoteObject.exportObject(this.bolaBuenaLocal, PUERTO);
			registry = LocateRegistry.getRegistry(PUERTO);
			registry.rebind("Clave bolaBuena", stub);
			
			
			stub = (BolaControlable) UnicastRemoteObject.exportObject(this.bolaMalaLocal, PUERTO);		
			registry = LocateRegistry.getRegistry(PUERTO);
			registry.rebind("Clave bolaMala", stub);
			
			System.out.println("Servidor de bolas locales listo.");
			
		}catch(Exception e){
			System.err.println("Excepción de servidor de bolas locales: " + e.toString());
			e.printStackTrace();
			
		}
	}
	
	// Método que pone a las bolas remotas a disposición del host local, para su control.
	// Es privado, sólo utilizado por el método establecerComunicacionBolasRemotas().
	public void ponerADisposicionBolasRemotas() throws Exception{
		//System.out.println("Conexión llamando a TankRMI en el otro host (IP:" + iPOponente + "): esperando respuesta...");
		Registry registry = LocateRegistry.getRegistry(iPOponente, PUERTO);
		this.bolaBuenaAControlar = (BolaControlable) registry.lookup("Clave bolaBuena");
		this.bolaMalaAControlar = (BolaControlable) registry.lookup("Clave bolaMala");
		System.out.println("Conexión de cliente exitosa. Tanque a disposición.");
		this.bolasListo = true;
		
	}
	
	public boolean bolasListo(){
		return bolasListo;
	}
	
	public void setBolasLocales(Bola bolaBuenaLocal, Bola bolaMalaLocal){
		this.bolaBuenaLocal = bolaBuenaLocal;
		this.bolaMalaLocal = bolaMalaLocal;
 	}
	
	// Método utilizado por el hilo de conexión para lograr el control de las bolas remotas.
	public void manejarBolasRemotas(){
		try {
			bolaBuenaAControlar.setTodo(bolaBuenaLocal.getX(), bolaBuenaLocal.getY());
			bolaMalaAControlar.setTodo(bolaMalaLocal.getX(), bolaMalaLocal.getY());
		} catch (RemoteException ex) {
			System.out.println("Error en el manejo de bolas remotas, clase Conexion. El oponente ha finalizado la sesión.");
			Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
			JOptionPane.showMessageDialog(null, "El oponente abandono conexión");
			System.exit(0);

		}
	}
	
	public void stopHilos(){
		correrHilos = false;
	}
	
	public Runnable getHiloManejadorDeBolas(){
		correrHilos = true;
		return new Runnable(){
			public void run() {
				while(correrHilos){
					try{
						manejarBolasRemotas();
						Thread.sleep(Finals.PERIODO);
					}catch(InterruptedException ex){
						Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
		};
	}	
	
	
	
	
	
	
	
	
	/////////CIRCUITO///////
	// Método que pone a disposición al circuito local, para que sea controlado remotamente.
	public void bindearCircuitoLocal(Circuito circuitoLocal){	
		try{
			LocateRegistry.createRegistry(PUERTO);
		}catch(Exception e){
			System.out.println("Registro de los tanques realizado anteriormente.");
		}
		try{
			
			CircuitoControlable stub = (CircuitoControlable) UnicastRemoteObject.exportObject(circuitoLocal, PUERTO);		
			Registry registry = LocateRegistry.getRegistry(PUERTO);
			registry.rebind("Clave circuito", stub);
			System.out.println("Servidor de circuito local listo.");
			
		}catch(Exception e){
			System.err.println("Excepción de servidor de circuito local: " + e.toString());
			e.printStackTrace();
		}
	}
	
	// Método que establece la comunicación con el circuito remoto.
	// Pone al circuito remoto a disposición del host local, para su control.
	public void ponerADisposicionCircuitoRemoto() throws Exception{
		
		Registry registry = LocateRegistry.getRegistry(iPOponente, PUERTO);
		CircuitoControlable retorno = (CircuitoControlable) registry.lookup("Clave circiuto");
		System.out.println("Conexión de circuito exitosa. Circuito remoto a disposición.");
		this.circuitoListo = true;
		this.circuitoRemotoAControlar = retorno;
	}
	
	public boolean circuitoListo(){
		return circuitoListo;
	}
	
	// Método utilizado por el hilo de conexión para lograr el control del tanque propio remoto.
	public void manejarCircuitoRemoto(){
		try {
			tanqueRemotoAControlar.setTodo(tanquePropio.getX(), tanquePropio.getY(), tanquePropio.getDireccion(), tanquePropio.getMovimientoTrama(), tanquePropio.getChoqueTrama());
		} catch (RemoteException ex) {
			System.out.println("Error en el manejo del tanque remoto, clase Conexion. El oponente ha finalizado la sesión.");
			Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);

			JOptionPane.showMessageDialog(null, "El oponente abandono conexión");
			System.exit(0);

		}
	}
	
	public void setCircuitoPropio(Circuito circuitoPropio){
		this.circuitoPropio = circuitoPropio;
	}
	
	public Runnable getHiloManejadorDeCircuitoRemoto(){
		correrHilos = true;
		return new Runnable(){
			public void run() {
				while(correrHilos){
					try{
						manejarCircuitoRemoto();
						Thread.sleep(Finals.PERIODO);
					}catch(InterruptedException ex){
						Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
		};
	}

	
	/////////CIRCIUTO///////
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void setClaveOponente(double clave) throws RemoteException {
		claveOponente = clave;
		claveOponenteRecibida = true;
	}
	
	public synchronized void darTurno() throws RemoteException {
		miTurno = true;
		this.notifyAll();

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
