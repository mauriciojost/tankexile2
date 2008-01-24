
package presentacion;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
public interface Legible extends Remote {
	public String leer(String archivo) throws RemoteException, IOException;
}
