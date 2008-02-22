package paquete;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Imitable extends Remote, Serializable{
	public void imitar(Imitable objetoAImitar) throws RemoteException;
	public Object[] getParametros()throws RemoteException;
}
