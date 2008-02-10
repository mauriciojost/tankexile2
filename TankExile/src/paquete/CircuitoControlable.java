package paquete;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CircuitoControlable extends Remote {
	
	
	public void informarChoque(int[] parametrosDelChoque) throws RemoteException;
	public void oponenteLlego() throws RemoteException;
}
