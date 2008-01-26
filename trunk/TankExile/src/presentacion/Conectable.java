package presentacion;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Conectable extends Remote {
	public void setClaveOponente(double clave) throws RemoteException;
	public void darTurno() throws RemoteException;
}
