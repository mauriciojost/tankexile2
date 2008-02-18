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
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

// Clase cuya función es establecer la comunicación entre los dos hosts.
import javax.swing.JOptionPane;
public class Conexion implements Conectable{
	private static Conexion instanciaConexion = null;
	private Conectable conexionRemoto;
	private VentanaControlable ventanaRemota; // Interfaz con la que se hace la manipulación de la ventana remota de selección de circuitos.
	private String iPOponente; // Ip del host oponente.
	private final int PUERTO = 4500; // Puerto al que se asocian todos los registros.
	private boolean tanqueListo = false; // Indicador de la disponibilidad o no del tanque remoto para el host local.
	private boolean ventanaLista = false; // Indicador de la disponibilidad o no de la ventana (de selección de circuito) remota para el host local.
	private boolean conexionLista = false; //Indicador de la conexión con el host remoto.
	private boolean bolasListo = false; //Indicador de la disponibilidad de las bolas remotas.
	private Tanque tanquePropio; // Tanque correspondiente al host propio (o no-remoto).
	private Controlable tanqueRemotoAControlar; // Interfaz con la que se controla el tanque propio remoto (en el host oponente).
	private BolaControlable bolaBuenaAControlar;
	private BolaControlable bolaMalaAControlar;
	private Bola bolaBuenaLocal;
	private Bola bolaMalaLocal;
	
	private double clavePropia; // Valor numérico generado localmente para la inicialización del turno.
	private double claveOponente; // Valor numérico enviado desde el oponente para iniciar el turno.
	private boolean claveOponenteRecibida = false; // Indicador de la llegada de la clave del oponente.
	private int miID = -1;
	private boolean correrHilos = true;
	private boolean circuitoListo = false;
	private CircuitoControlable circuitoRemotoAControlar;
	private Circuito circuitoPropio;
	private ArrayList<int[]> choquesPendientesCircuitoRemoto = new ArrayList<int[]>();
	private PrePartida ventana;
	private Tanque tanqueLocalLigadoOponente;
	/* Formato de presentación:
			1. Bindeo.
			2. Puesta a disposición.
			3. El getListo(...).
	 		4. Demás.
	 */
	
	
	public static Conexion getConexion(){
		if (instanciaConexion==null){
			return new Conexion();
		}else{
			return instanciaConexion;
		}
	}

	// Constructor.
	private Conexion(){
		
		instanciaConexion = this;
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
	
	// Equivalente a 'poner a disposición instancia de la clase conexión remota'.
	public void conectar(String iPOponente) throws Exception{
		this.iPOponente = iPOponente;
		Registry registry = LocateRegistry.getRegistry(iPOponente, PUERTO);
		this.conexionRemoto = (Conectable) registry.lookup("Clave conexion");
		System.out.println("Conexión exitosa.");
		conexionLista = true;	
	}
	
	public boolean conexionLista(){
		return conexionLista;
	}
	
	public void setVentanaRemota(PrePartida ventana){
		this.ventana = ventana;
	}
	// Método que pone la ventana de selección de circuitos de este host a disposición del host oponente.
	public void bindearMiVentana(){
		
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
	public VentanaControlable ponerADisposicionVentanaRemota() throws Exception{	
		ventanaLista = false;
		Registry registry = LocateRegistry.getRegistry(iPOponente,PUERTO);
		this.ventanaRemota = (VentanaControlable) registry.lookup("Clave ventana");    
		//System.out.println("Conexión de cliente exitosa. Ventana remota a disposición local.");
		ventanaLista = true;
		return ventanaRemota;
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
	
	
	public void setTanqueLocalOponente(Tanque tanqueLocalLigadoOponente){
		this.tanqueLocalLigadoOponente = tanqueLocalLigadoOponente;
	}
	
	// Método que pone a disposición al tanque local oponente, para que sea controlado remotamente.
	public Tanque bindearTanqueLocalOponente(){	
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
		this.tanqueListo = false;
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
	
	public void indicarChoque(){
		Runnable hilito = new Runnable(){
			public void run() {				
				try{
					tanqueRemotoAControlar.choqueResumido();
				}catch(Exception ex){
					System.out.println("Error al indicar un choque remotamente.");
					Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		};
		(new Thread(hilito, "Hilo indicador de choque remoto")).start();
	}
	// Método utilizado por el hilo de conexión para lograr el control del tanque propio remoto.
	public void manejarTanqueRemoto(){
		try {
			tanqueRemotoAControlar.setTodo(tanquePropio.getX()+20, tanquePropio.getY()+20, tanquePropio.getDireccion(), tanquePropio.getMovimientoTrama(), tanquePropio.getChoqueTrama(), tanquePropio.getMoviendose());
		} catch (RemoteException ex) {
			System.out.println("Error en el manejo del tanque remoto, clase Conexion. El oponente ha finalizado la sesión.");
			Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
			this.stopHilos();
			JOptionPane.showMessageDialog(null, "El oponente abandono conexión.");
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
	
	public String getNickTanqueOponente(){
		String nick = null;
		try {
			nick = this.tanqueRemotoAControlar.getNickOponente();
		} catch (RemoteException ex) {
			System.out.println("Error al intentar obtener el nick del oponente. Clase conexión.");
		}
		return nick;
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
		this.bolasListo = false;
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
			this.stopHilos();
			JOptionPane.showMessageDialog(null, "El oponente abandono conexión.");
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
	
	// Método que pone a disposición al circuito local, para que sea controlado remotamente.
	public void bindearCircuitoLocal(){	
		try{
			LocateRegistry.createRegistry(PUERTO);
		}catch(Exception e){
			System.out.println("Registro de los circuitos realizado anteriormente.");
		}
		try{
			
			CircuitoControlable stub = (CircuitoControlable) UnicastRemoteObject.exportObject(circuitoPropio, PUERTO);		
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
		this.circuitoListo = false;
		Registry registry = LocateRegistry.getRegistry(iPOponente, PUERTO);
		CircuitoControlable retorno = (CircuitoControlable) registry.lookup("Clave circuito");
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
			if (!this.choquesPendientesCircuitoRemoto.isEmpty()){
				for(int i = 0; i<this.choquesPendientesCircuitoRemoto.size();i++){
					this.circuitoRemotoAControlar.informarChoque(this.choquesPendientesCircuitoRemoto.get(i));
				}
				this.choquesPendientesCircuitoRemoto.clear();
			}
		} catch (RemoteException ex) {
			System.out.println("Error en el manejo del circuito remoto, clase Conexion. El oponente ha finalizado la sesión.");
			Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
			this.stopHilos();
			JOptionPane.showMessageDialog(null, "El oponente abandono conexión.");
			System.exit(0);
		}
	}
	
	public void setCircuitoPropio(Circuito circuitoPropio){
		this.circuitoPropio = circuitoPropio;
	}
	
	public void choqueNuevoCircuitoLocal(int indice, int magnitudDelChoque){
		int choque[] = {indice,magnitudDelChoque};
		this.choquesPendientesCircuitoRemoto.add(choque);
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
	
	public void setClaveOponente(double clave) throws RemoteException {
		claveOponente = clave;
		claveOponenteRecibida = true;
	}
	
	public void establecerIDs(){
		clavePropia = (new Random()).nextDouble();
		try{conexionRemoto.setClaveOponente(clavePropia);}catch(RemoteException e){e.printStackTrace();}
		while (!claveOponenteRecibida){
			try{Thread.sleep(Finals.ESPERA_CONEXION);}catch(InterruptedException e){e.printStackTrace();}
		}
		if (clavePropia >= claveOponente){
			miID = 1;
		}else{
			miID = 0;
		}
	}
	
	public void partidaPerdida(){
		//Runnable hilitoMensajeOponente = new Runnable(){
		//	public void run(){
		try {
			circuitoRemotoAControlar.oponenteLlego();
		} catch (RemoteException ex) {
			System.out.println("Error en el método finDePartida en la clase Conexion.");
			Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
		}
		//	}
		//};
		//(new Thread(hilitoMensajeOponente, "Hilo del mensaje de finalización para el oponente")).start();
	}
	
	public void desbindearTodo(boolean inclusoConexion){
		try {
			Registry registro = LocateRegistry.getRegistry(PUERTO);
			String[] lista = registro.list();
			String clave;
			for(int i=0;i<lista.length;i++){
				clave = lista[i];
				if((!clave.equals("Clave conexion"))||inclusoConexion){
					registro.unbind(clave);
				}
			}
		} catch (Exception ex) {
			System.out.println("Excepción en método desbindearTodo de la clase Conexion: ");
			ex.printStackTrace();
		}
	}
	public int getID(){
		return this.miID;
	}
	public int getOtroID(){
		return ((this.miID+1) % 2);
	}
	
}

