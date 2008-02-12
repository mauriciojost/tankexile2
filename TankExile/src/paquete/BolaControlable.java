package paquete;

import java.rmi.Remote;
import java.rmi.RemoteException;

// Interface usada para comandar las bolas remotamente.
public interface BolaControlable extends Remote {
	
	// Conjunto de métodos que se exige tenga una bola controlable, por comando remoto.
	public void setX(int x)throws RemoteException; // Métodos de posición.
	public void setY(int y)throws RemoteException;
	public void setTodo(int x, int y)throws RemoteException;
	
}
