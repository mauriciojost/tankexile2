package presentacion;

import paquete.*;
import java.io.*;
import java.rmi.RemoteException;
import java.util.*;
import javax.swing.JOptionPane;

// Clase cuya función es establecer la comunicación entre los dos hosts.

public class Conexion implements Conectable{
	private static Conexion instanciaConexion = null;
	private Conectable conexionRemoto;
	private String iPOponente; // Ip del host oponente.
	private double clavePropia; // Valor numérico generado localmente para la inicialización del turno.
	private double claveOponente; // Valor numérico enviado desde el oponente para iniciar el turno.
	private boolean claveOponenteRecibida = false; // Indicador de la llegada de la clave del oponente.
	private int miID = -1;
	private VentanaControlable ventana;
	
	private HashMap<String,Imitable> imitadores = new HashMap<String,Imitable>();
	private HashMap<String,Imitable> imitables = new HashMap<String,Imitable>();
	private HashMap<String,Imitable> imitadoresRemotos = new HashMap<String,Imitable>();
	private ArrayList<String> clavesImitadores = new ArrayList<String>();
	private ArrayList<String> clavesImitados = new ArrayList<String>();
	private Random random = new Random();
	
	public static Conexion getConexion(){
		if (instanciaConexion==null)
			instanciaConexion =  new Conexion();
		return instanciaConexion;
	}
	public Conectable getConexionRemoto(){
		return this.conexionRemoto;
	}
	private String nickPropio;

	// Constructor.
	private Conexion(){
		instanciaConexion = this;
		Bindeador.getBindeador().bindear(this, "Clave conexion");
	}
	
	// Equivalente a 'poner a disposición instancia de la clase conexión remota'.
	public void conectar(String iPOponente) throws Exception{
		this.iPOponente = iPOponente;
		conexionRemoto = (Conectable)Bindeador.getBindeador().ponerADisposicion(this.iPOponente, "Clave conexion");
	}
	
	public boolean conexionLista(){
		return Bindeador.getBindeador().getListo();
	}
	
	public void setVentanaRemota(VentanaControlable ventana){
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
				return (VentanaControlable)Bindeador.getBindeador().ponerADisposicion(this.iPOponente, "Clave ventana");
			}catch(Exception ex){
				System.out.println("Fallo en el intento de conexión con la ventana remota. Intentando conexión nuevamente...");
				try{Thread.sleep(Finals.ESPERA_CONEXION);}catch(InterruptedException r){}
			}
		}while(!Bindeador.getBindeador().getListo());
		return null;
	}
	
	public String getNickOponente(){
		String nick = null;
		try {
			nick = this.conexionRemoto.getNickPropio();
		} catch (RemoteException ex) {
			System.out.println("Error al intentar obtener el nick del oponente. Clase conexión.");
			ex.printStackTrace();
		}
		return nick;
	}
	
	public void setNickPropio(String n){
		nickPropio = n;
	}
	public String getNickPropio() throws RemoteException {
		return nickPropio;
	}
	
	public void setClaveOponente(double clave) throws RemoteException {
		claveOponente = clave;
		claveOponenteRecibida = true;
	}
	
	public void establecerIDs(){
		clavePropia = random.nextDouble();
		try{conexionRemoto.setClaveOponente(clavePropia);}catch(RemoteException e){e.printStackTrace();}
		while (!claveOponenteRecibida){
			try{Thread.sleep(Finals.ESPERA_CONEXION);}catch(InterruptedException e){e.printStackTrace();}
		}
		miID = ((clavePropia >= claveOponente)?1:0);
	}
	
	public int getID(){
		return this.miID;
	}
	public int getOtroID(){
		return ((this.miID+1) % 2);
	}	
	
	public void ponerADisposicionImitadoresRemotos(){
		Iterator iterador = this.clavesImitados.iterator();
		Imitable imitable=null;
		while(iterador.hasNext()){
			String clave = (String) iterador.next();
			do{
				try{
					imitable = (Imitable)Bindeador.getBindeador().ponerADisposicion(this.iPOponente, clave);
				}catch(Exception e){
					Partida.getPartida().mostrarEstado("Fallo en el intento de poner a disposición la clave: '"+ clave + "'. Reintentando...");
					try{Thread.sleep(500);}catch(Exception ex){ex.printStackTrace();}
				}
			}while(!Bindeador.getBindeador().getListo());
			Partida.getPartida().mostrarEstado("Conexión exitosa (clave: '"+ clave + "').");
			this.imitadoresRemotos.put(clave, imitable);
		}
	}
	
	public void bindearImitadores(){
		Iterator iterador = this.clavesImitadores.iterator();
		while (iterador.hasNext()){
			String clave = (String) iterador.next();
			Bindeador.getBindeador().bindear((Imitable)this.imitadores.get(clave), clave);
		}
	}
	
	public void actualizar(){
		Runnable a = new Runnable(){
			public void run(){
				Iterator iterador = clavesImitados.iterator();
				while(iterador.hasNext()){
					String clave = (String) iterador.next();
					Imitable imitador = imitadoresRemotos.get(clave);
					try {
						imitador.imitar(imitables.get(clave));
					} catch (RemoteException ex) {
						Partida.getPartida().stopHilos();
						synchronized(Conexion.getConexion()){
							JOptionPane.showMessageDialog(null, "Se perdió la conexión."); System.exit(-1);
						}
					}
				}
			
			}	
		};
		(new Thread(a, "Hilo imitador")).start();
	}
	
	public void ponerImitador(String clave, Imitable imitador){
		this.imitadores.put(clave, imitador);
		if (!this.clavesImitadores.contains(clave)) this.clavesImitadores.add(clave);
	}
	
	public void ponerImitado(String clave, Imitable imitado){
		this.imitables.put(clave, imitado);
		if (!this.clavesImitados.contains(clave)) this.clavesImitados.add(clave);
	}

	public String leer(String archivo) throws RemoteException, IOException {
		return Copiador.getCopiador().leer(archivo);
	}

	public void copiarDeHostRemoto(String archivoOrigenRemoto, String archivoDestinoLocal) throws IOException {
		Copiador.getCopiador().copiarDeHostRemoto(archivoOrigenRemoto, archivoDestinoLocal);
	}
	
}
