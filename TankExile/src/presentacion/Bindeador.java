package presentacion;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import paquete.*;

public class Bindeador {
	public static final int PUERTO = 4500; // Puerto al que se asocian todos los registros.
	private static Bindeador bindeador = null;
	
	public static Bindeador getBindeador(){
		if (bindeador==null){
			bindeador = new Bindeador();
		}
		return bindeador;
	}
	private boolean listo;
	private Bindeador(){}
	public boolean getListo(){
		return listo;
	}
	public void bindear(Remote objeto, String clave){
		try{
			LocateRegistry.createRegistry(PUERTO);
		}catch(Exception e){
			System.out.println("Registro sobre el puerto realizado.");
		}
		try{
			Remote stub = UnicastRemoteObject.exportObject(objeto, PUERTO);		
			Registry registry = LocateRegistry.getRegistry(PUERTO);
			registry.rebind(clave, stub);
			System.out.print("Servidor de: '"+ clave +"' listo.\nElementos bindeados: ");
			String lista[] = registry.list();
			for(int i=0; i<lista.length;i++){
				System.out.print(lista[i]+" ");
			}
			System.out.println();
		}catch(Exception e){
			System.err.println("Excepción de servidor de:'"+clave+"'. Reintentando...");
			//e.printStackTrace();
		}
	}
	
	public Remote ponerADisposicion(String clave)throws Exception{
		System.out.println("Poniendo a disposición: '"+clave+"'.");
		Remote retorno;
		listo = false;
		Registry registry = LocateRegistry.getRegistry(Conexion.getConexion().getIP(), PUERTO);
		retorno = (Remote) registry.lookup(clave);    
		listo = true;
		System.out.println("Correcto: '"+clave+"'.");
		return retorno;
	}
	
	public void desbindearTodo(boolean inclusoConexion){
		try {
			Registry registro = LocateRegistry.getRegistry( PUERTO);
			String[] lista = registro.list();
			String clave;
			for(int i=0;i<lista.length;i++){
				clave = lista[i];
				if((!clave.equals("Clave conexion"))||inclusoConexion){
					registro.unbind(clave);
				}
			}
		} catch (Exception ex) {
			System.out.println("Excepción en método desbindearTodo: ");
			ex.printStackTrace();
		}
	}
}
