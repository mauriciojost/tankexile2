package paquete;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Controlable extends Remote {
	
	// Conjunto de métodos que se exige tenga un objeto controlable, sea por teclado o por comando remoto.
	public void setX(int x)throws RemoteException; // Métodos de posición.
	public void setY(int y)throws RemoteException;
	
	public void irArriba()throws RemoteException; // Métodos que indican inicio de un comando.
	public void irAbajo()throws RemoteException;
	public void irIzquierda()throws RemoteException;
	public void irDerecha()throws RemoteException;
	public void noIrArriba()throws RemoteException; // Métodos que indican finalización de un comando.
	public void noIrAbajo()throws RemoteException;	
	public void noIrIzquierda()throws RemoteException;
	
	public void noIrDerecha()throws RemoteException;	
	public void acelerar()throws RemoteException;	
	public void noAcelerar()throws RemoteException;	
	public void setDireccion(int direccion)throws RemoteException; // Método de orientación-dirección.			
	
}
