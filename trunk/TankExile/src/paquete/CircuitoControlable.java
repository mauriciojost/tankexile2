package paquete;

import java.rmi.Remote;
import java.rmi.RemoteException;

// Interface usada por conexión para dar correspondencia a los dos circuitos del juego, el local y el remoto.
public interface CircuitoControlable extends Remote {
	// Indica un choque, con sus parámetros.
	public void informarChoque(int[] parametrosDelChoque) throws RemoteException;
	// Indica a un circuito que el oponente (remoto) ha llegado.
	public void oponenteLlego() throws RemoteException;
}
