package presentacion;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VentanaControlable extends Remote {
	public void setInicioHabilitado(boolean habilitada) throws RemoteException;
}
