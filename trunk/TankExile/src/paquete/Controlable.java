package paquete;


import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

// Interface relacionada al control de un tanque remotamente.
public interface Controlable extends Remote, Serializable {
	public void choqueResumido() throws RemoteException;
	public void imitar(Imitable tanque) throws RemoteException;
}
