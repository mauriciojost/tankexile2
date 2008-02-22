package presentacion;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface Conectable extends Remote {
	public void setClaveOponente(double clave) throws RemoteException;
	public String leer(String archivo) throws RemoteException, IOException;
	public void copiarDeHostRemoto(String archivoOrigenRemoto, String archivoDestinoLocal) throws IOException;
	public String getNickPropio() throws RemoteException;
}
