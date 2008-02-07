package paquete;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BolaControlable extends Remote {
	
	// Conjunto de métodos que se exige tenga un objeto controlable, sea por teclado o por comando remoto.
	public void setX(int x)throws RemoteException; // Métodos de posición.
	public void setY(int y)throws RemoteException;
	public void setTodo(int x, int y)throws RemoteException;
	
}
