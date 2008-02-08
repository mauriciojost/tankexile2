package paquete;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CircuitoControlable extends Remote {
	
	
	public void setTodo(int x, int y, int direccion, int movimientoDeTrama, int tramaChoque)throws RemoteException;
	
	
}
