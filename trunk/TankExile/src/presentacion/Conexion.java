package presentacion;

import paquete.*;
import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import javax.swing.JOptionPane;

// Clase cuya función es establecer la comunicación entre los dos hosts.

public class Conexion implements Conectable{
	private static Conexion instanciaConexion = null;
	private Conectable conexionRemoto;
	
	private String iPOponente; // Ip del host oponente.
	public static final int PUERTO = 4500; // Puerto al que se asocian todos los registros.
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
	
	private CircuitoControlable circuitoRemotoAControlar;
	private Circuito circuitoPropio;
	
	private PrePartida ventana;
	private Tanque tanqueLocalLigadoOponente;
	
	
	private HashMap<String,Imitable> imitadores = new HashMap<String,Imitable>();
	private HashMap<String,Imitable> imitables = new HashMap<String,Imitable>();
	private HashMap<String,Imitable> imitadoresRemotos = new HashMap<String,Imitable>();
	private ArrayList<String> claves = new ArrayList<String>();
	
	
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
		Bindeador.getBindeador().bindear(this, "Clave conexion");
	}
	
	// Equivalente a 'poner a disposición instancia de la clase conexión remota'.
	public void conectar(String iPOponente) throws Exception{
		this.iPOponente = iPOponente;
		conexionRemoto = (Conectable)Bindeador.getBindeador().ponerADisposicion("Clave conexion");
		System.out.println("Conexión exitosa.");
	}
	
	public boolean conexionLista(){
		return Bindeador.getBindeador().getListo();
	}
	
	public void setVentanaRemota(PrePartida ventana){
		this.ventana = ventana;
	}
	// Método que pone la ventana de selección de circuitos de este host a disposición del host oponente.
	public void bindearMiVentana(){
		Bindeador.getBindeador().bindear(ventana, "Clave ventana");
	}
	
	// Método que pone la ventana de selección de circuitos remota a disposición de este host.
	public VentanaControlable ponerADisposicionVentanaRemota(){	
		do{
			try{
				return (VentanaControlable)Bindeador.getBindeador().ponerADisposicion("Clave ventana");
			}catch(Exception ex){
				System.out.println("Fallo en el intento de conexión con la ventana remota. Intentando conexión nuevamente...");
				try{Thread.sleep(Finals.ESPERA_CONEXION);}catch(InterruptedException r){}
			}
		}while(!Bindeador.getBindeador().getListo());
		return null;
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
	public void bindearTanqueLocalOponente(Tanque tanqueLocalLigadoOponente){
		this.tanqueLocalLigadoOponente = tanqueLocalLigadoOponente;
		Bindeador.getBindeador().bindear(tanqueLocalLigadoOponente, "Clave tanques");
	}
	
	// Método que establece la comunicación con el tanque remoto. Utiliza un método privado.
	// Pone al tanque remoto a disposición del host local, para su control.
	
	public void ponerADisposicionTanqueRemoto(){
		do{
			try{
				this.tanqueRemotoAControlar = (Controlable)Bindeador.getBindeador().ponerADisposicion("Clave tanques");
			}catch(Exception e){
				System.out.println("Intento fallido para obtener tanque remoto. Intentando de nuevo...");
				try {Thread.sleep(Finals.ESPERA_CONEXION);} catch (InterruptedException ex) {ex.printStackTrace();}
			}
		}while(!Bindeador.getBindeador().getListo());	
	}
	
	public void indicarChoque(){
		Runnable hilito = new Runnable(){
			public void run() {				
				try{
					tanqueRemotoAControlar.choqueResumido();
				}catch(Exception ex){
					System.out.println("Error al indicar un choque remotamente.");
					ex.printStackTrace();
				}
			}
		};
		(new Thread(hilito, "Hilo indicador de choque remoto")).start();
	}
	// Método utilizado por el hilo de conexión para lograr el control del tanque propio remoto.
	public void manejarTanqueRemoto(){
		try {
			tanqueRemotoAControlar.imitar((Imitable)tanquePropio);
		} catch (RemoteException ex) {
			System.out.println("Error en el manejo del tanque remoto, clase Conexion. El oponente ha finalizado la sesión.");
			ex.printStackTrace();
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
					manejarTanqueRemoto();
					try{Thread.sleep(Finals.PERIODO_SINCRONIZACION_TANQUES);}catch(InterruptedException ex){ex.printStackTrace();}
				}
			}
		};
	}
	
	public String getNickTanqueOponente(){
		String nick = null;
		try {
			nick = this.circuitoRemotoAControlar.getNickOponente();
		} catch (RemoteException ex) {
			System.out.println("Error al intentar obtener el nick del oponente. Clase conexión.");
			ex.printStackTrace();
		}
		return nick;
	}
	
	// Método que pone a disposición las bolas locales, para que sean controladas remotamente.
	public void bindearBolasLocales(){	
		Bindeador.getBindeador().bindear(bolaBuenaLocal, "Clave bolaBuena");
		Bindeador.getBindeador().bindear(bolaMalaLocal, "Clave bolaMala");
	}
	
	// Método que pone a las bolas remotas a disposición del host local, para su control.
	// Es privado, sólo utilizado por el método establecerComunicacionBolasRemotas().
	public void ponerADisposicionBolasRemotas(){
		do{
			try{
				this.bolaBuenaAControlar = (BolaControlable)Bindeador.getBindeador().ponerADisposicion("Clave bolaBuena");
				this.bolaMalaAControlar = (BolaControlable)Bindeador.getBindeador().ponerADisposicion("Clave bolaMala"); // Se llama a las remotas en caso de hacer control de bolas localmente, para controlarlas.
			}catch(Exception e){
				try { Thread.sleep(Finals.ESPERA_CONEXION); } catch (InterruptedException ex) {ex.printStackTrace();}
				System.out.println("Intento fallido por obtener bolas remotas... Reintentando...");
			}
		}while(!Bindeador.getBindeador().getListo());
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
			ex.printStackTrace();
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
						ex.printStackTrace();
					}
				}
			}
		};
	}	
	
	// Método que pone a disposición al circuito local, para que sea controlado remotamente.
	public void bindearCircuitoLocal(){	
		Bindeador.getBindeador().bindear(circuitoPropio, "Clave circuito");
	}
	
	// Método que establece la comunicación con el circuito remoto.
	// Pone al circuito remoto a disposición del host local, para su control.
	public void ponerADisposicionCircuitoRemoto(){	
		do{
			try{
				this.circuitoRemotoAControlar = (CircuitoControlable)Bindeador.getBindeador().ponerADisposicion("Clave circuito");
			}catch(Exception e){
				try { Thread.sleep(Finals.ESPERA_CONEXION); } catch (InterruptedException ex) {ex.printStackTrace();}
				System.out.println("Intento fallido por obtener circuito remoto... Reintentando...");
			}
		}while(!Bindeador.getBindeador().getListo());
	}
	
	// Método utilizado por el hilo de conexión para lograr el control del tanque propio remoto.
	public void manejarCircuitoRemoto(){
		
		try{
			
			circuitoRemotoAControlar.imitar(this.circuitoPropio);
			
		}catch(Exception e){
			System.out.println("Error al intentar que el circuito remoto imite.");
			e.printStackTrace();

		}
		
		/*try {
			if (!this.choquesPendientesCircuitoRemoto.isEmpty()){
				for(int i = 0; i<this.choquesPendientesCircuitoRemoto.size();i++){
					this.circuitoRemotoAControlar.informarChoque(this.choquesPendientesCircuitoRemoto.get(i));
				}
				this.choquesPendientesCircuitoRemoto.clear();
			}
		} catch (RemoteException ex) {
			System.out.println("Error en el manejo del circuito remoto, clase Conexion. El oponente ha finalizado la sesión.");
			ex.printStackTrace();
			this.stopHilos();
			JOptionPane.showMessageDialog(null, "El oponente abandono conexión.");
			System.exit(0);
		}*/
	}
	
	public void setCircuitoPropio(Circuito circuitoPropio){
		this.circuitoPropio = circuitoPropio;
	}
	
	
	public Runnable getHiloManejadorDeCircuitoRemoto(){
		correrHilos = true;
		return new Runnable(){
			public void run() {
				while(correrHilos){
					manejarCircuitoRemoto();
					try{Thread.sleep(Finals.PERIODO);}catch(InterruptedException ex){ex.printStackTrace();}
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
		try {
			circuitoRemotoAControlar.oponenteLlego();
		} catch (RemoteException ex) {
			System.out.println("Error en el método finDePartida en la clase Conexion.");
			ex.printStackTrace();
		}
	}
	
	public int getID(){
		return this.miID;
	}
	public int getOtroID(){
		return ((this.miID+1) % 2);
	}	
	public String getIP(){
		return this.iPOponente;
	}
	
	public void ponerADisposicionImitadoresRemotos(){
		Iterator iterador = this.claves.iterator();
		Imitable imitable=null;
		while(iterador.hasNext()){
			String clave = (String) iterador.next();
			do{
				try{
					imitable = (Imitable)Bindeador.getBindeador().ponerADisposicion(clave);
				}catch(Exception e){
					e.printStackTrace();
				}
			}while(!Bindeador.getBindeador().getListo());
			this.imitadoresRemotos.put(clave, imitable);
		}
	}
	
	public void bindearImitadores(){
		Iterator iterador = this.claves.iterator();
		while (iterador.hasNext()){
			String clave = (String) iterador.next();
			Bindeador.getBindeador().bindear((Imitable)this.imitadores.get(clave), clave);
		}
	}
	
	public void actualizar(){
		Hashtable a = new Hashtable();
		
		/*
		Iterator iterador = imitadorRemoto.iterator();
		while(iterador.hasMore()){
			iterador.next()
		}
		recorrer imitadorRemoto
		tomar Clave del objeto recorrido
		 * buscar clave en imitables
		 * tomar ese imitable
		imitableREmoto.imitar(imitable)
		*/
	}
	public void ponerImitador(String clave, Imitable imitador){
		this.imitadores.put(clave, imitador);
		if (!this.claves.contains(clave)) this.claves.add(clave);
	}
	
	public void ponerImitado(String clave, Imitable imitado){
		this.imitables.put(clave, imitado);
		if (!this.claves.contains(clave)) this.claves.add(clave);
	}
	
	
	
}