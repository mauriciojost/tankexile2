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

public class Conexion extends Thread implements Legible{
	private Tanque tanquePropio;
	private Controlable tanqueRemotoAControlar;
	private Legible archivosRemotos;
	private String iPOponente;
	
	public void bindearMisArchivos(){
		
		try{
			LocateRegistry.createRegistry(4051);
			Legible stub = (Legible) UnicastRemoteObject.exportObject(this, 0);
			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry(4051);
			registry.bind("Clave archivos", stub);
			System.out.println("Servidor de archivos listo.");
			try{Thread.sleep(3000);}catch(Exception InterruptedException){}
		}catch(Exception e){
			System.err.println("Excepción de servidor: " + e.toString());
			e.printStackTrace();
		}
	}

	public void ponerADisposicionArchivosRemotos() {	
		try {
			Registry registry = LocateRegistry.getRegistry(iPOponente,4051);
			archivosRemotos = (Legible) registry.lookup("Clave archivos");    
			System.out.println("Conexión de cliente exitosa. Archivos a disposición.");
		}catch(Exception e){
			System.err.println("Excepción de cliente: " + e.toString());
			e.printStackTrace();
		}
	}
	
	private Controlable llamarTanqueLejano(){
		try {
			System.out.println("Partida llamando a TankRMI en el otro host (IP:"+iPOponente+"): esperando respuesta...");
			Registry registry = LocateRegistry.getRegistry(iPOponente,4050); // *****
			Controlable retorno = (Controlable) registry.lookup("Clave tanques");
			System.out.println("Conexión de cliente exitosa. Tanque a disposición.");
			return retorno;
		} catch (Exception e) {
			System.err.println("Excepción de cliente: " + e.toString());
			e.printStackTrace();
		}
		return null;
	}
	
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
	
	public void establecerComunicacionTanqueRemoto(){
		this.tanqueRemotoAControlar = llamarTanqueLejano();
	}
	public void setTanquePropio(Tanque tanquePropio){
		this.tanquePropio = tanquePropio;
	}
	public Conexion(String iPOponente){
		this.iPOponente=iPOponente;
	}
	public void run(){
		while(true){
			try {
				manejarTanqueRemoto();
				Thread.sleep(30);
			} catch (InterruptedException ex) {
				Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
	public static Tanque servirTanqueLocalOponente(Tanque tanqueLocalLigadoOponente){	
		try{
			LocateRegistry.createRegistry(4050);
			
			Controlable stub = (Controlable) UnicastRemoteObject.exportObject(tanqueLocalLigadoOponente, tanqueLocalLigadoOponente.getID());
			// Bind the remote object's stub in the registry
		
			Registry registry = LocateRegistry.getRegistry(4050);
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
	
}
